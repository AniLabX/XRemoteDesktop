package xyz.xremote.utils;

import net.greypanther.natsort.CaseInsensitiveSimpleNaturalComparator;
import org.apache.commons.io.FileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Utils {
    private static final String QR_GEN_URL = "https://quickchart.io/chart?cht=qr&chs=300&choe=UTF-8&chl=";

    public static String createQRCodeUrl(String data) {
        return QR_GEN_URL + data;
    }

    public static String getSystemLanguageCode() {
        return Locale.getDefault().getLanguage();
    }

    public static boolean isCurrentSystemLocaleIsRussian() {
        String systemLanguageCode = getSystemLanguageCode();
        return systemLanguageCode.equalsIgnoreCase("ru");
    }

    public static boolean isCurrentSystemLocaleIsUkrainian() {
        String systemLanguageCode = getSystemLanguageCode();
        return systemLanguageCode.equalsIgnoreCase("uk");
    }

    public static void safeRun(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static List<File> getAllFilesFromDirectory(String path, String[] allowedExtensions, boolean recursive) {
        File file = new File(path);
        if (file.isDirectory()) {
            List<File> files = (List<File>) FileUtils.listFiles(file, allowedExtensions, recursive);
            files.sort((file1, file2) -> CaseInsensitiveSimpleNaturalComparator.getInstance().compare(file1.getPath(), file2.getPath()));
            return files;
        }

        return new ArrayList<>();
    }

    public static String getPath(String path) {
        int indexSlash = path.lastIndexOf("\\");
        int indexBackSlash = path.lastIndexOf("/");

        if (indexSlash >= 0) {
            return path.substring(0, indexSlash);
        } else if (indexBackSlash >= 0) {
            return path.substring(0, indexBackSlash);
        }

        return "";
    }

    public static String getFileNameWithExt(String path) {
        int indexSlash = path.lastIndexOf(File.separator);
        return indexSlash >= 0 ? path.substring(indexSlash + 1) : path;
    }

    public static String join(String delimiter, Iterable<?> tokens) {
        if (isEmpty(tokens)) {
            return "";
        }

        final Iterator<?> it = tokens.iterator();
        if (!it.hasNext()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(it.next());
        while (it.hasNext()) {
            sb.append(delimiter);
            sb.append(it.next());
        }
        return sb.toString();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(Object collectionMapArray) {
        return !isEmpty(collectionMapArray);
    }

    public static boolean isEmpty(Object collectionMapArray, Integer... lengthArr) {
        int length = lengthArr != null && lengthArr.length > 0 ? lengthArr[0] : 1;
        if (collectionMapArray == null) {
            return true;
        } else if (collectionMapArray instanceof Collection) {
            return ((Collection<?>) collectionMapArray).size() < length;
        } else if (collectionMapArray instanceof Map) {
            return ((Map<?, ?>) collectionMapArray).size() < length;
        } else if (collectionMapArray instanceof Object[]) {
            return ((Object[]) collectionMapArray).length < length || ((Object[]) collectionMapArray)[length - 1] == null;
        } else return true;
    }

    public static List<String> splitString(String str, String regex) {
        try {
            return new ArrayList<>(Arrays.asList(str.split(regex)));
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public static boolean containsIgnoreCase(String firstValue, String secondValue) {
        return isNotEmpty(firstValue) && isNotEmpty(secondValue) && firstValue.toLowerCase().contains(secondValue.toLowerCase());
    }

    public static boolean equalsIgnoreCase(String s1, String s2) {
        if (s1 == null) {
            s1 = "";
        }
        return s1.equalsIgnoreCase(s2);
    }

    public static String getFileName(String path) {
        int indexSlash = path.lastIndexOf(File.separator);
        int indexDot = path.lastIndexOf(".");

        if (indexSlash >= 0 && indexDot > indexSlash) {
            return path.substring(indexSlash + 1, indexDot);
        }
        if (indexSlash >= 0) {
            return path.substring(indexSlash + 1);
        }
        if (indexDot >= 0) {
            return path.substring(0, indexDot);
        }

        return path;
    }

    public static int getIntDef(String value, int def) {
        if (value == null) {
            return def;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    public static boolean getBoolDef(String value, boolean def) {
        if (value == null) {
            return def;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    public static <E extends Enum<E>> E valueOf(Class<E> classE, String name) {
        return valueOf(classE, name, classE.getEnumConstants()[0]);
    }

    public static <E extends Enum<E>> E valueOf(Class<E> classE, String name, E def) {
        E[] values = classE.getEnumConstants();
        for (E value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return def;
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String md5Hex(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NullPointerException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static boolean isFileExist(File file) {
        return file != null && file.exists();
    }

    public static boolean isDirectory(File file) {
        return isFileExist(file) && file.isDirectory();
    }

    public static boolean isFile(File file) {
        return isFileExist(file) && file.isFile();
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
                // ignore
            }
        }
    }
}
