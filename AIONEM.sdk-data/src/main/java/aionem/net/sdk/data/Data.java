package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsNetwork;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsData;
import aionem.net.sdk.data.utils.UtilsJson;
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

    private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

    public Data() {

    }

    public Data(final JsonObject values) {
        fromData(values);
    }
    public Data(final HashMap<String, Object> values) {
        fromData(values);
    }
    public Data(final Object data) {
        fromData(UtilsJson.toJsonObject(data));
    }
    

    public <T> T init(T t) {

        for(Field field : t.getClass().getDeclaredFields()) {
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
                    Object value = field.get(t);
                    if(value != null) {
                        value = UtilsConverter.convert(value, field.getType());
                    }
                    put(key, value);
                }catch(Exception e) {
                    log.error("\nERROR: Data - fromData " + e +"\n");
                }
            }
        }

        return t;
    }

    public JsonObject toJson() {
        return UtilsJson.fromHashMap(values);
    }
    public JsonObject toJsonAll() {
        return UtilsJson.fromHashMap(values);
    }
    public <T> JsonObject toJson(final T db) {
        final JsonObject data = UtilsJson.jsonObject();
        try {
            for(Field field : db.getClass().getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                final boolean isStatic = Modifier.isStatic(modifiers);
                final boolean isPrivate = Modifier.isPrivate(modifiers);
                if(!isStatic && !isPrivate) {
                    field.setAccessible(true);
                    final Col col = field.isAnnotationPresent(Col.class) ? field.getDeclaredAnnotation(Col.class) : null;
                    final String fieldName = field.getName();
                    final String key = col != null ? UtilsText.notEmpty(col.value(), fieldName) : fieldName;
                    Object value = field.get(db);
                    if(value == null) {
                        value = this.values.get(key);
                    }
                    UtilsJson.add(data, key, value);
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: " + e +"\n");
        }
        return data;
    }

    public Data fromData(final Data data) {
        if(data != null) {
            fromData(data.getValues());
        }
        return this;
    }

    public Data fromData(final HashMap<String, Object> data) {
        this.values.clear();
        this.values.putAll(data);
        return this;
    }

    public Data fromData(final JsonObject data) {
        return fromData(UtilsJson.toHashMap(data));
    }

    public Data fromData(final Object data) {
        return fromData(UtilsJson.toJsonObject(data));
    }

    public <T> T fromData(final T dbInstance, final JsonObject data) {
        fromData(UtilsJson.toHashMap(data));
        try {
            return UtilsData.adaptTo(dbInstance, data);
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Data - fromData " + e +"\n");
        }
        return dbInstance;
    }

    public Data fromData(final String key, final Data data) {
        final Object value = data.get(key);
        return puts(this, key, value);
    }

    public Data put(final String key, final Object... values) {
        return puts(this, key, values);
    }
    public <T> T puts(final T dbInstance, final String key, final Object... values) {
        return put(dbInstance, key, values.length > 0 ? (values.length == 1 ? values[0] : Arrays.asList(values)) : null);
    }
    public <T> T put(final T dbInstance, final String key, final Object value) {
        this.values.put(key, value);
        try {
            final Field field = dbInstance.getClass().getDeclaredField(key);
            final int modifiers = field.getModifiers();
            final boolean isStatic = Modifier.isStatic(modifiers);
            final boolean isPrivate = Modifier.isPrivate(modifiers);
            if(!isStatic && !isPrivate) {
                field.setAccessible(true);
                if(value != null) {
                    field.set(dbInstance, value);
                }else {
                    final T defaultInstance = (T) dbInstance.getClass().newInstance();
                    final Field defaultField = defaultInstance.getClass().getDeclaredField(key);
                    defaultField.setAccessible(true);
                    final Object defaultValue = defaultField.get(defaultInstance);
                    defaultField.set(dbInstance, defaultValue);
                }
            }
        } catch (NoSuchFieldException ignore) {
        } catch (Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK put : " + e + "\n");
        }
        return dbInstance;
    }

    public String get(final String key1, final String key2) {
        return getOrLast(new String[] {key1, key2}, false);
    }
    public String get(final String... keys) {
        return getOrLast(keys, false);
    }
    public String getOr(final String... keys) {
        return getOrLast(keys, true);
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
    public DataArray getChildren() {
        final DataArray datas = new DataArray();
        for(final String key : this.values.keySet()) {
            final Object value = this.values.get(key);
            datas.add(new Data(value));
        }
        return datas;
    }
    public DataArray getChildren(final String key) {
        final DataArray datas = new DataArray();
        for(final JsonElement jsonElement : UtilsJson.toJsonArray(get(key))) {
            datas.add(new Data(jsonElement));
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

    public Data remove(final String key) {
        return remove(this, key);
    }
    public <T> T remove(final T dbInstance, final String key) {
        put(dbInstance, key, null);
        this.values.remove(key);
        return dbInstance;
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

    public String toLines() {
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

    public boolean equals2(final String key, final Object... values) {
        final Object value = getObject(key);
        for(final Object value1 : values) {
            final boolean isEqual = Objects.equals(value, value1) || UtilsText.equals(UtilsText.toString(value), UtilsText.toString(value1));
            if(isEqual) return true;
        }
        return false;
    }
    public boolean equalsIgnoreCase2(final String key, final Object... values) {
        final String value = get(key);
        for(final Object value1 : values) {
            final boolean isEqual = Objects.equals(value, value1) || UtilsText.equalsIgnoreCase(value, UtilsText.toString(value1));
            if(isEqual) return true;
        }
        return false;
    }

    public boolean equals(final Object value, final String key) {
        return Objects.equals(value, getObject(key));
    }
    public boolean equalsIgnoreCase(final Object value, final String key) {
        return equals(value, key) || UtilsText.equalsIgnoreCase(UtilsText.toString(value), get(key));
    }

    public <T> T adaptTo(Class<T> type) {
        try {
            return UtilsData.adaptTo(type, toJson());
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Data - adaptTo(Class<T> type) " + e +"\n");
            return null;
        }
    }
    public <T> T adaptTo(T t) {
        try {
            return UtilsData.adaptTo(t, toJson());
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Data - adaptTo(T t) " + e +"\n");
            return null;
        }
    }

}
