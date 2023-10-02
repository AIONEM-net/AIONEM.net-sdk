package aionem.net.sdk.data;

import aionem.net.sdk.utils.AlnDataUtils;
import aionem.net.sdk.utils.AlnJsonUtils;
import aionem.net.sdk.utils.AlnNetworkUtils;
import aionem.net.sdk.utils.AlnTextUtils;
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
        }catch(NoSuchFieldException ignore) {
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK put : " + e +"\n");
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
            if(!AlnTextUtils.isEmpty(key)) {
                final String value = get(key, String.class);
                if(i > 0 && i == keys.length - 1) {
                    return AlnTextUtils.notEmpty(value, isOrLast ? key : "");
                }else {
                    if(has(key)) {
                        return value;
                    }
                }
            }
        }
        return "";
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
    public AlnData getChild(final String key) {
        return new AlnData(get(key));
    }
    public AlnDatas getChildren() {
        final AlnDatas datas = new AlnDatas();
        for(final String key : this.values.keySet()) {
            final Object value = this.values.get(key);
            datas.add(new AlnData(value));
        }
        return datas;
    }
    public AlnDatas getChildren(final String key) {
        final AlnDatas datas = new AlnDatas();
        for(final JsonElement jsonElement : AlnJsonUtils.toJsonArray(get(key))) {
            datas.add(new AlnData(jsonElement));
        }
        return datas;
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
    public boolean equals(Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        final AlnData data = (AlnData) object;
        return size() == data.size() && Objects.equals(values.toString(), data.values.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    public boolean equals2(final String key, final Object... values) {
        final Object value = getObject(key);
        for(final Object value1 : values) {
            final boolean isEqual = Objects.equals(value, value1) || AlnTextUtils.equals(AlnTextUtils.toString(value), AlnTextUtils.toString(value1));
            if(isEqual) return true;
        }
        return false;
    }
    public boolean equalsIgnoreCase2(final String key, final Object... values) {
        final String value = get(key);
        for(final Object value1 : values) {
            final boolean isEqual = Objects.equals(value, value1) || AlnTextUtils.equalsIgnoreCase(value, AlnTextUtils.toString(value1));
            if(isEqual) return true;
        }
        return false;
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
