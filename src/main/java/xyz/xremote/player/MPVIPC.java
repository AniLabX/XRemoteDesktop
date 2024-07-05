package xyz.xremote.player;

import xyz.xremote.helpers.JavaHelper;

import java.util.Optional;

public class MPVIPC {
    private static MPVIPC INSTANCE;
    public static final String IPC_WINDOWS_PIPE_NAME = "\\\\.\\pipe\\tmp\\mpv-xremote-socket";
    public static final String IPC_UNIX_SOCKET_NAME = "/tmp/mpv-xremote-socket";

    private static final String CMD = "cmd.exe";
    private static final String SH = "/bin/sh";

    private static final String CMD_COMMAND_ARG = "/c";
    private static final String SH_COMMAND_ARG = "-c";

    private static final String IPC_BASE_CMD_COMMAND = "echo %s " + (
            JavaHelper.Platform.isWindows()
                    ? ">" + IPC_WINDOWS_PIPE_NAME
                    : "| nc -U " + IPC_UNIX_SOCKET_NAME
    );

    private static final String IPC_BASE_COMMAND = "{ \"command\": [\"set_property\", %s] }";
    private static final String IPC_PLAY_PAUSE_COMMAND = String.format(IPC_BASE_COMMAND, "\"pause\", %s");
    private static final String IPC_VOLUME_COMMAND = String.format(IPC_BASE_COMMAND, "\"volume\", %d");
    private static final String IPC_MUTE_COMMAND = String.format(IPC_BASE_COMMAND, "\"mute\", %s");
    private static final String IPC_SEEK_TO_COMMAND = "seek %d absolute";
    private static final String IPC_SEEK_BY_COMMAND = "seek %d relative";

    public static MPVIPC getInstance() {
        return Optional.ofNullable(INSTANCE).orElseGet(() -> INSTANCE = new MPVIPC());
    }

    private MPVIPC() {
    }

    public boolean play() {
        return executeCommand(wrapCommand(String.format(IPC_PLAY_PAUSE_COMMAND, "false")));
    }

    public boolean pause() {
        return executeCommand(wrapCommand(String.format(IPC_PLAY_PAUSE_COMMAND, "true")));
    }

    public boolean volume(int volume) {
        return executeCommand(wrapCommand(String.format(IPC_VOLUME_COMMAND, volume)));
    }

    public boolean mute(boolean mute) {
        return executeCommand(wrapCommand(String.format(IPC_MUTE_COMMAND, mute)));
    }

    public boolean seekTo(long seekToMillis) {
        return executeCommand(wrapCommand(String.format(IPC_SEEK_TO_COMMAND, seekToMillis / 1000)));
    }

    public boolean seekBy(long seekByMillis) {
        return executeCommand(wrapCommand(String.format(IPC_SEEK_BY_COMMAND, seekByMillis / 1000)));
    }

    private boolean executeCommand(String command) {
        try {
            new ProcessBuilder(new String[]{getShellPath(), getShellCommandArg(), String.format(IPC_BASE_CMD_COMMAND, command)}).start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String wrapCommand(String command) {
        return !JavaHelper.Platform.isWindows()
                ? "'" + command + "'"
                : command;
    }

    private String getShellPath() {
        return JavaHelper.Platform.isWindows() ? CMD : SH;
    }

    private String getShellCommandArg() {
        return JavaHelper.Platform.isWindows() ? CMD_COMMAND_ARG : SH_COMMAND_ARG;
    }
}
