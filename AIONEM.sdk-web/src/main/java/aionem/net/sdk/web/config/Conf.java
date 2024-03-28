package aionem.net.sdk.web.config;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.web.dao.ResourceResolver;
import lombok.Getter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.ResourceBundle;


public class Conf {

    private static final HashMap<String, Data> mapData = new HashMap<>();

    @Getter
    private String name = "application";
    private Data data = new Data();
    private ResourceBundle resourceBundle;

    public Conf() {
        init();
    }

    public Conf(final String name) {
        init(name);
    }

    public void init() {
        init(name);
    }

    public void init(String name) {

        if(getClass().isAnnotationPresent(Name.class)) {
            final Name webServlet = getClass().getAnnotation(Name.class);
            this.name = webServlet.value();
        }else {
            this.name = name;
        }

        final String env = getEnv();

        if(!UtilsText.isEmpty(env)) {
            name = env +"/"+ name;
            this.resourceBundle = ResourceResolver.getResourceBundleEnv(name);
        }else {
            this.resourceBundle = ResourceResolver.getResourceBundleEnv(name);
        }

        this.data = getData(name);
    }

    public String getEnv() {
        return ConfEnv.getInstance().getEnv();
    }

    public String get(final String key) {
        return get(key, "");
    }

    public Data getChild(final String key) {
        return new Data(get(key, ""));
    }

    public String getOr(final String key1, final String key2) {
        final String value = get(key1);
        return !UtilsText.isEmpty(value) ? value : get(key2);
    }

    public boolean isEmpty(final String key) {
        return UtilsText.isEmpty(get(key, ""));
    }

    public <T> T get(final String key, final T defaultValue) {
        if(data.has(key)) {
            return data.get(key, defaultValue);
        }else {
            final Data baseData = getBaseData();
            if(baseData.has(key)) {
                return baseData.get(key, defaultValue);
            }else {
                if(resourceBundle != null && resourceBundle.containsKey(key)) {
                    return UtilsConverter.convert(resourceBundle.getString(key), defaultValue);
                }else {
                    final ResourceBundle resourceBundleBase = getBaseResourceBundle();
                    if(resourceBundleBase != null && resourceBundleBase.containsKey(key)) {
                        return UtilsConverter.convert(resourceBundleBase.getString(key), defaultValue);
                    }
                }
            }
        }
        return defaultValue;
    }

    private String getBaseName() {
        String name = this.name;
        if(name.endsWith(".json")) name = name.substring(".json".length());
        if(name.endsWith(".properties")) name = name.substring(".properties".length());
        return name;
    }

    private Data getBaseData() {
        return getData(getBaseName());
    }

    private ResourceBundle getBaseResourceBundle() {
        return ResourceResolver.getResourceBundleEnv(getBaseName());
    }

    private static Data getData(String name) {
        Data data = null;

        if(!name.endsWith(".json")) name += ".json";

        if(mapData.containsKey(name)) {
            data = mapData.get(name);
        }
        if(data == null || data.isEmpty()) {

            String json = ResourceResolver.readResourceEnv(name);
            if(UtilsText.isEmpty(json)) {
                json = ResourceResolver.readResourceConfig(name);
            }

            if(!UtilsText.isEmpty(json)) {
                data = new Data(json);
                mapData.put(name, data);
            }
        }

        return data != null ? data : new Data();
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public void invalidate() {
        mapData.remove(getName());
        mapData.remove(getBaseName());
        init();
    }

    public static void invalidateAll() {
        mapData.clear();
        ConfEnv.getInstance().invalidate();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Name {

        String value();

    }
}
