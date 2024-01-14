package aionem.net.sdk.data.utils;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsParse;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.beans.Datas;
import com.google.gson.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


@Log4j2
public class UtilsJson {


    public static JsonObject jsonObject() {
        return new JsonObject();
    }

    public static JsonArray jsonArray() {
        return new JsonArray();
    }

    public static Gson getGsonPretty() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }

    public static String prettyPrint(final JsonElement jsonElement) {
        return getGsonPretty().toJson(jsonElement);
    }

    public static boolean isJson(final Object object) {
        boolean isJson = false;
        try {
            if(!(object instanceof JsonElement)) {
                final String value = UtilsText.toString(object);
                if(!UtilsText.isEmpty(value)) {
                    new Gson().fromJson(value, JsonObject.class);
                    isJson = true;
                }
            }
        }catch(Exception ignore) {
        }
        return isJson;
    }

    public static JsonElement toJson(final Object object) {
        final JsonArray jsonArray = toJsonArray(object);
        if(!jsonArray.isEmpty()) {
            return jsonArray;
        }else {
            final JsonObject jsonObject = toJsonObject(object);
            if(!jsonObject.isEmpty()) {
                return jsonObject;
            }
        }
        return new JsonObject();
    }

    public static JsonObject toJsonObject(final Object object) {
        JsonObject jsonObject = new JsonObject();
        try {
            if(object instanceof JsonElement) {
                jsonObject = (JsonObject) object;
            }else if(object instanceof Data) {
                jsonObject = ((Data) object).toJson();
            }else if(object instanceof HashMap) {
                jsonObject = fromHashMap((HashMap<String, Object>) object);
            }else if(object instanceof ResourceBundle) {
                jsonObject = fromResourceBundle((ResourceBundle) object);
            }else {
                final String value = UtilsText.toString(object);
                if(!UtilsText.isEmpty(value)) {
                    jsonObject = new Gson().fromJson(value, JsonObject.class);
                }
            }
        }catch(final Exception ignore) {
        }
        return jsonObject != null && !jsonObject.isJsonNull() ? jsonObject : new JsonObject();
    }

    public static JsonArray toJsonArray(final Object object) {
        JsonArray jsonArray = new JsonArray();
        try {
            if(object instanceof JsonArray) {
                jsonArray = (JsonArray) object;
            }else if(object instanceof Datas) {
                jsonArray = ((Datas) object).toJson();
            }else if(object instanceof String) {
                jsonArray = new Gson().fromJson((String) object, JsonArray.class);
            }else {
                jsonArray = new Gson().toJsonTree(object).getAsJsonArray();
            }
        }catch(Exception ignore) {
        }
        return jsonArray != null && !jsonArray.isJsonNull() ? jsonArray : new JsonArray();
    }

    public static HashMap<String, Object> toHashMap(final JsonObject jsonObject) {
        final HashMap<String, Object> values = new HashMap<>();
        if(jsonObject != null) {
            for(final String key : jsonObject.keySet()) {
                values.put(key, getValue(jsonObject, key));
            }
        }
        return values;
    }

    public static JsonObject fromHashMap(final HashMap<String, Object> hashMap) {
        final JsonObject jsonObject = new JsonObject();
        if(hashMap != null) {
            for(final String key : hashMap.keySet()) {
                final Object value = hashMap.get(key);
                add(jsonObject, key, value);
            }
        }
        return jsonObject;
    }

    public static JsonObject fromResourceBundle(final ResourceBundle resourceBundle) {
        final JsonObject jsonObject = new JsonObject();
        if(resourceBundle != null) {
            for(final String key : resourceBundle.keySet()) {
                final Object value = resourceBundle.getString(key);
                add(jsonObject, key, value);
            }
        }
        return jsonObject;
    }

    public static JsonObject add(final JsonObject data, final String key, final Object value) {
        if(value instanceof JsonObject) {
            data.add(key, (JsonObject) value);
        }else if(value instanceof JsonArray) {
            data.add(key, (JsonArray) value);
        }else if(value instanceof JsonElement) {
            data.add(key, (JsonElement) value);
        }else if(value instanceof Data) {
            data.add(key, ((Data) value).toJson());
        }else if(value instanceof Datas) {
            data.add(key, ((Datas) value).toJson());
        }else if(value instanceof Number) {
            data.addProperty(key, (Number) value);
        }else if(value instanceof Boolean) {
            data.addProperty(key, (Boolean) value);
        }else if(value instanceof Character) {
            data.addProperty(key, (Character) value);
        }else if(value instanceof String) {
            data.addProperty(key, (String) value);
        }else {
            final String valueString = UtilsText.toString(value);
            if(UtilsJson.isJson(value)) {
                data.add(key, UtilsJson.toJsonObject(valueString));
            }else if(UtilsParse.isNumber(valueString)) {
                final double valueNumber = UtilsParse.toNumber(valueString, 0);
                if(valueString.contains(".")) {
                    data.addProperty(key, valueNumber);
                }else {
                    data.addProperty(key, (long) valueNumber);
                }
            }else if(UtilsParse.isBoolean(valueString)) {
                final boolean valueBoolean = UtilsParse.toBoolean(valueString, false);
                data.addProperty(key, valueBoolean);
            }else {
                data.addProperty(key, valueString);
            }
        }
        return data;
    }

    public static String getValue(final JsonElement json, final String path) {
        return getValue(json, path, String.class);
    }

    public static <T> T getValue(final JsonElement json, final String path, final T defaultValue) {
        if(defaultValue == null) return null;
        return (T) getValue(json, path, defaultValue.getClass());
    }

    public static <T> T getValue(final JsonElement json, final String path, final Class<T> type) {
        final JsonElement value = getJson(json, path);
        final String stringValue = value != null && !value.isJsonNull() ? (value.isJsonPrimitive() ? value.getAsString() : value.toString()) : null;
        return UtilsConverter.convert(stringValue, type);
    }

    public static JsonObject getJsonObject(final JsonElement json, final String path) {
        return getJsonObject(json, path, null);
    }

    public static JsonObject getJsonObject(final JsonElement json, final String path, final JsonObject defaultJsonObject) {
        final JsonElement value = getJson(json, path);
        return value != null && value.isJsonObject() ? value.getAsJsonObject() : defaultJsonObject;
    }

    public static JsonArray getJsonArray(final JsonElement json, final String path) {
        return getJsonArray(json, path, null);
    }

    public static JsonArray getJsonArray(final JsonElement json, final String path, final JsonArray defaultJsonArray) {
        final JsonElement value = getJson(json, path);
        return value != null && value.isJsonArray() ? value.getAsJsonArray() : defaultJsonArray;
    }

    public static JsonElement getJson(final JsonElement json, String path) {
        if(json == null || UtilsText.isEmpty(path)) {
            return json;
        }
        JsonElement value = json;
        try {
            path = path.startsWith("/") ? path.substring(1) : path;
            final String[] keys = path.split("/");
            for(final String key : keys) {
                if(value instanceof JsonObject) {
                    value = ((JsonObject) value).get(key);
                }else if(value instanceof JsonArray) {
                    final int index = Integer.parseInt(key);
                    value = ((JsonArray) value).get(index);
                }else {
                    return null;
                }
            }
        }catch(Exception ignore) {
        }
        return value;
    }

    public static ArrayList<Map<String, Object>> jsonArrayToListMap(final JsonArray jsonArray) {

        final ArrayList<Map<String, Object>> listMap = new ArrayList<>();

        try {

            if(jsonArray != null) {

                for(int i = 0; i < jsonArray.size(); i++) {

                    final JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

                    final Map<String, Object> objectMap = new HashMap<>();

                    for(final String key : jsonObject.keySet()) {

                        final JsonElement jsonValue = jsonObject.get(key);

                        if(jsonValue.isJsonObject()) {

                            final Map<String, Object> valueMap = new HashMap<>();

                            final JsonObject jsonValue1 = jsonValue.getAsJsonObject();

                            for(final String key1 : jsonValue1.getAsJsonObject().keySet()) {
                                final JsonElement jsonValue2 = jsonValue1.get(key1);
                                if(!jsonValue2.isJsonNull()) {
                                    final Object value = jsonValue2.isJsonPrimitive() ? jsonValue2.getAsString() : jsonValue2.toString();
                                    valueMap.put(key1, value);
                                }
                            }

                            objectMap.put(key, valueMap);

                        }else {
                            if(!jsonValue.isJsonNull()) {
                                final Object value = jsonValue.isJsonPrimitive() ? jsonValue.getAsString() : jsonValue.toString();
                                objectMap.put(key, value);
                            }
                        }
                    }

                    listMap.add(objectMap);
                }

            }

        }catch(Exception ignore) {
        }

        return listMap;
    }

}
