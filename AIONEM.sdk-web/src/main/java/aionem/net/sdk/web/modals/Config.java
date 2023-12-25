package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsData;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.Getter;

import java.util.HashMap;
import java.util.ResourceBundle;


public class Config {

    private static final String extension = ".json";
    private static final String default_config = "application";

    private static final HashMap<String, Data> mapData = new HashMap<>();

    @Getter
    private String name;
    private Data data = new Data();
    private ResourceBundle resourceBundle;
    protected AioWeb aioWeb;

    public Config() {

    }

    public Config(final AioWeb aioWeb) {
        this.init(aioWeb, default_config);
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

        this.data = getData(aioWeb, !name.endsWith(extension) ? name + extension : name);
    }

    public String getEnv() {
        if(aioWeb == null) return "";
        final String envRequest = aioWeb.getRequest().getHeader("A-Env");
        final String envWebApp = aioWeb.getInitParameter("env");
        String env = UtilsText.notEmpty(envRequest, envWebApp);
        if(env.equalsIgnoreCase("${env}")) env = ConfEnv.ENV_LOCAL;
        return env.toLowerCase();
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
            final Config configBase = getBaseConfig();
            if(configBase.data.has(key)) {
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
        if(!name.endsWith(extension)) name += extension;
        if(name.contains("/")) name = name.substring(name.indexOf("/"));
        return name;
    }

    public Config getBaseConfig() {
        final Config dataConfig = new Config();
        dataConfig.data = getData(aioWeb, getBaseName());
        return dataConfig;
    }

    public ResourceBundle getBaseResourceBundle() {
        return UtilsWeb.getResourceBundleConfig(getBaseName());
    }

    private static Data getData(final AioWeb aioWeb, final String name) {
        Data data = null;
        if(!mapData.containsKey(name) || mapData.get(name) == null) {

            String json = UtilsWeb.readFileEnv(aioWeb, name);
            if(json == null) {
                json = UtilsWeb.readFileConfig(aioWeb, name);
            }

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
