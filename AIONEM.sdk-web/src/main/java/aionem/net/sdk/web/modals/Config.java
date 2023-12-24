package aionem.net.sdk.web.modals;

import aionem.net.sdk.data.Data;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.Getter;

import java.util.HashMap;
import java.util.ResourceBundle;


public class Config {

    private static final String extension = ".json";
    private static final String separator = "_";
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

    public Config(final String config) {
        this.init(aioWeb, config);
    }

    public Config(final AioWeb aioWeb, final String config) {
        this.init(aioWeb, config);
    }

    public void init(final AioWeb aioWeb, String name) {
        this.aioWeb = aioWeb;
        this.name = name;
        name = name + separator + getEnv();

        if(!name.endsWith(extension)) name += extension;

        this.data = getData(aioWeb, name);
    }

    public String getEnv() {
        final String envRequest = aioWeb.getRequest().getHeader("A-Env");
        final String envWebApp = aioWeb.getInitParameter("env");
        return getEnv(UtilsText.notEmpty(envRequest, envWebApp));
    }

    public String getEnv(String env) {
        if(ConfEnv.ENV_LOCAL.equalsIgnoreCase(env)) {
            env = ConfEnv.ENV_LOCAL;
        }else if(ConfEnv.ENV_DEV.equalsIgnoreCase(env)) {
            env = ConfEnv.ENV_DEV;
        }else if(ConfEnv.ENV_STAGE.equalsIgnoreCase(env)) {
            env = ConfEnv.ENV_STAGE;
        }else if(ConfEnv.ENV_PROD.equalsIgnoreCase(env)) {
            env = ConfEnv.ENV_PROD;
        }else {
            env = ConfEnv.ENV_LOCAL;
        }
        return UtilsText.notNull(env).toLowerCase();
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
            return getBaseConfig().data.get(key, defaultValue);
        }
    }

    public Config getBaseConfig() {
        final Config dataConfig = new Config();
        String config = this.name;
        if(!config.endsWith(extension)) config += extension;

        if(config.endsWith(separator +"local"+ extension)) {
            config = config.replace(separator +"local"+ extension, extension);
        }else if(config.endsWith(separator+"dev"+extension)) {
            config = config.replace(separator +"dev"+ extension, extension);
        }else if(config.endsWith(separator +"stage"+ extension)) {
            config = config.replace(separator+"stage"+extension, extension);
        }else if(config.endsWith(separator +"prod"+ extension)) {
            config = config.replace(separator +"prod"+ extension, extension);
        }

        dataConfig.data = getData(aioWeb, config);
        return dataConfig;
    }

    private static Data getData(final AioWeb aioWeb, final String name) {
        Data data = new Data();
        if(!mapData.containsKey(name) || mapData.get(name) == null) {
            final String json = UtilsWeb.readFileWebInfConfig(aioWeb, name);
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
