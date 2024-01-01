package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.Env;
import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.Getter;

import java.util.HashMap;
import java.util.ResourceBundle;


public class Config {

    private static final HashMap<String, Data> mapData = new HashMap<>();

    @Getter
    private String name = "application";
    private Data data = new Data();
    private ResourceBundle resourceBundle;
    protected AioWeb aioWeb;

    public Config() {
        init(aioWeb, name);
    }

    public Config(final AioWeb aioWeb) {
        this.init(aioWeb, name);
    }

    public Config(final String name) {
        init(aioWeb, name);
    }

    public Config(final AioWeb aioWeb, final String name) {
        init(aioWeb, name);
    }

    public void init(final AioWeb aioWeb, String name) {
        this.aioWeb = aioWeb;
        this.name = name;
        
        final String env = getEnv();
        if(!UtilsText.isEmpty(env)) {
            name = env +"/"+ name;
            this.resourceBundle = UtilsWeb.getResourceBundleEnv(name);
        }else {
            this.resourceBundle = UtilsWeb.getResourceBundleConfig(name);
            if(this.resourceBundle == null) {
                this.resourceBundle = UtilsWeb.getResourceBundleEnv(name);
            }
        }

        this.data = getData(name);
    }

    public String getEnv() {
        String env = Env.ENV;
        if(aioWeb != null) {
            final String envRequest = aioWeb.getHeader("A-Env");
            env = UtilsText.notEmpty(envRequest, env);
            if(UtilsText.isEmpty(env)) {
                final String envWebApp = aioWeb.getInitParameter("env");
                env = UtilsText.notEmpty(envRequest, envWebApp);
                if (env.equalsIgnoreCase("${env}")) env = ConfEnv.ENV_LOCAL;
            }
        }
        env = UtilsText.notEmpty(env, Env.ENV).toLowerCase();
        return env;
    }

    public String get(final String key) {
        return get(key, "");
    }

    public String getOr(final String key1, final String key2) {
        final String value = get(key1);
        return !UtilsText.isEmpty(value) ? value : get(key2);
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
        String name = this.name;
        if(name.endsWith(".json")) name = name.substring(".json".length());
        if(name.endsWith(".properties")) name = name.substring(".properties".length());
        return name;
    }

    public Data getBaseData() {
        return getData(getBaseName());
    }

    public ResourceBundle getBaseResourceBundle() {
        return UtilsWeb.getResourceBundleConfig(getBaseName());
    }

    private Data getData(String name) {
        return getData(aioWeb, name);
    }

    private static Data getData(final AioWeb aioWeb, String name) {
        Data data = null;

        if(!name.endsWith(".json")) name += ".json";

        if(mapData.containsKey(name)) {
            data = mapData.get(name);
        }
        if(data == null || data.isEmpty()) {

            String json = UtilsWeb.readFileEnv(aioWeb, name);
            if(UtilsText.isEmpty(json)) {
                json = UtilsWeb.readFileConfig(aioWeb, name);
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
    }

    public static void invalidateAll() {
        mapData.clear();
    }

}
