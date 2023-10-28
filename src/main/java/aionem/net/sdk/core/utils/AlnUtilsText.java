package aionem.net.sdk.core.utils;

import aionem.net.sdk.core.data.AlnData;
import aionem.net.sdk.core.api.AlnDaoRes;
import aionem.net.sdk.core.data.AlnDatas;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j2
public class AlnUtilsText {

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

    public static String notEmptyUseElse(final Object object, final String useValue, final String elseValue) {
        return !isEmpty(toString(object, "")) ? useValue : elseValue;
    }

    public static String toString(final Object object, final String defaultValue) {
        return toString(object, defaultValue, "");
    }

    public static String toString(final Object object, final String default1, final String default2) {
        final String defaultValue = !isEmpty(default1) ? default1 : default2;
        if(object == null) return defaultValue;
        String value = AlnUtilsText.toString(object);
        if(isEmpty(value) || value.equalsIgnoreCase("null")) {
            value = defaultValue;
        }
        return value;
    }

    public static String toString(final Object object) {
        if(object == null) return null;
        String value = object.toString();
        try {

            if(!(object instanceof String || object instanceof Character || object instanceof StringBuilder ||
                    object instanceof Integer || object instanceof Long || object instanceof Double || object instanceof Boolean)) {

                if(object instanceof JsonElement) {
                    value = ((JsonElement) object).getAsString();
                }else if(object instanceof AlnDaoRes) {
                    value = ((AlnDaoRes) object).getData().toString();
                }else if(object instanceof AlnData) {
                    value = ((AlnData) object).getData().toString();
                }  else if(object instanceof AlnDatas) {
                    value = ((AlnDatas) object).getDatas().toString();
                }else if(object instanceof ArrayList) {
                    JsonArray jsonArray = AlnUtilsJson.jsonArray();
                    for(Object item : ((ArrayList) object)) {
                        String itemValue = toString(item);
                        jsonArray.add(itemValue);
                    }
                    value = jsonArray.toString();
                }else if(object instanceof File) {
                    value = toString(new BufferedReader(new FileReader((File) object, StandardCharsets.UTF_8)));
                }else if(object instanceof HttpURLConnection) {
                        value = toString(new BufferedReader(new InputStreamReader(((HttpURLConnection) object).getInputStream(), StandardCharsets.UTF_8)));
                }else if(object instanceof BufferedReader) {
                    final StringBuilder response = new StringBuilder();
                    final BufferedReader bufferedReader = (BufferedReader) object;
                    String line;
                    while((line = bufferedReader.readLine()) != null) {
                        response.append(line);
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
                    value = AlnUtilsData.Converter.DateUtils.calendarToString((Calendar) object);
                }else if(object instanceof Date) {
                    value = AlnUtilsData.Converter.DateUtils.dateToString((Date) object);
                }else {
                    value = object.toString();
                }

            }
        }catch(Exception ignore) {
        }
        return value;
    }

    public static String notNull(final Object object) {
        return notEmpty(object, "");
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

    public static String replaceVariables(String text, final AlnData data) {
        if(isEmpty(text)) return text;

        if(text.contains("${list") && text.contains("}")) {

            final StringBuilder stringBuilder = new StringBuilder();

            final Pattern pattern = Pattern.compile("\\$\\{list(.*?)\\}\\s(.*?)\\s\\$\\{list(.*?)\\}");
            final Matcher matcher = pattern.matcher(text);

            int indexMatchEnd = 0;
            while (matcher.find()) {
                final String key = matcher.group(1);
                final String child = matcher.group(2);

                stringBuilder.append(text, indexMatchEnd, matcher.start());

                final AlnDatas datas = data.has("$_list"+ key) ? data.getChildren("$_list"+ key) : data.getChildren("list"+ key);

                for(AlnData data1 : datas) {
                    final String text1 = replaceVariables(child, data1);
                    stringBuilder.append(text1);
                }

                indexMatchEnd = matcher.end();
            }

            stringBuilder.append(text, indexMatchEnd, text.length());

            if (!isEmpty(stringBuilder.toString())) {
                text = stringBuilder.toString();
            }
        }

        for(String key : data.keySet()) {
            String name = key;
            if(key.startsWith("$_")) {
                name = key.substring(2);
            }
            final String value = data.get(key);
            text = text.replace("${" + name + "}", value);
        }

        if(text.contains("${") && text.contains("}")) {

            final StringBuffer stringBuffer = new StringBuffer();

            final Pattern pattern = Pattern.compile("\\$\\{(\\w+)\\}");
            final Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                try {
                    final String key = matcher.group(1);
                    final String value = data.get(key);
                    matcher.appendReplacement(stringBuffer, value);
                } catch (Exception e) {
                    log.info("\nERROR: TextUtils - replaceVariables ::" + e + "\n");
                }
            }
            matcher.appendTail(stringBuffer);

            if (!isEmpty(stringBuffer.toString())) {
                text = stringBuffer.toString();
            }

        }

        return text;
    }

}
