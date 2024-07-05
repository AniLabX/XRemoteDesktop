package xyz.xremote.managers;

import xyz.xremote.utils.Utils;

import java.io.File;

public class Workspace {
    public static final String BIN_DIR = getWorkingDir() + File.separator + "bin" + File.separator;

    public static void configureWorkspace() {
        File binDir = new File(BIN_DIR);
        if (!Utils.isDirectory(binDir)) {
            binDir.mkdirs();
        }
    }

    private static String getWorkingDir() {
        return new File(System.getProperty("user.dir")).toString();
    }
}
