package aionem.net.sdk.web.jsp.modals;

import aionem.net.sdk.data.Data;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.jsp.AioJsp;
import aionem.net.sdk.web.jsp.utils.JspUtils;
import lombok.Getter;

import java.util.HashMap;


public class JspConfig {

    private static final HashMap<String, Data> mapData = new HashMap<>();

    @Getter
    private String config;
    private AioJsp aioJsp;
    private Data data = new Data();

    public static String folder = "ui.config";
    private static final String extension = ".json";
    private static final String separator = "_";
    private static final String default_config = "application";

    public JspConfig() {

    }
    public JspConfig(final AioJsp aioJsp) {
        this.init(aioJsp, default_config);
    }
    public JspConfig(final AioJsp aioJsp, final String config) {
        this.init(aioJsp, config);
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

    public void init(final AioJsp aioJsp, String config) {
        this.aioJsp = aioJsp;
        this.config = config;
        config = config + separator + aioJsp.getConfigEnv().toLowerCase();

        if(!config.endsWith(extension)) config += extension;
        if(!config.startsWith("/"+folder+"/") && !config.startsWith(folder+"/")) {
            config = "/"+folder+"/" + config;
        }

        this.data = getData(aioJsp, config);
    }

    public JspConfig getBaseConfig() {
        final JspConfig dataConfig = new JspConfig();
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

        dataConfig.data = getData(aioJsp, config);
        return dataConfig;
    }

    private static Data getData(final AioJsp aioJsp, final String config) {
        Data data = new Data();
        if(!mapData.containsKey(config) || mapData.get(config) == null) {
            final String json = JspUtils.readWebInfFile(aioJsp, config);
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
