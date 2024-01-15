package aionem.net.sdk.data.utils;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.query.Col;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.beans.Datas;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j2
public class UtilsData {


    public static <T> T adaptTo(final Class<T> type, final JsonObject data) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return adaptTo(type, (Object) data);
    }

    public static <T> T adaptTo(final Class<T> type, final Object data) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if(type == null || data == null) return null;
        if(type.getSuperclass().isAssignableFrom(Data.class) || type.isAssignableFrom(Data.class)) {
            return type.getConstructor(data.getClass()).newInstance(data);
        }else {
            final T t = type.getConstructor().newInstance();

            if(data instanceof HashMap) {
                return adaptTo(t, UtilsJson.fromHashMap((HashMap<String, Object>) data));
            }else if(data instanceof JsonObject) {
                return adaptTo(t, (JsonObject) data);
            }else {
                return t;
            }
        }
    }

    public static <T> T adaptTo(final T t, final JsonObject data) throws IllegalAccessException {
        if(t == null || data == null) return null;
        for(final Field field : t.getClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            final boolean isStatic = Modifier.isStatic(modifiers);
            final boolean isFinal = Modifier.isFinal(modifiers);
            final boolean isPrivate = Modifier.isPrivate(modifiers);
            if(!isStatic && !isFinal && !isPrivate) {
                field.setAccessible(true);
                final Col col = field.isAnnotationPresent(Col.class) ? field.getDeclaredAnnotation(Col.class) : null;
                final String fieldName = field.getName();
                final String name = col != null ? UtilsText.notEmpty(col.value(), fieldName) : fieldName;
                Object value = UtilsJson.getValue(data, name);
                if(value != null) {
                    value = UtilsConverter.convert(value, field.getType());
                    if(value != null) {
                        field.set(t, value);
                    }
                }
            }
        }
        return t;
    }

    public static String replaceVariables(String text, final Data data) {
        if(UtilsText.isEmpty(text)) return "";

        if(text.contains("${list") && text.contains("}")) {

            final StringBuilder stringBuilder = new StringBuilder();

            final Pattern pattern = Pattern.compile("\\$\\{list(.*?)}(.*?)\\$\\{list(.*?)\\}", Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(text);

            int indexMatchEnd = 0;
            while(matcher.find()) {
                final String key = matcher.group(1);
                final String child = matcher.group(2);

                stringBuilder.append(text, indexMatchEnd, matcher.start());

                final Datas datas = data.has("$_list"+ key) ? data.getChildren("$_list"+ key) : data.getChildren("list"+ key);

                for(final Data data1 : datas) {
                    final String text1 = replaceVariables(child, data1);
                    stringBuilder.append(text1);
                }

                indexMatchEnd = matcher.end();
            }

            stringBuilder.append(text, indexMatchEnd, text.length());

            if(!UtilsText.isEmpty(stringBuilder.toString())) {
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

            final StringBuilder stringBuffer = new StringBuilder();

            final Pattern pattern = Pattern.compile("\\$\\{(\\w+)\\}");
            final Matcher matcher = pattern.matcher(text);
            while(matcher.find()) {
                try {
                    final String key = matcher.group(1);
                    final String value = data.get(key);
                    matcher.appendReplacement(stringBuffer, value);
                }catch(final Exception e) {
                    log.info("\nERROR: TextUtils - replaceVariables ::" + e + "\n");
                }
            }
            matcher.appendTail(stringBuffer);

            if(!UtilsText.isEmpty(stringBuffer.toString())) {
                text = stringBuffer.toString();
            }

        }

        return text;
    }

}
