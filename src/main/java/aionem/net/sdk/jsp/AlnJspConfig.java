package aionem.net.sdk.jsp;

import aionem.net.sdk.core.data.AlnData;
import aionem.net.sdk.core.utils.AlnUtilsJsp;
import aionem.net.sdk.core.utils.AlnUtilsText;
import lombok.Getter;

import java.util.HashMap;


public class AlnJspConfig {

    private static final HashMap<String, AlnData> mapData = new HashMap<>();

    @Getter
    private String config;
    private AlnJsp alnJsp;
    private AlnData data = new AlnData();

    public static String folder = "ui.config";
    private static final String extension = ".json";
    private static final String separator = "_";
    private static final String default_config = "config";

    public AlnJspConfig() {

    }
    public AlnJspConfig(final AlnJsp alnJsp) {
        this.init(alnJsp, default_config);
    }
    public AlnJspConfig(final AlnJsp alnJsp, final String config) {
        this.init(alnJsp, config);
    }

    public String get(final String key) {
        return get(key, "");
    }
    public String getOr(final String key1, final String key2) {
        final String value = get(key1);
        return !AlnUtilsText.isEmpty(value) ? value : get(key2);
    }
    public <T> T get(final String key, final T defaultValue) {
        if(data.has(key)) {
            return data.get(key, defaultValue);
        }else {
            return getBaseConfig().data.get(key, defaultValue);
        }
    }

    public void init(final AlnJsp alnJsp, String config) {
        this.alnJsp = alnJsp;
        this.config = config;
        config = config + separator + alnJsp.getConfigEnv().toLowerCase();

        if(!config.endsWith(extension)) config += extension;
        if(!config.startsWith("/"+folder+"/") && !config.startsWith(folder+"/")) {
            config = "/"+folder+"/" + config;
        }

        this.data = getData(alnJsp, config);
    }

    public AlnJspConfig getBaseConfig() {
        final AlnJspConfig dataConfig = new AlnJspConfig();
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

        dataConfig.data = getData(alnJsp, config);
        return dataConfig;
    }

    private static AlnData getData(final AlnJsp alnJsp, final String config) {
        AlnData data = new AlnData();
        if(!mapData.containsKey(config) || mapData.get(config) == null) {
            final String json = AlnUtilsJsp.readResourceFile(alnJsp, config);
            if(!AlnUtilsText.isEmpty(json)) {
                data = new AlnData();
                data.fromData(json);
                mapData.put(config, data);
            }
        }else if(mapData.containsKey(config)) {
            data = mapData.get(config);
        }
        return data != null ? data : new AlnData();
    }

}
