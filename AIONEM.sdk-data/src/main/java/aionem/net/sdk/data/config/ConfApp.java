package aionem.net.sdk.data.config;

import aionem.net.sdk.core.Env;
import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.utils.UtilsResource;
import lombok.Getter;

import java.util.HashMap;
import java.util.ResourceBundle;


public class ConfApp {

    private static final HashMap<String, Data> mapData = new HashMap<>();

    @Getter
    private final String name = "application";
    private String env = "";
    private Data data = new Data();
    private ResourceBundle resourceBundle;

    private static ConfApp confApp;
    public static ConfApp getInstance() {
        if(confApp == null) {
            confApp = new ConfApp();

            Env.IS_DEBUG = confApp.isDebug();
            Env.IS_DEBUG_EXCEPTION = confApp.isDebugException();
        }
        return confApp;
    }

    public ConfApp() {
        init();
    }

    public ConfApp init() {

        final String name = UtilsResource.path(getEnv(), this.name);

        this.resourceBundle = UtilsResource.getResourceBundle(name, "/WEB-INF/ui.config/conf", "/ui.config/conf", "/");

        this.data = getData(name);

        return this;
    }

    public String getEnv() {
        env = !UtilsText.isEmpty(env) ? env : get("env");
        return env;
    }

    public String get(final String key) {
        return get(key, "");
    }

    public String getOr(final String key1, final String key2) {
        final String value = get(key1);
        return !UtilsText.isEmpty(value) ? value : get(key2);
    }

    public <T> T get(final String key1, final String key2, final T defaultValue) {
        final String value = get(key1);
        return UtilsConverter.convert(has(key1) ? value : null, get(key2, defaultValue));
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

    public boolean has(final String key) {
        if(data.has(key)) {
            return true;
        }else {
            final Data baseData = getBaseData();
            if(baseData.has(key)) {
                return true;
            }else {
                if(resourceBundle != null && resourceBundle.containsKey(key)) {
                    return true;
                }else {
                    final ResourceBundle resourceBundleBase = getBaseResourceBundle();
                    if(resourceBundleBase != null && resourceBundleBase.containsKey(key)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Data getBaseData() {
        return getData(getName());
    }

    private ResourceBundle getBaseResourceBundle() {
        return UtilsResource.getResourceBundle(getName(), "/WEB-INF/ui.config/conf", "/ui.config/conf", "/");
    }

    private static <T> Data getData(String name) {
        Data data = null;

        if(!name.endsWith(".json")) name += ".json";

        if(mapData.containsKey(name)) {
            data = mapData.get(name);
        }
        if(data == null || data.isEmpty()) {

            String json = UtilsResource.readResourceOrParent(name, "/WEB-INF/ui.config/conf", "/ui.config/conf", "/");
            if(UtilsText.isEmpty(json)) {
                json = UtilsResource.readResource(name, "/WEB-INF/ui.config/conf", "/ui.config/conf", "/");
            }

            if(!UtilsText.isEmpty(json)) {
                data = new Data(json);
                mapData.put(name, data);
            }
        }

        return data != null ? data : new Data();
    }

    public void invalidate() {
        mapData.remove(getName());
        this.env = "";
        init();
    }

    public static void invalidateAll() {
        mapData.clear();
        getInstance().invalidate();
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public static boolean isUsePoolDataSource() {
        return ConfApp.getInstance().get("db_use_pool_data_source", "spring.", false);
    }

    public static String getDBDriver() {
        return ConfApp.getInstance().get("db_driver", "spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
    }

    public static String getDBConnection() {
        return ConfApp.getInstance().get("db_connection", "jdbc:mysql");
    }

    public static String getDBUrl() {
        return ConfApp.getInstance().get("db_url", "spring.datasource.url", getDBHost() +":"+ getDBPort() +"/"+ getDBName());
    }

    public static String getDBConnectionUrl() {
        return ConfApp.getDBConnection() + "://"+ ConfApp.getDBUrl();
    }

    public static String getDBHost() {
        return ConfApp.getInstance().get("db_host", "localhost");
    }

    public static String getDBPort() {
        return ConfApp.getInstance().get("db_port", "3306");
    }

    public static String getDBName() {
        return ConfApp.getInstance().get("db_name", "spring.datasource.name", "");
    }

    public static String getDBUser() {
        return ConfApp.getInstance().get("db_user", "spring.datasource.username", "root");
    }

    public static String getDBPassword() {
        return ConfApp.getInstance().get("db_password", "spring.datasource.password", "");
    }

    public boolean isDebug() {
        return get("debug", Env.IS_DEBUG);
    }

    public boolean isDebugException() {
        return get("debug_exception", Env.IS_DEBUG_EXCEPTION);
    }

}
