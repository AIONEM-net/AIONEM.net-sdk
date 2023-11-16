package aionem.net.sdk.data.utils;

import aionem.net.sdk.core.utils.AlnUtilsConverter;
import aionem.net.sdk.core.utils.AlnUtilsText;
import aionem.net.sdk.data.AlnDBCol;
import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.data.AlnDatas;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j2
public class AlnUtilsData {


    public static <T> T adaptTo(final Class<T> type, final JsonObject data) throws Exception {
        return adaptTo(type, (Object) data);
    }
    public static <T> T adaptTo(final Class<T> type, final Object data) throws Exception {
        T t;
        if(type.getSuperclass().isAssignableFrom(AlnData.class) || type.isAssignableFrom(AlnData.class)) {
            return type.getConstructor(data.getClass()).newInstance(data);
        }else {
            t = type.getConstructor().newInstance();

            if(type.getSuperclass().isAssignableFrom(HashMap.class) || type.isAssignableFrom(HashMap.class)) {
                return adaptTo(t, AlnUtilsJson.fromHashMap((HashMap<String, Object>) data));
            }else if(type.getSuperclass().isAssignableFrom(JsonObject.class) || type.isAssignableFrom(JsonObject.class)) {
                return adaptTo(t, (JsonObject) data);
            }else {
                return t;
            }
        }
    }

    public static <T> T adaptTo(final T t, final JsonObject data) throws Exception {
        if(data == null) return null;
        for(final Field field : t.getClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            final boolean isStatic = Modifier.isStatic(modifiers);
            final boolean isFinal = Modifier.isFinal(modifiers);
            final boolean isPrivate = Modifier.isPrivate(modifiers);
            if(!isStatic && !isFinal && !isPrivate) {
                field.setAccessible(true);
                final AlnDBCol col = field.isAnnotationPresent(AlnDBCol.class) ? field.getDeclaredAnnotation(AlnDBCol.class) : null;
                final String fieldName = field.getName();
                final String name = col != null ? AlnUtilsText.notEmpty(col.value(), fieldName) : fieldName;
                Object value = AlnUtilsJson.getValue(data, name);
                if(value != null) {
                    value = AlnUtilsConverter.convert(value, field.getType());
                }
                field.set(t, value);
            }
        }
        return t;
    }

    public static String replaceVariables(String text, final AlnData data) {
        if(AlnUtilsText.isEmpty(text)) return "";

        if(text.contains("${list") && text.contains("}")) {

            final StringBuilder stringBuilder = new StringBuilder();

            final Pattern pattern = Pattern.compile("\\$\\{list(.*?)}(.*?)\\$\\{list(.*?)\\}", Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(text);

            int indexMatchEnd = 0;
            while(matcher.find()) {
                final String key = matcher.group(1);
                final String child = matcher.group(2);

                stringBuilder.append(text, indexMatchEnd, matcher.start());

                final AlnDatas datas = data.has("$_list"+ key) ? data.getChildren("$_list"+ key) : data.getChildren("list"+ key);

                for(final AlnData data1 : datas) {
                    final String text1 = replaceVariables(child, data1);
                    stringBuilder.append(text1);
                }

                indexMatchEnd = matcher.end();
            }

            stringBuilder.append(text, indexMatchEnd, text.length());

            if(!AlnUtilsText.isEmpty(stringBuilder.toString())) {
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
                } catch (Exception e) {
                    log.info("\nERROR: TextUtils - replaceVariables ::" + e + "\n");
                }
            }
            matcher.appendTail(stringBuffer);

            if(!AlnUtilsText.isEmpty(stringBuffer.toString())) {
                text = stringBuffer.toString();
            }

        }

        return text;
    }

}
