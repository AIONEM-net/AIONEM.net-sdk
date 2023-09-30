package aionem.net.sdk.data;

import aionem.net.sdk.utils.AlnDataUtils;
import aionem.net.sdk.utils.AlnJsonUtils;
import aionem.net.sdk.utils.AlnNetworkUtils;
import aionem.net.sdk.utils.AlnTextUtils;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Log4j
@Getter
public class AlnData {

    private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

    public AlnData() {

    }

    public AlnData(final JsonObject values) {
        fromData(values);
    }
    public AlnData(final HashMap<String, Object> values) {
        fromData(values);
    }
    public AlnData(final Object data) {
        fromData(AlnJsonUtils.toJsonObject(data));
    }
    

    public <T> T init(T t) {

        for(Field field : t.getClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            final boolean isStatic = Modifier.isStatic(modifiers);
            final boolean isFinal = Modifier.isFinal(modifiers);
            final boolean isPrivate = Modifier.isPrivate(modifiers);
            if(!isStatic && !isFinal && !isPrivate) {
                field.setAccessible(true);
                final AlnDBCol col = field.isAnnotationPresent(AlnDBCol.class) ? field.getDeclaredAnnotation(AlnDBCol.class) : null;
                final String fieldName = field.getName();
                final String key = col != null ? AlnTextUtils.notEmpty(col.name(), fieldName) : fieldName;
                try {
                    Object value = field.get(t);
                    if(value != null) {
                        value = AlnDataUtils.convert(value, field.getType());
                    }
                    put(key, value);
                }catch(Exception e) {
                    log.error("\nERROR: AlnData - fromData " + e +"\n");
                }
            }
        }

        return t;
    }

    public JsonObject getData() {
        return AlnJsonUtils.fromHashMap(values);
    }
    public <T> JsonObject getData(final T db) {
        final JsonObject data = AlnJsonUtils.jsonObject();
        try {
            for(Field field : db.getClass().getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                final boolean isStatic = Modifier.isStatic(modifiers);
                final boolean isPrivate = Modifier.isPrivate(modifiers);
                if(!isStatic && !isPrivate) {
                    field.setAccessible(true);
                    final AlnDBCol col = field.isAnnotationPresent(AlnDBCol.class) ? field.getDeclaredAnnotation(AlnDBCol.class) : null;
                    final String fieldName = field.getName();
                    final String key = col != null ? AlnTextUtils.notEmpty(col.name(), fieldName) : fieldName;
                    Object value = field.get(db);
                    if(value == null) {
                        value = this.values.get(key);
                    }
                    AlnJsonUtils.add(data, key, value);
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: " + e +"\n");
        }
        return data;
    }

    public AlnData fromData(final AlnData data) {
        if(data != null) {
            fromData(data.getValues());
        }
        return this;
    }

    public AlnData fromData(final HashMap<String, Object> data) {
        this.values.clear();
        this.values.putAll(data);
        return this;
    }

    public AlnData fromData(final JsonObject data) {
        return fromData(AlnJsonUtils.toHashMap(data));
    }

    public AlnData fromData(final Object data) {
        return fromData(AlnJsonUtils.toJsonObject(data));
    }

    public <T> T fromData(final T dbInstance, final JsonObject data) {
        fromData(AlnJsonUtils.toHashMap(data));
        try {
            return AlnDataUtils.adaptTo(dbInstance, data);
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : AlnData - fromData " + e +"\n");
        }
        return dbInstance;
    }

    public AlnData fromData(final String key, final AlnData data) {
        final Object value = data.get(key);
        return put(this, key, value);
    }

    public AlnData put(final String key, final Object value) {
        return put(this, key, value);
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
                field.set(dbInstance, value);
            }
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : " + e +"\n");
        }
        return dbInstance;
    }

    public String get(final String key) {
        return get(key, "");
    }
    public String getOr(final String key1, final String key2) {
        final String value = get(key1);
        return !AlnTextUtils.isEmpty(value) ? value : get(key2);
    }
    public String getOr(final String key1, final String key2, final String defaultValue) {
        final String value = get(key1);
        return !AlnTextUtils.isEmpty(value) ? value : get(key2, defaultValue);
    }
    public String getNullable(final String key) {
        return has(key) ? get(key) : null;
    }
    public String getEmptyNull(final String key) {
        final String value = get(key, String.class);
        return !AlnTextUtils.isEmpty(value) ? value : null;
    }
    public Object getObject(final String key) {
        return this.values.get(key);
    }
    public <T> T get(final String key, final T defaultValue) {
        final Object value = this.values.get(key);
        return AlnDataUtils.convert(value, defaultValue);
    }
    public <T> T get(final String key, final Class<T> type) {
        final Object value = this.values.get(key);
        return AlnDataUtils.convert(value, type);
    }

    public HashMap<String, String> getValuesString() {
        final HashMap<String, String> valuesString = new HashMap<>();
        for(final String key : this.values.keySet()) {
            final Object value = this.values.get(key);
            valuesString.put(key, AlnTextUtils.toString(value));
        }
        return valuesString;
    }

    public boolean has(String key) {
        return this.values.containsKey(key);
    }

    public boolean isEmpty(final String key) {
        return AlnTextUtils.isEmpty(get(key));
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
            final String key = AlnNetworkUtils.encodeUrl(entry.getKey());
            final String value = AlnNetworkUtils.encodeUrl(AlnTextUtils.toString(entry.getValue()));
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
        return getData().toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final AlnData alnData = (AlnData) o;
        return Objects.equals(values, alnData.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    public boolean equals(final Object value, final String key) {
        return Objects.equals(value, getObject(key));
    }
    public boolean equalsIgnoreCase(final Object value, final String key) {
        return equals(value, key) || AlnTextUtils.equalsIgnoreCase(AlnTextUtils.toString(value), get(key));
    }

    public <T> T adaptTo(Class<T> type) {
        try {
            return AlnDataUtils.adaptTo(type, getData());
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : AlnData - adaptTo(Class<T> type) " + e +"\n");
            return null;
        }
    }
    public <T> T adaptTo(T t) {
        try {
            return AlnDataUtils.adaptTo(t, getData());
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : AlnData - adaptTo(T t) " + e +"\n");
            return null;
        }
    }

}
