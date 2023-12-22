package aionem.net.sdk.web.modals;

import aionem.net.sdk.data.Data;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.Getter;

import java.util.HashMap;


public class Config {

    public static String folder = "ui.config";
    private static final String extension = ".json";
    private static final String separator = "_";
    private static final String default_config = "application";

    private static final HashMap<String, Data> mapData = new HashMap<>();

    @Getter
    private String config;
    private AioWeb aioWeb;
    private Data data = new Data();

    public Config() {

    }
    public Config(final AioWeb aioWeb) {
        this.init(aioWeb, default_config);
    }
    public Config(final AioWeb aioWeb, final String config) {
        this.init(aioWeb, config);
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

    public void init(final AioWeb aioWeb, String config) {
        this.aioWeb = aioWeb;
        this.config = config;
        config = config + separator + aioWeb.getConfigEnv().toLowerCase();

        if(!config.endsWith(extension)) config += extension;
        if(!config.startsWith("/"+folder+"/") && !config.startsWith(folder+"/")) {
            config = "/"+folder+"/" + config;
        }

        this.data = getData(aioWeb, config);
    }

    public Config getBaseConfig() {
        final Config dataConfig = new Config();
        String config = this.config;
        if(!config.endsWith(extension)) config += extension;

        if(config.endsWith(separator+"local"+extension)) {
            config = config.replace(separator+"local"+extension, extension);
        }else if(config.endsWith(separator+"dev"+extension)) {
            config = config.replace(separator+"dev"+extension, extension);
        }else if(config.endsWith(separator+"stage"+extension)) {
            config = config.replace(separator+"stage"+extension, extension);
        }else if(config.endsWith(separator+"prod"+extension)) {
            config = config.replace(separator+"prod"+extension, extension);
        }

        if(!config.startsWith("/"+folder+"/") && !config.startsWith(folder+"/")) {
            config = "/"+folder+"/" + config;
        }

        dataConfig.data = getData(aioWeb, config);
        return dataConfig;
    }

    private static Data getData(final AioWeb aioWeb, final String config) {
        Data data = new Data();
        if(!mapData.containsKey(config) || mapData.get(config) == null) {
            final String json = UtilsWeb.readWebInfFile(aioWeb, config);
            if(!UtilsText.isEmpty(json)) {
                data = new Data();
                data.fromData(json);
                mapData.put(config, data);
            }
        }else if(mapData.containsKey(config)) {
            data = mapData.get(config);
        }
        return data != null ? data : new Data();
    }

}
