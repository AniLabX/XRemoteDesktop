package xyz.xremote.managers;

import xyz.xremote.Constants;
import xyz.xremote.models.data.Subtitles;
import xyz.xremote.utils.Utils;

import java.util.prefs.Preferences;

public class Settings {
    private static final Preferences preferences = Preferences.userRoot().node("XRemote");

    private static <E extends Enum<E>> E getEnum(Class<E> clazz, String key, E def) {
        return Utils.valueOf(clazz, preferences.get(key, null), def);
    }

    private static <E extends Enum<E>> void putEnum(String key, E value) {
        preferences.put(key, value.name());
    }

    private static final String KEY_APP_LANGUAGE = "app_language";
    private static final String DEFAULT_APP_LANGUAGE = "";

    public static String getDefaultAppLanguageCode() {
        return preferences.get(KEY_APP_LANGUAGE, DEFAULT_APP_LANGUAGE);
    }

    public static void putDefaultAppLanguageCode(String value) {
        preferences.put(KEY_APP_LANGUAGE, value);
    }

    public static class XRemote {
        private static final String KEY_APP_NAME = "xremote_app_name";
        private static final String DEFAULT_APP_NAME = Constants.APP_NAME;

        public static String getAppName() {
            return preferences.get(KEY_APP_NAME, DEFAULT_APP_NAME);
        }

        public static void putAppName(String value) {
            preferences.put(KEY_APP_NAME, value);
        }

        private static final String KEY_START_XREMOTE_ON_LAUNCH = "start_xremote_on_launch";
        private static final boolean DEFAULT_START_XREMOTE_ON_LAUNCH = false;

        public static boolean isStartXRemoteOnLaunch() {
            return preferences.getBoolean(KEY_START_XREMOTE_ON_LAUNCH, DEFAULT_START_XREMOTE_ON_LAUNCH);
        }

        public static void putStartXRemoteOnLaunch(boolean value) {
            preferences.putBoolean(KEY_START_XREMOTE_ON_LAUNCH, value);
        }

        private static final String KEY_XREMOTE_PORT = "xremote_port";
        public static final int DEFAULT_XREMOTE_PORT = 31337;

        public static int getXRemotePort() {
            return preferences.getInt(KEY_XREMOTE_PORT, DEFAULT_XREMOTE_PORT);
        }

        public static void putXRemotePort(int value) {
            preferences.putInt(KEY_XREMOTE_PORT, value);
        }
    }

    public static class Player {
        public static final String KEY_SKIP_MPV_INSTALLATION = "player_skip_mpv_installation";
        private static final boolean DEFAULT_SKIP_MPV_INSTALLATION = false;

        public static boolean isSkipMPVInstallation() {
            return preferences.getBoolean(KEY_SKIP_MPV_INSTALLATION, DEFAULT_SKIP_MPV_INSTALLATION);
        }

        public static void putSkipMPVInstallation(boolean value) {
            preferences.putBoolean(KEY_SKIP_MPV_INSTALLATION, value);
        }

        public static final String KEY_LAUNCH_IN_FULLSCREEN = "player_launch_in_fullscreen";
        private static final boolean DEFAULT_LAUNCH_IN_FULLSCREEN = true;

        public static boolean isLaunchInFullscreen() {
            return preferences.getBoolean(KEY_LAUNCH_IN_FULLSCREEN, DEFAULT_LAUNCH_IN_FULLSCREEN);
        }

        public static void putLaunchInFullscreen(boolean value) {
            preferences.putBoolean(KEY_LAUNCH_IN_FULLSCREEN, value);
        }

        public static final String KEY_SCREEN_POSITION = "player_screen_position";
        private static final String DEFAULT_SCREEN_POSITION = "0";

        public static String getScreenPosition() {
            return preferences.get(KEY_SCREEN_POSITION, DEFAULT_SCREEN_POSITION);
        }

        public static void putScreenPosition(String value) {
            preferences.put(KEY_SCREEN_POSITION, value);
        }

        public static final String KEY_LAUNCH_ON_TOP = "player_launch_on_top";
        private static final boolean DEFAULT_LAUNCH_ON_TOP = false;

        public static boolean isLaunchOnTop() {
            return preferences.getBoolean(KEY_LAUNCH_ON_TOP, DEFAULT_LAUNCH_ON_TOP);
        }

        public static void putLaunchOnTop(boolean value) {
            preferences.putBoolean(KEY_LAUNCH_ON_TOP, value);
        }

        public static final String KEY_SPEED = "player_speed";
        private static final String DEFAULT_SPEED = "1";

        public static String getSpeed() {
            return preferences.get(KEY_SPEED, DEFAULT_SPEED);
        }

        public static void putSpeed(String value) {
            preferences.put(KEY_SPEED, value);
        }

        public static final String KEY_VOLUME = "player_volume";
        private static final int DEFAULT_VOLUME = 100;

        public static int getVolume() {
            return preferences.getInt(KEY_VOLUME, DEFAULT_VOLUME);
        }

        public static void putVolume(int value) {
            preferences.putInt(KEY_VOLUME, value);
        }

        public static final String KEY_MUTE = "player_mute";
        private static final boolean DEFAULT_MUTE = false;

        public static boolean isMute() {
            return preferences.getBoolean(KEY_MUTE, DEFAULT_MUTE);
        }

        public static void putMute(boolean value) {
            preferences.putBoolean(KEY_MUTE, value);
        }

        public static final String KEY_PRIORITY_SUBTITLE_TYPE = "player_priority_subtitle_type";
        private static final Subtitles.SubtitleType DEFAULT_PRIORITY_SUBTITLE_TYPE = Subtitles.SubtitleType.ANY;

        public static Subtitles.SubtitleType getPrioritySubtitleType() {
            return getEnum(Subtitles.SubtitleType.class, KEY_PRIORITY_SUBTITLE_TYPE, DEFAULT_PRIORITY_SUBTITLE_TYPE);
        }

        public static void putPrioritySubtitleType(Subtitles.SubtitleType value) {
            putEnum(KEY_PRIORITY_SUBTITLE_TYPE, value);
        }

        public static final String KEY_SUBTITLES_FONT_SIZE = "player_subtitles_font_size";
        private static final String DEFAULT_SUBTITLES_FONT_SIZE = "55";

        public static String getSubtitlesFontSize() {
            return preferences.get(KEY_SUBTITLES_FONT_SIZE, DEFAULT_SUBTITLES_FONT_SIZE);
        }

        public static void putSubtitlesFontSize(String value) {
            preferences.put(KEY_SUBTITLES_FONT_SIZE, value);
        }

        public static final String KEY_SUBTITLES_BACK_COLOR = "player_subtitles_back_color";
        private static final String DEFAULT_SUBTITLES_BACK_COLOR = "#00000000";

        public static String getSubtitlesBackColor() {
            return preferences.get(KEY_SUBTITLES_BACK_COLOR, DEFAULT_SUBTITLES_BACK_COLOR);
        }

        public static void putSubtitlesBackColor(String value) {
            preferences.put(KEY_SUBTITLES_BACK_COLOR, value);
        }

        public static final String KEY_USER_ARGUMENTS = "player_user_arguments";
        private static final String DEFAULT_USER_ARGUMENTS = "";

        public static String getUserArguments() {
            return preferences.get(KEY_USER_ARGUMENTS, DEFAULT_USER_ARGUMENTS);
        }

        public static void putUserArguments(String value) {
            preferences.put(KEY_USER_ARGUMENTS, value);
        }
    }
}
