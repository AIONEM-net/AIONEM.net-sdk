package aionem.net.sdk.core.utils;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;


@Log4j2
public class UtilsText {


    public static boolean isEmpty(final CharSequence value) {
        return value == null || value.length() == 0;
    }

    public static String notEmpty(final Object object, final String holder) {
        return toString(object, holder);
    }

    public static String notEmpty(final Object object, final String holder1, final String holder2) {
        return toString(object, holder1, holder2);
    }

    public static String notEmptyUse(final Object object, final String value) {
        return !isEmpty(toString(object, "")) ? value : "";
    }

    public static String notEmptyUse(final Object object, final String useValue, final String elseValue) {
        return !isEmpty(toString(object, "")) ? useValue : elseValue;
    }

    public static String notNull(final Object object) {
        return notNull(object, "");
    }

    public static String notNull(final Object object, final String holder) {
        return object != null ? toString(object) : toString(holder);
    }

    public static String toString(final Object object, final String defaultValue) {
        return toString(object, defaultValue, "");
    }

    public static String toString(final Object object, final String default1, final String default2) {
        final String defaultValue = !isEmpty(default1) ? default1 : default2;
        if(object == null) return defaultValue;
        String value = UtilsText.toString(object);
        if(isEmpty(value) || value.equalsIgnoreCase("null")) {
            value = defaultValue;
        }
        return value;
    }

    public static String toString(final Object object) {
        return toString(object, true);
    }

    public static String toString(final Object object, final boolean isLine) {
        if(object == null) return null;
        String value = object.toString();
        try {

            if(!(object instanceof String || object instanceof Character || object instanceof StringBuilder ||
                    object instanceof Integer || object instanceof Long || object instanceof Double || object instanceof Boolean)) {

                if(object instanceof Path) {
                    value = toString(((Path) object).toFile());
                }else if(object instanceof File) {
                    final File file = (File) object;
                    value = file.exists() && file.isFile() ? toString(new FileInputStream(file)) : "";
                }else if(object instanceof HttpURLConnection) {
                    value = toString(((HttpURLConnection) object).getInputStream());
                }else if(object instanceof InputStream) {
                    value = toString(new BufferedReader(new InputStreamReader((InputStream) object, StandardCharsets.UTF_8)));
                }else if(object instanceof BufferedReader) {
                    final StringBuilder response = new StringBuilder();
                    final BufferedReader bufferedReader = (BufferedReader) object;
                    String line;
                    int i = 0;
                    while((line = bufferedReader.readLine()) != null) {
                        response.append(i > 0 && isLine ? "\n" : "").append(line);
                        i++;
                    }
                    value = response.toString();
                    bufferedReader.close();
                }else if(object.getClass().isArray()) {
                    if(Array.getLength(object) == 0) {
                        value = null;
                    }else {
                        value = toString(Array.get(object, 0));
                    }
                }else if(object instanceof Calendar) {
                    value = UtilsConverter.Converter.DateUtils.calendarToString((Calendar) object);
                }else if(object instanceof Date) {
                    value = UtilsConverter.Converter.DateUtils.dateToString((Date) object);
                }else {
                    value = object.toString();
                }

            }
        }catch(Exception ignore) {
        }
        return value;
    }

    public static boolean equals(final String text1, final String text2) {
        return text1 != null && text1.equals(text2);
    }

    public static boolean equalsIgnoreCase(final String text1, final String text2) {
        return text1 != null && text1.equalsIgnoreCase(text2);
    }

    public static boolean contains(final String text, final String keyword) {
        return text != null && text.contains(keyword);
    }

    public static boolean startsWith(final String text, final String keyword) {
        return text != null && text.startsWith(keyword);
    }

    public static boolean endsWith(final String text, final String keyword) {
        return text != null && text.endsWith(keyword);
    }

    public static String substring(final String text, final int beginIndex) {
        return text != null ? text.substring(beginIndex) : null;
    }

    public static String substring(final String text, final int beginIndex, final int endIndex) {
        return text != null ? text.substring(beginIndex, endIndex) : null;
    }

    public static int indexOf(final String text, final String keyword) {
        return text != null ? text.indexOf(keyword) : -1;
    }

    public static int lastIndexOf(final String text, final String keyword) {
        return text != null ? text.lastIndexOf(keyword) : -1;
    }

    public static int compareTo(final String text1, final String text2) {
        if(text1 == null && text2 == null) return 0;
        if(text1 == null) return -1;
        if(text2 == null) return 1;
        return text1.compareTo(text2);
    }

    public static int compareToIgnoreCase(final String text1, final String text2) {
        if(text1 == null && text2 == null) return 0;
        if(text1 == null) return -1;
        if(text2 == null) return 1;
        return text1.compareToIgnoreCase(text2);
    }

    public static String capitalizeFirstLetter(final String text) {
        return !isEmpty(text) ? Character.toUpperCase(text.charAt(0)) + text.substring(1) : text;
    }

    public static String join(final String... texts) {
        return join(texts, "");
    }

    public static String join(final String[] texts, final String separator) {
        if(texts == null) return null;
        if(texts.length == 1) return texts[0];
        if(texts.length == 2) return texts[0] +separator+ texts[1];

        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < texts.length; i++) {
            final String text = texts[i];
            if(i > 0 && i < texts.length - 1) {
                stringBuilder.append(text).append(separator);
            }else {
                stringBuilder.append(text);
            }
        }
        return stringBuilder.toString();
    }

}
