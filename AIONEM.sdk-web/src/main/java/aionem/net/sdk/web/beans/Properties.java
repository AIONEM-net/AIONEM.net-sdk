package aionem.net.sdk.web.beans;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.web.AioWeb;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


@Log4j2
public @Getter class Properties {

    public static final String PROPERTIES = "$_properties";
    public static final String PROPERTIES_JSON = ".json";

    private Data data;

    public Properties() {
        init(new Data());
    }

    public Properties(final Data data) {
        init(data);
    }

    public Properties(final Resource resourceProperties) {
        init(resourceProperties);
    }

    public Properties(final AioWeb aioWeb) {
        init(aioWeb);
    }

    public Properties(final HttpServletRequest request) {
        init(request);
    }

    public Properties init(final Resource resourceProperties) {
        return init(new Data(resourceProperties.readContent()));
    }

    public Properties init(final AioWeb aioWeb) {
        if(data == null || data.isEmpty()) {
            final Resource resource = new Resource(aioWeb.getRealPathPageCurrent(Properties.PROPERTIES_JSON));
            init(resource);
        }
        return this;
    }

    public Properties init(final HttpServletRequest request) {
        if(data == null || data.isEmpty()) {
            final String properties = request.getParameter(PROPERTIES);
            init(new Data(properties));
        }
        return this;
    }

    public Properties init(final Data data) {
        this.data = data != null ? data : new Data();
        return this;
    }

    public String get(final String key1, final String key2) {
        return data.get(key1, key2);
    }

    public String get(final String... keys) {
        return data.get(keys);
    }

    public String getOr(final String... keys) {
        return getOrLast(keys, true);
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

    public Properties getChild(final String key) {
        return new Properties(data.getChild(key));
    }

    public ArrayList<Properties> getChildren() {
        return getChildren("");
    }

    public ArrayList<Properties> getChildren(final String key) {
        final ArrayList<Properties> listContents = new ArrayList<>();
        for(final Data dataProperty : data.getChildren(key)) {
            listContents.add(new Properties(dataProperty));
        }
        return listContents;
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

    public String getResourceType() {
        final String resourceType = get("resourceType");
        if(UtilsText.isEmpty(resourceType)) return "";
        if(resourceType.startsWith("/WEB-INF/")) {
            return resourceType;
        }else if(resourceType.startsWith("/ui.page")) {
            return resourceType;
        }else if(resourceType.startsWith("ui.apps")) {
            return "/WEB-INF/"+ resourceType + (!resourceType.endsWith(".jsp") ? "/.jsp" : "");
        }
        return "/WEB-INF/ui.apps/"+ resourceType + (!resourceType.endsWith(".jsp") ? "/.jsp" : "");
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        final Properties that = (Properties) object;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

}
