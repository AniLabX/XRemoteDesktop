package xyz.xremote.remote;

import xyz.xremote.Constants;
import xyz.xremote.controller.MainController;
import xyz.xremote.controls.Snackbar;
import xyz.xremote.managers.LocaleManager;
import xyz.xremote.managers.Settings;
import xyz.xremote.managers.XRemoteManager;
import xyz.xremote.models.data.ParseLink;
import xyz.xremote.models.player.PlayerInfo;
import xyz.xremote.models.remote.XRemoteData;
import xyz.xremote.models.remote.XRemotePlayData;
import xyz.xremote.models.remote.XRemotePlayInfo;
import xyz.xremote.models.remote.XRemoteResponseData;
import xyz.xremote.player.MPVIPC;
import xyz.xremote.player.MPVPlayer;
import xyz.xremote.player.PlayerCallback;
import xyz.xremote.utils.NetworkUtils;
import xyz.xremote.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.github.palexdev.materialfx.utils.ExceptionUtils;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class XRemoteServer {
    public static final int API_VERSION = 10;

    private ServerSocket serverSocket;
    private Socket socket;
    private final int socketPort;
    private final Gson gson;
    private final Gson prettyGson;

    public XRemoteServer(int port) {
        socketPort = port;
        gson = new Gson();
        prettyGson = new GsonBuilder().setPrettyPrinting().create();

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketPort;
    }

    public void onDestroy() {
        Optional.ofNullable(serverSocket).ifPresent(Utils::closeQuietly);
    }

    private class SocketServerThread extends Thread {
        @Override
        public void run() {
            try {
                // create ServerSocket using specified port
                serverSocket = new ServerSocket(socketPort);

                //Server is running always. This is done using this while(true) loop
                while (!serverSocket.isClosed()) {
                    listenSocket();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof BindException) {
                    XRemoteManager.log("[ERROR] Unable to start server! Reason: " + ExceptionUtils.getStackTraceString(e));
                }
            }
        }
    }

    private void listenSocket() {
        try {
            //Reading the message from the client
            socket = serverSocket.accept();

            // Wait client message and read it
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();

            XRemoteData remoteData = null;
            try {
                remoteData = prettyGson.fromJson(message, XRemoteData.class);
                XRemoteManager.log("[INFO] XRemotexServer@Receive: Message received from client:\n" + prettyGson.toJson(remoteData));
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
                XRemoteManager.log("[ERROR] XRemotexServer@Receive: Message received from client is broken: " + message + ". Reason: " + ExceptionUtils.getStackTraceString(ex));
            }

            XRemoteResponseData remoteResponseData;
            if (remoteData != null && remoteData.getApiVersion() >= XRemoteData.XREMOTE_API_VERSION) {
                remoteResponseData = switch (remoteData.getCommand()) {
                    case PING, HEARTBEAT -> new XRemoteResponseData(XRemoteResponse.ALIVE);
                    case GET_PLAY_INFO -> onGetPlayInfo();
                    case PLAY_VIDEO -> onPlayVideo(remoteData);
                    case CLOSE_PLAYER -> onPlayerClose();
                    case UPDATE_STATE -> onUpdateState(remoteData);
                };
            } else {
                remoteResponseData = new XRemoteResponseData(
                        remoteData == null
                                ? XRemoteResponse.EXCEPTION
                                : XRemoteResponse.VERSION_INCOMPATIBLE
                );
            }

            new SocketServerReplyThread(gson.toJson(remoteResponseData), prettyGson.toJson(remoteResponseData)).start();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private XRemoteResponseData onGetPlayInfo() {
        XRemotePlayData currentRemoteData = MPVPlayer.getRemotePlayData();
        if (MPVPlayer.isExecuted() && currentRemoteData != null) {
            PlayerInfo playerInfo = MPVPlayer.getPlayerInfo();

            XRemotePlayInfo playInfo = new XRemotePlayInfo();
            playInfo.setTitle(currentRemoteData.getContentTitle());
            playInfo.setSubtitle(currentRemoteData.buildEpisodeTitle().toString());
            playInfo.setPosition(playerInfo.getProgress());
            playInfo.setDuration(playerInfo.getLength());
            playInfo.setVolumeLevel(playerInfo.getVolume());
            playInfo.setMuted(playerInfo.isMute());
            playInfo.setPlaying(playerInfo.isPlaying());
            playInfo.setResizeMode(-1); // Not supported
            playInfo.setWindow(0); // Not supported

            playInfo.setSubsAvailable(
                    Optional.ofNullable(currentRemoteData.getParseLink())
                            .map(ParseLink::getSubtitles)
                            .isPresent()
            );

            return new XRemoteResponseData(XRemoteResponse.COMMAND_SUCCESS, playInfo);
        } else {
            return new XRemoteResponseData(XRemoteResponse.PLAYER_NOT_RUNNING);
        }
    }

    private XRemoteResponseData onPlayVideo(XRemoteData remoteData) {
        if (remoteData.getPlayData().getParseLink() != null) {
            boolean success = MPVPlayer.play(
                    remoteData,
                    true,
                    new PlayerCallback() {
                        @Override
                        public void onProgressUpdate(PlayerInfo playerInfo) {
                            // Stub
                        }

                        @Override
                        public void onPlayerClose(PlayerInfo playerInfo) {
                            // Stub
                        }
                    }
            );

            if (!success && !MPVPlayer.isExecuted()) {
                Snackbar.showSnackBar(
                        MainController.getRoot(),
                        LocaleManager.getString("gui.error.mpv_not_found"),
                        Snackbar.Type.ERROR
                );
            }
            return new XRemoteResponseData(XRemoteResponse.COMMAND_SUCCESS);
        }
        return new XRemoteResponseData(XRemoteResponse.COMMAND_FAIL);
    }

    private XRemoteResponseData onPlayerClose() {
        MPVPlayer.destroy();
        return new XRemoteResponseData(XRemoteResponse.COMMAND_SUCCESS);
    }

    private XRemoteResponseData onUpdateState(XRemoteData remoteData) {
        XRemotePlayCommand playCommand = remoteData.getPlayCommand();
        XRemotePlayInfo playInfo = remoteData.getPlayInfo();

        if (playCommand != XRemotePlayCommand.NONE && playInfo != null && MPVPlayer.isExecuted()) {
            MPVIPC mpvipc = MPVIPC.getInstance();
            switch (playCommand) {
                case PLAY -> {
                    if (playInfo.isPlaying()) {
                        mpvipc.play();
                    } else {
                        mpvipc.pause();
                    }
                }
                case MUTE -> {
                    boolean isMute = !MPVPlayer.getPlayerInfo().isMute();
                    Settings.Player.putMute(isMute);
                    mpvipc.mute(isMute);
                }
                case SEEK_TO -> mpvipc.seekTo(playInfo.getPosition());
                case SEEK_BY -> mpvipc.seekBy(playInfo.getSeekByMillis());
                case VOLUME_UP -> {
                    int volume = Math.min(MPVPlayer.getPlayerInfo().getVolume() + 5, 130);
                    Settings.Player.putVolume(volume);
                    mpvipc.volume(volume);
                }
                case VOLUME_DOWN -> {
                    int volume = Math.max(MPVPlayer.getPlayerInfo().getVolume() - 5, 0);
                    Settings.Player.putVolume(volume);
                    mpvipc.volume(volume);
                }
            }
            return new XRemoteResponseData(XRemoteResponse.COMMAND_SUCCESS);
        }
        return new XRemoteResponseData(XRemoteResponse.COMMAND_FAIL);
    }

    public String getIpAddress() {
        return Optional.of(NetworkUtils.getLocalIPs())
                .filter(Utils::isNotEmpty)
                .map(list -> list.get(0))
                .orElse("");
    }

    public String getAllIpAddresses() {
        return Optional.of(NetworkUtils.getLocalIPs())
                .filter(Utils::isNotEmpty)
                .map(list -> Utils.join(" | ", list))
                .orElse("");
    }

    private class SocketServerReplyThread extends Thread {
        private final String message;
        private final String prettyMessage;

        SocketServerReplyThread(String message, String prettyMessage) {
            this.message = message;
            this.prettyMessage = prettyMessage;
        }

        @Override
        public void run() {
            try {
                //Sending the response back to the client.
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(message + "\n");
                bw.flush();

                XRemoteManager.log("[INFO] " + Constants.APP_NAME + "@Response: Message sent as response:\n" + prettyMessage);
            } catch (IOException e) {
                e.printStackTrace();
                XRemoteManager.log("[ERROR] " + Constants.APP_NAME + "@Error: " + ExceptionUtils.getStackTraceString(e) + "\n");
            }
        }
    }
}