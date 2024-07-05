package xyz.xremote.managers;

import xyz.xremote.utils.Utils;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class LocaleManager {
    private static ResourceBundle resourceBundle;

    public static ResourceBundle getResourceBundle() {
        return Optional.ofNullable(resourceBundle).orElseGet(() -> resourceBundle = loadResourceBundle());
    }

    private static ResourceBundle loadResourceBundle() {
        String langCode = Settings.getDefaultAppLanguageCode();
        if (Utils.isNotEmpty(langCode)) {
            Locale.setDefault(new Locale(langCode));
        }
        return ResourceBundle.getBundle("strings", java.util.Locale.getDefault());
    }


    public static String getString(String key) {
        return getString(key, "placeholder");
    }

    public static String getString(String key, String def) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            return def;
        }
    }

    public static String getString(String key, Object... formatArgs) {
        return String.format(getString(key), formatArgs);
    }

    public static String getStringFormatted(String key, Object... formatArgs) {
        return getString(key, formatArgs);
    }
}
