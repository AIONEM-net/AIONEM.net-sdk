package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import lombok.Getter;

import java.util.HashMap;
import java.util.ResourceBundle;


public class ConfApp {

    private static final HashMap<String, Data> mapData = new HashMap<>();

    private final String name = "application";
    @Getter
    private String env = "";
    @Getter
    private Data data = new Data();
    private ResourceBundle resourceBundle;

    private static ConfApp confApp;
    public static ConfApp getInstance() {
        if(confApp == null) {
            confApp = new ConfApp();
        }
        return confApp;
    }

    private ConfApp() {
        init();
    }

    public ConfApp init() {
        final ResourceBundle resourceBundleBase = getBaseResourceBundle();
        if(resourceBundleBase != null && resourceBundleBase.containsKey("env")) {
            env = UtilsConverter.convert(resourceBundleBase.getString("env"), env);
        }
        return init(env);
    }

    public ConfApp init(final String env) {
        this.env = env;

        String name = this.name;
        if(!UtilsText.isEmpty(env)) {
            name = "/"+ env +"/"+ name;
            this.resourceBundle = UtilsResource.getResourceBundle(name, "/ui.config/env", "/config/env");
        }else {
            this.resourceBundle = UtilsResource.getResourceBundle(name, "/ui.config", "/config");
        }

        this.data = getData(this.getClass(), name);

        return this;
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

    public String getBaseName() {
        return name;
    }

    public Data getBaseData() {
        return getData(this.getClass(), getBaseName());
    }

    public ResourceBundle getBaseResourceBundle() {
        return UtilsResource.getResourceBundle(getBaseName(), "/ui.config/", "/ui.config/env/", "/config/", "/config/env/", "/");
    }

    private static <T> Data getData(Class<T> tClass, String name) {
        Data data = null;

        if(!name.endsWith(".json")) name += ".json";

        if(mapData.containsKey(name)) {
            data = mapData.get(name);
        }
        if(data == null || data.isEmpty()) {

            String json = UtilsResource.readParentResource(tClass, name, "/ui.config/", "/ui.config/env/");
            if(UtilsText.isEmpty(json)) {
                json = UtilsResource.readResource(tClass, name, "/config/", "/config/env/", "/");
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

}
