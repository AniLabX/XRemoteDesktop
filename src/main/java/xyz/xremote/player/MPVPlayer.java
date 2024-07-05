package xyz.xremote.player;

import xyz.xremote.Constants;
import xyz.xremote.controller.MainController;
import xyz.xremote.controls.Snackbar;
import xyz.xremote.dialog.DialogBuilder;
import xyz.xremote.dialog.JFXCustomDialog;
import xyz.xremote.helpers.JavaHelper;
import xyz.xremote.helpers.OkHttpHelper;
import xyz.xremote.helpers.SevenZip;
import xyz.xremote.managers.LocaleManager;
import xyz.xremote.managers.Settings;
import xyz.xremote.managers.Workspace;
import xyz.xremote.models.data.ParseLink;
import xyz.xremote.models.player.PlayerInfo;
import xyz.xremote.models.remote.XRemoteData;
import xyz.xremote.models.remote.XRemotePlayData;
import xyz.xremote.utils.FXUtils;
import xyz.xremote.utils.Utils;
import io.reactivex.annotations.Nullable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MPVPlayer {
    public static final String MPV_SITE_URL = "https://mpv.io";
    public static final String MPV_URL = "https://github.com/zhongfly/mpv-winbuild/releases/download/2023-09-07-b47a585/mpv-x86_64-20230907-git-b47a585.7z";

    private static final String MPV_BINARY = "mpv";

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("AV: (.*?) / (.*?) .*");
    private static final String PAUSED_MARK = "(Paused)";

    private static final String IDLE_ARG = "--idle=yes";
    private static final String CACHE_ARG = "--cache=yes";
    private static final String PREFETCH_PLAYLIST_ARG = "--prefetch-playlist=yes";

    private static final String DEMUXER_SEEKABLE_CACHE_ARG = "--demuxer-seekable-cache=yes";
    private static final String DEMUXER_MAX_BYTES_ARG = "--demuxer-max-bytes=2048MiB";
    private static final String DEMUXER_MAX_BACK_BYTES_ARG = "--demuxer-max-back-bytes=1024MiB";
    private static final String DEMUXER_READAHEAD_SECS_ARG = "--demuxer-readahead-secs=1500";
    private static final String DEMUXER_HYSTERESIS_SECS_ARG = "--demuxer-hysteresis-secs=20";

    private static final String IPC_ARG = "--input-ipc-server=";

    private static final String TITLE_ARG = "--title=";
    private static final String SUB_FILE_ARG = "--sub-file=";
    private static final String USER_AGENT_ARG = "--user-agent=";
    private static final String HTTP_HEADER_FIELDS_ARG = "--http-header-fields=";

    private static final String SPEED_ARG = "--speed=";

    private static final String VOLUME_ARG = "--volume=";
    private static final String MUTE_ARG = "--mute=";

    private static final String SUB_FONT_SIZE_ARG = "--sub-font-size=";
    private static final String SUB_BACK_COLOR_ARG = "--sub-back-color=";

    private static final String FULLSCREEN_ARG = "--fullscreen";
    private static final String SCREEN_ARG = "--screen=";
    private static final String FULLSCREEN_SCREEN_ARG = "--fs-screen=";
    private static final String ON_TOP = "--ontop";
    private static final String SAVE_POSITION_ON_QUIT_ARG = "--save-position-on-quit";

    private static String mpvDirectory;
    private static Process mpvProcess;
    private static Thread mpvThread;

    @Getter
    private static XRemotePlayData remotePlayData;

    @Getter
    private static PlayerInfo playerInfo;

    private static final StringProperty mpvVersionProperty = new SimpleStringProperty();
    private static final BooleanProperty isExecutedProperty = new SimpleBooleanProperty(false);

    private static final OkHttpHelper.OnDownloadCallback downloadCallback = new OkHttpHelper.OnDownloadCallback() {
        @Override
        public void onProgress(long count, long total) {
            MainController.updateProgress(count, total, LocaleManager.getStringFormatted("gui.downloading_data", "MPV Player"));
        }

        @Override
        public void onFinish(File _unused) {
            MainController.updateProgress(100, 100, "");
        }
    };

    public static void setMPVDirectory(String directory) {
        mpvDirectory = directory;
        mpvVersionProperty.set(detectMPVVersion());
    }

    public static void download(boolean force) {
        // Download MPV Player
        if (MPVPlayer.getMPVVersion() == null && (!Settings.Player.isSkipMPVInstallation() || force)) {
            if (JavaHelper.Platform.isWindows()) {
                DialogBuilder.create(MainController.getRoot())
                        .withTransition(JFXCustomDialog.DialogTransition.CENTER)
                        .withHeading(LocaleManager.getString("gui.dialog.install_mpv"))
                        .withBody(
                                LocaleManager.getString("gui.dialog.install_mpv.required"),
                                LocaleManager.getString("gui.dialog.install_mpv.select_method")
                        )
                        .withButton(ButtonType.CANCEL, LocaleManager.getString("gui.skip"), dialog -> {
                            Settings.Player.putSkipMPVInstallation(true);
                            dialog.close();
                        })
                        .withButton(ButtonType.APPLY, LocaleManager.getString("gui.manual"), dialog -> {
                            dialog.close();
                            showManualMPVInstallationDialog();
                        })
                        .withButton(ButtonType.APPLY, LocaleManager.getString("gui.automatic"), dialog -> {
                            dialog.close();
                            downloadMPV();
                        })
                        .withMinWidth(500)
                        .withClosable(false)
                        .show();
            } else {
                DialogBuilder builder = DialogBuilder.create(MainController.getRoot())
                        .withTransition(JFXCustomDialog.DialogTransition.CENTER)
                        .withHeading(LocaleManager.getString("gui.dialog.install_mpv"))
                        .withBody(
                                LocaleManager.getString("gui.dialog.install_mpv.required")
                        );

                if (JavaHelper.Platform.isUnix()) {
                    builder.withBody(LocaleManager.getString("gui.dialog.install_mpv.required.linux_debian"))
                            .withBody(createCommandContainer("sudo apt install mpv"))
                            .withBody(LocaleManager.getString("gui.dialog.install_mpv.required.linux_rhel"))
                            .withBody(createCommandContainer("sudo dnf install mpv"))
                            .withBody(LocaleManager.getString("gui.dialog.install_mpv.required.linux_suse"))
                            .withBody(createCommandContainer("sudo zypper install mpv"))
                            .withBody(LocaleManager.getString("gui.dialog.install_mpv.required.linux_arch"))
                            .withBody(createCommandContainer("sudo pacman -S mpv"))
                            .withBody(LocaleManager.getString("gui.dialog.install_mpv.required.other_linux"));
                } else if (JavaHelper.Platform.isMac()) {
                    builder.withBody(LocaleManager.getString("gui.dialog.install_mpv.required.mac_os_brew"))
                            .withBody(createCommandContainer("/bin/bash -c \"$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""))
                            .withBody(LocaleManager.getString("gui.dialog.install_mpv.required.mac_os"))
                            .withBody(createCommandContainer("brew install mpv"))
                            .withBody(LocaleManager.getString("gui.dialog.install_mpv.required.mac_os_other"));
                }

                builder.withButton(ButtonType.CANCEL, LocaleManager.getString("gui.skip"), dialog -> {
                            Settings.Player.putSkipMPVInstallation(true);
                            dialog.close();
                        })
                        .withButton(new ButtonType(""), LocaleManager.getString("gui.dialog.install_mpv.open_site"), dialog -> FXUtils.openUrl(MPV_SITE_URL))
                        .withButton(ButtonType.OK, LocaleManager.getString("gui.dialog.check"), dialog -> {
                            if (checkMPVInstalled()) {
                                dialog.close();
                            }
                        })
                        .withMinWidth(750)
                        .withClosable(false)
                        .show();
            }
        }
    }

    private static void showManualMPVInstallationDialog() {
        DialogBuilder.create(MainController.getRoot())
                .withTransition(JFXCustomDialog.DialogTransition.CENTER)
                .withHeading(LocaleManager.getString("gui.dialog.install_mpv"))
                .withBody(LocaleManager.getString("gui.dialog.install_mpv.manual"))
                .withButton(new ButtonType(""), LocaleManager.getString("gui.dialog.install_mpv.open_site"), dialog -> FXUtils.openUrl(MPV_SITE_URL))
                .withButton(new ButtonType(""), LocaleManager.getString("gui.dialog.install_mpv.open_folder"), dialog -> JavaHelper.openDirectory(Workspace.BIN_DIR))
                .withButton(ButtonType.OK, LocaleManager.getString("gui.dialog.check"), dialog -> {
                    if (checkMPVInstalled()) {
                        dialog.close();
                    }
                })
                .withMinWidth(500)
                .withClosable(false)
                .show();
    }

    private static HBox createCommandContainer(String command) {
        Label lblCommand = new Label(command);
        lblCommand.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblCommand, Priority.ALWAYS);

        FontIcon fiCopy = new FontIcon(MaterialDesignC.CONTENT_COPY);
        fiCopy.setIconColor(Color.WHITE);
        fiCopy.setIconSize(22);

        HBox hbCommand = new HBox(lblCommand, fiCopy);
        hbCommand.setStyle("-fx-background-color: #1D1F21; -fx-background-radius: 6");
        hbCommand.setPadding(new Insets(6));
        hbCommand.setOnMouseClicked(event -> FXUtils.copyToClipboard(lblCommand.getText()));

        return hbCommand;
    }

    private static boolean checkMPVInstalled() {
        String version = MPVPlayer.detectMPVVersion();
        mpvVersionProperty.set(version);

        Snackbar.showSnackBar(
                MainController.getRoot(),
                version == null
                        ? LocaleManager.getString("gui.dialog.install_mpv.not_found")
                        : LocaleManager.getStringFormatted("gui.dialog.install_mpv.found", version),
                version == null
                        ? Snackbar.Type.ERROR
                        : Snackbar.Type.SUCCESS
        );

        return version != null;
    }

    private static void downloadMPV() {
        if (MPVPlayer.getMPVVersion() == null) {
            File mpvDir = new File(mpvDirectory, "MPV");
            mpvDir.mkdirs();
            File tempFile = new File(mpvDir, System.currentTimeMillis() + ".tmp");
            OkHttpHelper.downloadAsync(MPVPlayer.MPV_URL, tempFile, new OkHttpHelper.OnDownloadCallback() {
                @Override
                public void onProgress(long count, long total) {
                    downloadCallback.onProgress(count, total);
                }

                @Override
                public void onFinish(File downloadedFile) {
                    SevenZip.unpack(downloadedFile, mpvDir + File.separator);
                    downloadedFile.deleteOnExit();
                    downloadCallback.onFinish(downloadedFile);
                    mpvVersionProperty.set(detectMPVVersion());
                }
            });
        }
    }

    public static boolean play(XRemoteData remoteData, boolean killIfPresent, PlayerCallback callback) {
        MPVPlayer.remotePlayData = remoteData.getPlayData();
        String title = remotePlayData.getContentTitle() + " - " + remotePlayData.buildEpisodeTitle();
        return play(
                remotePlayData.getVideoUrl().getLinkPartsList().get(0),
                getSubtitlesUrl(remotePlayData.getParseLink()),
                title,
                Constants.USER_AGENT,
                remotePlayData.getParseLink().getHeadersForVideo(),
                remotePlayData.getContentId(),
                remotePlayData.getEpisodeId(),
                killIfPresent,
                callback
        );
    }

    private static boolean play(String url, String subtitleFilePath, String title, String userAgent, Map<String, String> headers,
                                String contentId, String episodeId, boolean killIfPresent, PlayerCallback callback) {
        if (killIfPresent) {
            destroy();
        }

        File mpvExecutable = findMPVExecutable();
        if (isExecuted() || (JavaHelper.Platform.isWindows() && !Utils.isFile(mpvExecutable))) {
            return false;
        }

        mpvThread = new Thread(() -> new MPVPlayer().executeMPV(mpvExecutable, url, subtitleFilePath, title, userAgent, headers, contentId, episodeId, callback));
        mpvThread.start();

        return true;
    }

    public static void destroy() {
        if (mpvThread != null) {
            mpvThread.interrupt();
            mpvThread = null;
        }
        if (mpvProcess != null) {
            mpvProcess.destroy();
            mpvProcess = null;
        }
        isExecutedProperty.set(false);
    }

    private void executeMPV(File pathToMPV, String url, String subtitleFilePath, String title, String userAgent,
                            Map<String, String> headers, String contentId, String episodeId, PlayerCallback callback) {
        playerInfo = new PlayerInfo();
        InputStream stderr = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isExecutedProperty.set(true);

            mpvProcess = new ProcessBuilder(prepareArgs(pathToMPV, url, subtitleFilePath, title, userAgent, headers))
                    .redirectErrorStream(true)
                    .start();

            stderr = mpvProcess.getInputStream();
            isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);

            String line;
            double position = -1;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (line.contains("AV: ")) {
                    boolean success = parseInfo(line, contentId, episodeId);
                    if (success) {
                        double currentPosition = Utils.roundDouble(playerInfo.getPosition(), 3);
                        if (currentPosition != position) {
                            position = currentPosition;
                            callback.onProgressUpdate(playerInfo);
                        }
                    }
                }
            }

            mpvProcess.waitFor();

            callback.onPlayerClose(playerInfo);

            isExecutedProperty.set(false);
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            Utils.closeQuietly(stderr);
            Utils.closeQuietly(isr);
            Utils.closeQuietly(br);
        }
    }

    private List<String> prepareArgs(File pathToMPV, String url, String subtitleFilePath, String title, String userAgent, Map<String, String> headers) {
        final List<String> args = new ArrayList<>();
        args.add(pathToMPV.toString());
        args.add(wrapArg(wrapArg(url)));

        args.add(wrapArg(IDLE_ARG));
        args.add(wrapArg(CACHE_ARG));
        args.add(wrapArg(PREFETCH_PLAYLIST_ARG));

        args.add(wrapArg(DEMUXER_SEEKABLE_CACHE_ARG));
        args.add(wrapArg(DEMUXER_MAX_BYTES_ARG));
        args.add(wrapArg(DEMUXER_MAX_BACK_BYTES_ARG));
        args.add(wrapArg(DEMUXER_READAHEAD_SECS_ARG));
        args.add(wrapArg(DEMUXER_HYSTERESIS_SECS_ARG));

        args.add(wrapArg(IPC_ARG + (JavaHelper.Platform.isWindows() ? MPVIPC.IPC_WINDOWS_PIPE_NAME : MPVIPC.IPC_UNIX_SOCKET_NAME)));

        if (Utils.isNotEmpty(subtitleFilePath)) {
            args.add(wrapArg(SUB_FILE_ARG + subtitleFilePath));
        }
        if (Utils.isNotEmpty(title)) {
            args.add(wrapArg(TITLE_ARG + title));
        }
        if (Utils.isNotEmpty(userAgent)) {
            args.add(wrapArg(USER_AGENT_ARG + userAgent));
        }
        if (Utils.isNotEmpty(headers)) {
            List<String> headerPairs = new ArrayList<>();
            headers.forEach((key, value) -> headerPairs.add(String.format("%s: %s", key, value)));
            args.add(wrapArg(HTTP_HEADER_FIELDS_ARG + Utils.join(",", headerPairs)));
        }

        // Playback
        args.add(wrapArg(SPEED_ARG + Settings.Player.getSpeed()));

        // Volume/Mute
        args.add(wrapArg(VOLUME_ARG + Settings.Player.getVolume()));
        args.add(wrapArg(MUTE_ARG + (Settings.Player.isMute() ? "yes" : "no")));

        // Subtitles
        args.add(wrapArg(SUB_FONT_SIZE_ARG + Settings.Player.getSubtitlesFontSize()));
        args.add(wrapArg(SUB_BACK_COLOR_ARG + Settings.Player.getSubtitlesBackColor()));

        // Other args
        if (Settings.Player.isLaunchInFullscreen()) {
            args.add(wrapArg(FULLSCREEN_ARG));
        }
        if (!Utils.equalsIgnoreCase(Settings.Player.getScreenPosition(), "0")) {
            args.add(wrapArg((Settings.Player.isLaunchInFullscreen() ? FULLSCREEN_SCREEN_ARG : SCREEN_ARG) + Settings.Player.getScreenPosition()));
        }
        if (Settings.Player.isLaunchOnTop()) {
            args.add(wrapArg(ON_TOP));
        }
        args.add(wrapArg(SAVE_POSITION_ON_QUIT_ARG));

        for (String userArgument : Settings.Player.getUserArguments().split(" ")) {
            args.add(wrapArg(userArgument));
        }

        return args;
    }

    private boolean parseInfo(String progressLine, String contentId, String episodeId) {
        Matcher matcher = RESPONSE_PATTERN.matcher(progressLine);
        if (matcher.find()) {
            playerInfo.setContentId(contentId);
            playerInfo.setEpisodeId(episodeId);

            playerInfo.setProgress(convertMPVTimeToMillis(matcher.group(1)));
            playerInfo.setLength(convertMPVTimeToMillis(matcher.group(2)));
            playerInfo.setPosition((float) playerInfo.getProgress() / playerInfo.getLength());

            playerInfo.setVolume(Settings.Player.getVolume());
            playerInfo.setMute(Settings.Player.isMute());

            playerInfo.setPlaying(!Utils.containsIgnoreCase(progressLine, PAUSED_MARK));

            return true;
        }
        return false;
    }

    private long convertMPVTimeToMillis(String timeStr) {
        List<String> lengthValues = Utils.splitString(timeStr, ":");
        return Utils.getIntDef(lengthValues.get(0), 0) * 60 * 60 * 1000L
                + Utils.getIntDef(lengthValues.get(1), 0) * 60 * 1000L
                + Utils.getIntDef(lengthValues.get(2), 0) * 1000L;
    }

    private static String detectMPVVersion() {
        if (JavaHelper.Platform.isWindows()) {
            return Optional.ofNullable(findMPVExecutable())
                    .map(File::toString)
                    .map(MPVPlayer::mapToVersion)
                    .orElse(null);
        } else {
            return mapToVersion(MPV_BINARY);
        }
    }

    public static File findMPVExecutable() {
        if (JavaHelper.Platform.isWindows()) {
            if (mpvDirectory == null) {
                throw new IllegalArgumentException("MPV dir not set!");
            }
            return Utils.getAllFilesFromDirectory(mpvDirectory, new String[]{"exe"}, true)
                    .stream()
                    .filter(file -> Utils.equalsIgnoreCase(Utils.getFileName(file.getName()), MPV_BINARY))
                    .findFirst()
                    .orElse(null);
        } else {
            return new File(MPV_BINARY);
        }
    }

    private static String getSubtitlesUrl(ParseLink parseLink) {
        return Optional.ofNullable(parseLink)
                .map(ParseLink::getSubtitles)
                .map(subtitles -> subtitles.getSubtitlesByType(Settings.Player.getPrioritySubtitleType()))
                .filter(Utils::isNotEmpty)
                .orElse(null);
    }

    private static String wrapArg(String arg) {
        if (JavaHelper.Platform.isWindows()) {
            return "\"" + arg + "\"";
        }
        return arg;
    }

    @Nullable
    private static String mapToVersion(String executableOrCommand) {
        try {
            Process process = new ProcessBuilder(executableOrCommand, wrapArg("--version"))
                    .redirectErrorStream(true)
                    .start();

            InputStream stderr = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);

            String versionLine = br.readLine();

            process.destroy();

            return versionLine.replaceAll("mpv (.*?) Copyright.*", "$1");
        } catch (final IOException e) {
            return null;
        }
    }

    // Properties
    public static String getMPVVersion() {
        return mpvVersionProperty.get();
    }

    public static StringProperty mpvVersionProperty() {
        return mpvVersionProperty;
    }

    public static boolean isExecuted() {
        return isExecutedProperty.get();
    }

    public static BooleanProperty isExecutedProperty() {
        return isExecutedProperty;
    }
}
