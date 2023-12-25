package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import lombok.Getter;

import java.util.HashMap;
import java.util.ResourceBundle;


public class ConfApp {

    private static final HashMap<String, Data> mapData = new HashMap<>();

    @Getter
    private String name = "application";
    @Getter
    private String env = "";
    private Data data = new Data();
    private ResourceBundle resourceBundle;

    private static ConfApp confApp;
    public static ConfApp getInstance() {
        if(confApp == null) {
            confApp = new ConfApp();
        }
        return confApp;
    }

    public ConfApp() {

    }

    public ConfApp(final String name) {
        init(name, env);
    }

    public ConfApp(final String name, final String env) {
        init(name, env);
    }

    public void init(String name, final String env) {
        this.name = name;
        this.env = env;

        if(!UtilsText.isEmpty(env)) {
            name = "/"+ env +"/"+ name;
            this.resourceBundle = UtilsResource.getResourceBundle(name, "/ui.config/env", "/config/env");
        }else {
            this.resourceBundle = UtilsResource.getResourceBundle(name, "/ui.config", "/config");
        }

        this.data = getData(this.getClass(), !name.endsWith(".json") ? name + ".json" : name);
    }

    public String get(final String key) {
        return get(key, "");
    }

    public String getOr(final String key1, final String key2) {
        final String value = get(key1);
        return !UtilsText.isEmpty(value) ? value : get(key2);
    }

    public String get(final String key1, final String key2, final String defaultValue) {
        final String value = get(key1);
        return UtilsText.notEmpty(!UtilsText.isEmpty(value) ? value : get(key2, defaultValue), defaultValue);
    }

    public <T> T get(final String key, final T defaultValue) {
        if(data.has(key)) {
            return data.get(key, defaultValue);
        }else {
            final ConfApp confBase = getBaseConfig();
            if(confBase.data.has(key)) {
                return getBaseConfig().get(key, defaultValue);
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

    public String getBaseName() {
        String name = this.name;
        if(!name.endsWith(".json")) name += ".json";
        if(name.contains("/")) name = name.substring(name.indexOf("/"));
        return name;
    }

    public ConfApp getBaseConfig() {
        final ConfApp dataConf = new ConfApp();
        dataConf.data = getData(this.getClass(), getBaseName());
        return dataConf;
    }

    public ResourceBundle getBaseResourceBundle() {
        return UtilsResource.getResourceBundle(getBaseName(), "/ui.config", "/ui.config/env", "/config", "/config/env");
    }

    private static <T> Data getData(Class<T> tClass, final String name) {
        Data data = null;
        if(!mapData.containsKey(name) || mapData.get(name) == null) {

            String json = UtilsResource.readParentResource(tClass, "/ui.config", "/config");

            if(!UtilsText.isEmpty(json)) {
                data = new Data(json);
                mapData.put(name, data);
            }

        }else if(mapData.containsKey(name)) {
            data = mapData.get(name);
        }
        return data != null ? data : new Data();
    }

}
