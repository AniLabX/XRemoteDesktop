package com.crazyxacker.apps.xremote.helpers;

import java.io.IOException;

public class JavaHelper {

    private static final Platform CURRENT_PLATFORM = detectPlatform();

    public enum Platform {
        WINDOWS("win"),
        LINUX("unix"),
        MACOS("mac");

        private final String codename;

        Platform(String codename) {
            this.codename = codename;
        }

        public static String getCodename() {
            return CURRENT_PLATFORM.codename;
        }

        public static boolean isWindows() {
            return CURRENT_PLATFORM == Platform.WINDOWS;
        }

        public static boolean isMac() {
            return CURRENT_PLATFORM == Platform.MACOS;
        }

        public static boolean isUnix() {
            return CURRENT_PLATFORM == Platform.LINUX;
        }
    }

    public static boolean inNativeImage() {
        try {
            return org.graalvm.nativeimage.ImageInfo.inImageCode();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Platform detectPlatform() {
        String osName = System.getProperty("os.name");

        try {
            if (inNativeImage()) {
                osName = org.graalvm.nativeimage.ImageSingletons.lookup(org.graalvm.nativeimage.Platform.class).getOS();
            }
        } catch (Throwable ignored) {
        }

        if (isWindows(osName)) {
            return Platform.WINDOWS;
        } else if (isUnix(osName)) {
            return Platform.LINUX;
        } else if (isMac(osName)) {
            return Platform.MACOS;
        }
        throw new RuntimeException("Unsupported platform!");
    }

    private static boolean isWindows(String osName) {
        return osName.toLowerCase().contains("win") && !isMac(osName);
    }

    private static boolean isMac(String osName) {
        String os = osName.toLowerCase();
        return os.contains("mac") || os.contains("darwin");
    }

    private static boolean isUnix(String osName) {
        String os = osName.toLowerCase();
        return os.contains("nix") || os.contains("nux");
    }

    public static void openDirectory(String dir) {
        Runtime rt = Runtime.getRuntime();
        try {
            if (JavaHelper.Platform.isWindows()) {
                rt.exec("explorer " + dir).waitFor();
            } else if (JavaHelper.Platform.isMac()) {
                rt.exec("open -R " + dir).waitFor();
            } else if (JavaHelper.Platform.isUnix()) {
                rt.exec("xdg-open " + dir).waitFor();
            } else {
                try {
                    throw new IllegalStateException();
                } catch (IllegalStateException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
