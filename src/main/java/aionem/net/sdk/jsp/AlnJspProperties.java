package aionem.net.sdk.jsp;

import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.data.AlnDatas;
import aionem.net.sdk.utils.AlnTextUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@Log4j2
public @Getter class AlnJspProperties {

    private AlnData data;

    public static final String PROPERTIES = "$_properties";
    public static final String PROPERTIES_JSON = "properties.json";

    public AlnJspProperties() {
        init(new AlnData());
    }
    public AlnJspProperties(final AlnData data) {
        init(data);
    }
    public AlnJspProperties(final HttpServletRequest request, final Class<?> type) {
        this(request, AlnJsp.name(type));
    }
    public AlnJspProperties(final HttpServletRequest request, final String propertiesKey) {
        init(request, propertiesKey);
    }

    public AlnJspProperties init(final AlnData data) {
        this.data = data != null ? data : new AlnData();
        return this;
    }
    public AlnJspProperties init(final HttpServletRequest request, final Class<?> type) {
        return init(request, AlnJsp.name(type));
    }
    public AlnJspProperties init(final HttpServletRequest request, final String propertiesKey) {
        final Object properties = request.getAttribute(propertiesKey);
        data = new AlnData(properties);
        request.removeAttribute(propertiesKey);
        return this;
    }


    public String get(final String key1, final String key2) {
        return data.get(key1, key2);
    }
    public String get(final String... keys) {
        return data.get(keys);
    }
    public String getOr(final String... keys) {
        return data.getOr(keys);
    }
    public String getOrLast(final String[] keys, final boolean isOrLast) {

        return data.getOrLast(keys, isOrLast);
    }
    public boolean getBoolean(final String key) {
        return data.get(key, false);
    }
    public Object getObject(final String key) {
        return data.get(key);
    }
    public <T> T get(final String key, final T defaultValue) {
        return this.data.get(key, defaultValue);
    }
    public <T> T get(final String key, final Class<T> type) {
        return this.data.get(key, type);
    }
    public AlnData getChild(final String key) {
        return data.getChild(key);
    }
    public AlnDatas getChildren() {
        return data.getChildren();
    }
    public AlnDatas getChildren(final String key) {
        return data.getChildren(key);
    }

    public boolean has(final String key) {
        return data.has(key);
    }
    public boolean isEmpty(final String key) {
        return data.isEmpty(key);
    }
    public boolean equals2(final String key, final Object... values) {
        return data.equals2(key, values);
    }
    public boolean equalsIgnoreCase2(final String key, final Object... values) {
        return data.equalsIgnoreCase2(key, values);
    }

    public int size() {
        return data.size();
    }
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public <T> T adapt(final Class<T> type) {
        try {
            if(type.getSuperclass().isAssignableFrom(AlnData.class) || type.isAssignableFrom(AlnData.class)) {
                return type.getConstructor(this.data.getClass()).newInstance(this.data);
            }else {
                return type.getConstructor().newInstance();
            }
        }catch(Exception e) {
            log.error("\nERROR: DB JspProperties - Adapt ::" + e +"\n");
        }
        return null;
    }

    public static <T> T adapt(final HttpServletRequest request, final Class<T> type) {
        final AlnJspProperties alnJspProperties = new AlnJspProperties(request, type);
        return alnJspProperties.adapt(type);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final AlnJspProperties that = (AlnJspProperties) object;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

}
