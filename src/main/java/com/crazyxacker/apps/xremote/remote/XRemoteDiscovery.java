package com.crazyxacker.apps.xremote.remote;

import com.crazyxacker.apps.xremote.Constants;
import com.crazyxacker.apps.xremote.managers.Settings;
import com.crazyxacker.apps.xremote.managers.XRemoteManager;
import de.mannodermaus.rxbonjour.BonjourBroadcastConfig;
import de.mannodermaus.rxbonjour.RxBonjour;
import de.mannodermaus.rxbonjour.drivers.jmdns.JmDNSDriver;
import de.mannodermaus.rxbonjour.platforms.desktop.DesktopPlatform;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class XRemoteDiscovery {
    private static XRemoteDiscovery INSTANCE;

    private Disposable disposable;

    public static void startService() {
        getInstance().start();
    }

    public static void stopService() {
        if (isServiceStarted()) {
            getInstance().stop();
        }
    }

    public static boolean isServiceStarted() {
        return getInstance().disposable != null;
    }

    private static XRemoteDiscovery getInstance() {
        return Optional.ofNullable(INSTANCE).orElseGet(() -> INSTANCE = new XRemoteDiscovery());
    }

    private XRemoteDiscovery() {
    }

    private void start() {
        BonjourBroadcastConfig broadcastConfig = new BonjourBroadcastConfig(
                "_xremote._tcp",
                Constants.APP_CODENAME,
                null,
                Settings.XRemote.getXRemotePort(),
                createParamsMap()
        );

        disposable = new RxBonjour.Builder()
                .platform(DesktopPlatform.create())
                .driver(JmDNSDriver.create())
                .create()
                .newBroadcast(broadcastConfig)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .subscribe();

        XRemoteManager.log("[INFO] XRemotexDiscovery service started");
    }

    private Map<String, String> createParamsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("app.name", Constants.APP_NAME);
        map.put("app.codename", Constants.APP_CODENAME);
        map.put("app.version", Constants.APP_VERSION);
        map.put("app.version_code", String.valueOf(Constants.APP_VERSION_CODE));
        map.put("app.api_version", String.valueOf(XRemoteServer.API_VERSION));
        return map;
    }

    private void stop() {
        disposable.dispose();
        disposable = null;
        XRemoteManager.log("[INFO] XRemotexDiscovery service stopped");
    }
}
