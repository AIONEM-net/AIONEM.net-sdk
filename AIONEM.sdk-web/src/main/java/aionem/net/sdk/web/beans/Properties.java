package aionem.net.sdk.web.beans;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.WebContext;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Objects;


@Log4j2
public class Properties {

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

    public Properties(final WebContext webContext) {
        init(webContext);
    }

    public Properties(final HttpServletRequest request) {
        init(request);
    }

    public Properties init(final Resource resource) {
        if(resource.exists()) {
            if(resource.isFile()) {
                return init(new Data(resource.readContent()));
            }else {
                return init(resource.child(PROPERTIES_JSON));
            }
        }
        return init(new Data());
    }

    public Properties init(final WebContext webContext) {
        if(data == null || data.isEmpty()) {
            final Resource resource = new Resource(webContext.getRealPathPageCurrent(Properties.PROPERTIES_JSON));
            init(resource);
        }
        return this;
    }

    public Properties init(final HttpServletRequest request) {
        if(data == null || data.isEmpty()) {
            String properties = request.getParameter(PROPERTIES);
            if(UtilsText.isEmpty(properties)) {
                properties = (String) request.getAttribute(PROPERTIES);
            }
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

    public ArrayList<String> getArray(final String key) {
        return data.getArray(key);
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
        return data.equals(key, values);
    }

    public boolean equalsIgnoreCase2(final String key, final Object... values) {
        return data.equalsIgnoreCase(key, values);
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
        }else if(resourceType.startsWith("/ui.page/")) {
            return resourceType;
        }else if(resourceType.startsWith("ui.apps/") || resourceType.startsWith("/ui.apps/")) {
            return UtilsResource.path("/WEB-INF", resourceType + (!resourceType.endsWith(".jsp") ? "/.jsp" : ""));
        }
        return UtilsResource.path( "/WEB-INF/ui.apps", resourceType + (!resourceType.endsWith(".jsp") ? "/.jsp" : ""));
    }

    public boolean hasResourceType() {
        return !isEmpty("resourceType");
    }

    public Data toData() {
        return new Data(data.toJsonAll());
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
