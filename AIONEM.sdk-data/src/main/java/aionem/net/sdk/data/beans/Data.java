package aionem.net.sdk.data.beans;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsNetwork;
import aionem.net.sdk.core.utils.UtilsParse;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.query.Col;
import aionem.net.sdk.data.utils.UtilsData;
import aionem.net.sdk.data.utils.UtilsJson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Log4j2
@Getter
public class Data {

    private Object instance = null;

    private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

    public Data() {

    }

    public Data(final JsonObject values) {
        init(null, values);
    }

    public Data(final HashMap<String, Object> values) {
        init(null, values);
    }

    public Data(final Object data) {
        init(null, UtilsJson.toJsonObject(data));
    }

    public Data(final Object instance, final JsonObject values) {
        init(instance, values);
    }

    public Data(final Object instance, final HashMap<String, Object> values) {
        init(instance, values);
    }

    public Data(final Object instance, final Object data) {
        init(instance, UtilsJson.toJsonObject(data));
    }

    public <T> T init(T dbInstance) {
        instance = dbInstance;

        if(dbInstance != null && !dbInstance.getClass().equals(Data.class)) {
            for(final Field field : dbInstance.getClass().getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                final boolean isStatic = Modifier.isStatic(modifiers);
                final boolean isFinal = Modifier.isFinal(modifiers);
                final boolean isPrivate = Modifier.isPrivate(modifiers);
                if(!isStatic && !isFinal && !isPrivate) {
                    field.setAccessible(true);
                    final Col col = field.isAnnotationPresent(Col.class) ? field.getDeclaredAnnotation(Col.class) : null;
                    final String fieldName = field.getName();
                    final String key = col != null ? UtilsText.notEmpty(col.value(), fieldName) : fieldName;
                    try {
                        Object value = field.get(dbInstance);
                        if(value != null) {
                            value = UtilsConverter.convert(value, field.getType());
                        }
                        put(key, value);
                    }catch(final Exception e) {
                        log.error("\nERROR: Data - init " + e + "\n");
                    }
                }
            }
        }

        return dbInstance;
    }

    public <T> T init(final T dbInstance, final Data data) {
        this.values.clear();
        this.values.putAll(data.values);
        try {
            return UtilsData.adaptTo(dbInstance, data.toJson());
        }catch(final Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Data - fromData " + e +"\n");
        }
        return dbInstance;
    }

    public <T> T init(final T dbInstance, final HashMap<String, Object> data) {
        this.values.clear();
        this.values.putAll(data);
        try {
            return UtilsData.adaptTo(dbInstance, UtilsJson.fromHashMap(data));
        }catch(final Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Data - fromData " + e +"\n");
        }
        return dbInstance;
    }

    public <T> T init(final T dbInstance, final JsonObject data) {
        this.values.clear();
        this.values.putAll(UtilsJson.toHashMap(data));
        try {
            return UtilsData.adaptTo(dbInstance, data);
        }catch(final Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Data - fromData " + e +"\n");
        }
        return dbInstance;
    }

    public <T> T init(final T dbInstance, final Object data) {
        return init(dbInstance, UtilsJson.toJsonObject(data));
    }

    public <T> T getInstance() {
        return instance != null ? (T) instance : (T) this;
    }

    public void setValues(final Map<String, Object> values) {
        init(instance, values);
    }

    public void setValues(final Object values) {
        init(instance, values);
    }

    public JsonObject toJson() {
        return toJson(instance);
    }

    public JsonObject toJsonAll() {
        return UtilsJson.fromHashMap(values);
    }

    public <T> JsonObject toJson(final T dbInstance) {
        final JsonObject json = UtilsJson.jsonObject();
        try {
            if(dbInstance != null && !dbInstance.getClass().equals(Data.class)) {
                for(Field field : dbInstance.getClass().getDeclaredFields()) {
                    final int modifiers = field.getModifiers();
                    final boolean isStatic = Modifier.isStatic(modifiers);
                    final boolean isPrivate = Modifier.isPrivate(modifiers);
                    if(!isStatic && !isPrivate) {
                        field.setAccessible(true);
                        final Col col = field.isAnnotationPresent(Col.class) ? field.getDeclaredAnnotation(Col.class) : null;
                        final String fieldName = field.getName();
                        final String key = col != null ? UtilsText.notEmpty(col.value(), fieldName) : fieldName;
                        Object value = field.get(dbInstance);
                        if(value == null) {
                            value = this.values.get(key);
                        }
                        UtilsJson.add(json, key, value);
                    }
                }
            }else {
                return UtilsJson.fromHashMap(this.values);
            }
        }catch(final Exception e) {
            log.error("\nERROR: toJson " + e +"\n");
        }
        return json;
    }

    public Data put(final String key, final Object... values) {
        return puts(key, values);
    }

    public Data puts(final String key, final Object... values) {
        return put(key, values.length > 0 ? (values.length == 1 ? values[0] : Arrays.asList(values)) : null);
    }

    public Data put(final String key, final Object value) {
        this.values.put(key, value);
        try {
            if(instance != null) {
                final Field field = instance.getClass().getDeclaredField(key);
                final int modifiers = field.getModifiers();
                final boolean isStatic = Modifier.isStatic(modifiers);
                final boolean isPrivate = Modifier.isPrivate(modifiers);
                if(!isStatic && !isPrivate) {
                    field.setAccessible(true);
                    if(value != null) {

                        final Object defaultValue = field.get(instance);
                        final Class<?> fieldType = field.getType();

                        if(String.class.isAssignableFrom(fieldType)) {
                            field.set(instance, UtilsText.toString(value));
                        }else if(Integer.class.isAssignableFrom(fieldType)) {
                            field.set(instance, UtilsParse.toNumber(value, (Integer) defaultValue));
                        }else if(Double.class.isAssignableFrom(fieldType)) {
                            field.set(instance, UtilsParse.toNumber(value, (Double) defaultValue));
                        }else if(Long.class.isAssignableFrom(fieldType)) {
                            field.set(instance, UtilsParse.toNumber(value, (Long) defaultValue));
                        }else if(Boolean.class.isAssignableFrom(fieldType)) {
                            field.set(instance, UtilsParse.toBoolean(value, (Boolean) defaultValue));
                        }else {
                            field.set(instance, value);
                        }

                    }else {
                        final Object defaultInstance = instance.getClass().getDeclaredConstructor().newInstance();
                        final Field defaultField = defaultInstance.getClass().getDeclaredField(key);
                        defaultField.setAccessible(true);
                        final Object defaultValue = defaultField.get(defaultInstance);
                        defaultField.set(instance, defaultValue);
                    }
                }
            }
        } catch (NoSuchFieldException ignore) {
        }catch(final Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK put : " + e + "\n");
        }
        return getInstance();
    }

    public String get(final String key1, final String key2) {
        return getOrLast(new String[] {key1, key2}, false);
    }

    public String get(final String... keys) {
        return getOrLast(keys, false);
    }

    public String getOr(final String key, final String... keys) {
        final String[] keys2;
        if(keys != null && keys.length > 0) {
            keys2 = new String[keys.length + 1];
            keys2[0] = key;
            for(int i = 0; i < keys.length; i++) {
                final String key1 = keys[i];
                keys2[i + 1] = key1;
            }
        }else {
            keys2 = new String[] {key};
        }
        return getOrLast(keys2, true);
    }

    public String getOrLast(final String[] keys, final boolean isOrLast) {
        for(int i = 0; i < keys.length; i++) {
            final String key = keys[i];
            if(!UtilsText.isEmpty(key)) {
                final String value = get(key, String.class);
                if(i > 0 && i == keys.length - 1) {
                    return UtilsText.notEmpty(value, isOrLast ? key : "");
                }else {
                    if(has(key)) {
                        return value;
                    }
                }
            }
        }
        return isOrLast && keys.length > 0 ? keys[keys.length-1] : "";
    }

    public long getId(final String key) {
        return (long) UtilsParse.toNumber(get(key), -1);
    }

    public ArrayList<String> getArray(final String key) {
        final ArrayList<String> values = new ArrayList<>();
        final JsonArray jsonArray = UtilsJson.toJsonArray(get(key));
        for(final JsonElement jsonElement : jsonArray) {
            if(jsonElement.isJsonPrimitive()) {
                values.add(jsonElement.getAsString());
            }else {
                values.add(jsonElement.toString());
            }
        }
        return values;
    }

    public String getNullable(final String key) {
        return has(key) ? get(key) : null;
    }

    public String getEmptyNull(final String key) {
        final String value = get(key, String.class);
        return !UtilsText.isEmpty(value) ? value : null;
    }

    public Object getObject(final String key) {
        return this.values.get(key);
    }

    public <T> T get(final String key, final T defaultValue) {
        final Object value = this.values.get(key);
        return UtilsConverter.convert(value, defaultValue);
    }

    public <T> T get(final String key, final Class<T> type) {
        final Object value = this.values.get(key);
        return UtilsConverter.convert(value, type);
    }

    public Data getChild(final String key) {
        return new Data(get(key));
    }

    public Datas getChildren() {
        final Datas datas = new Datas();
        for(final String key : this.values.keySet()) {
            final Object value = this.values.get(key);
            datas.add(new Data(value));
        }
        return datas;
    }

    public Datas getChildren(final String key) {
        if(UtilsText.isEmpty(key)) return getChildren();
        final Datas datas = new Datas();
        final JsonElement jsonElement = UtilsJson.toJson(get(key));
        if(jsonElement.isJsonArray()) {
            for(final JsonElement json : (JsonArray) jsonElement) {
                datas.add(new Data(json));
            }
        }else if(jsonElement.isJsonObject()) {
            for(final Map.Entry<String, JsonElement> json : ((JsonObject) jsonElement).entrySet()) {
                datas.add(new Data(json.getValue()));
            }
            return datas;
        }
        return datas;
    }

    public HashMap<String, String> getValuesString() {
        final HashMap<String, String> valuesString = new HashMap<>();
        for(final String key : this.values.keySet()) {
            final Object value = this.values.get(key);
            valuesString.put(key, UtilsText.toString(value));
        }
        return valuesString;
    }

    public <T> T remove(final String key) {
        put(key, (Object) null);
        this.values.remove(key);
        return getInstance();
    }

    public boolean has(String key) {
        return this.values.containsKey(key);
    }

    public boolean isEmpty(final String key) {
        return UtilsText.isEmpty(get(key));
    }

    public int size() {
        return this.values.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set<String> keySet() {
        return this.values.keySet();
    }

    public String toHtmlLines() {
        final StringBuilder lines = new StringBuilder();
        for(final String key : this.values.keySet()) {
            final Object value = this.values.get(key);
            lines.append("\n<b>").append(key).append("</b> : ").append(value).append("\n");
        }
        return lines.toString();
    }

    public String toQueryString() {
        final StringBuilder lines = new StringBuilder();
        int i = 0;
        for(final Map.Entry<String, Object> entry : this.values.entrySet()) {
            final String key = UtilsNetwork.encodeUrl(entry.getKey());
            final String value = UtilsNetwork.encodeUrl(UtilsText.toString(entry.getValue()));
            lines.append(i > 0 ? "&" : "").append(key).append("=").append(value);
            i++;
        }
        return lines.toString();
    }

    public String toResourceBundleString() {
        final StringBuilder lines = new StringBuilder();
        int i = 0;
        for(final Map.Entry<String, Object> entry : this.values.entrySet()) {
            final String key = UtilsNetwork.encodeUrl(entry.getKey());
            final String value = UtilsNetwork.encodeUrl(UtilsText.toString(entry.getValue()));
            lines.append(i > 0 ? "\n" : "").append(key).append("=").append(value);
            i++;
        }
        return lines.toString();
    }

    public byte[] getJsonBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getQueryBytes() {
        return toQueryString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        final Data data = (Data) object;
        return size() == data.size() && Objects.equals(values.toString(), data.values.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    public boolean equals(final String key, final Object... values) {
        final Object value = getObject(key);
        for(final Object value1 : values) {
            final boolean isEqual = Objects.equals(value, value1) || UtilsText.equals(UtilsText.toString(value), UtilsText.toString(value1));
            if(isEqual) return true;
        }
        return false;
    }

    public boolean equalsIgnoreCase(final String key, final Object... values) {
        final String value = get(key);
        for(final Object value1 : values) {
            final boolean isEqual = Objects.equals(value, value1) || UtilsText.equalsIgnoreCase(value, UtilsText.toString(value1));
            if(isEqual) return true;
        }
        return false;
    }

}
