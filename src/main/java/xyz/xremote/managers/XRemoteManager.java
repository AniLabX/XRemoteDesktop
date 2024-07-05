package xyz.xremote.managers;

import xyz.xremote.Constants;
import xyz.xremote.remote.XRemoteDiscovery;
import xyz.xremote.remote.XRemoteServer;
import io.reactivex.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class XRemoteManager {
    private static XRemoteServer server;
    private static Consumer<String> logConsumer;

    public static boolean isServerRunning() {
        return server != null;
    }

    public static void configure(Consumer<String> logConsumer) {
        XRemoteManager.logConsumer = logConsumer;
        if (Settings.XRemote.isStartXRemoteOnLaunch()) {
            XRemoteManager.startServer(Settings.XRemote.getXRemotePort());
        }
    }

    public static void startServer(int port) {
        if (server != null) {
            log("[INFO] Recreating " + Constants.APP_NAME);
            stopServer();
        }

        if (port < 1000 || port > 65536) {
            port = Settings.XRemote.DEFAULT_XREMOTE_PORT;
        }

        server = new XRemoteServer(port);

        startDiscoveryService();

        log("[INFO] " + Constants.APP_NAME + " started with IP = [" + server.getAllIpAddresses() + "] and port = [" + server.getPort() + "]");
    }

    public static void stopServer() {
        if (server != null) {
            server.onDestroy();
            server = null;
        }
        stopDiscoveryService();

        log("[INFO] " + Constants.APP_NAME + " stopped");
    }

    private static void startDiscoveryService() {
        stopDiscoveryService();
        XRemoteDiscovery.startService();
    }

    private static void stopDiscoveryService() {
        XRemoteDiscovery.stopService();
    }

    @Nullable
    public static String getServerIp() {
        return Optional.ofNullable(server)
                .map(XRemoteServer::getIpAddress)
                .orElse(null);
    }

    public static void log(String str) {
        logConsumer.accept(str);
    }
}
