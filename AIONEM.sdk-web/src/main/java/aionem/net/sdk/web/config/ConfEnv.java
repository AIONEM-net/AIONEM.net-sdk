package aionem.net.sdk.web.config;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.config.ConfApp;
import aionem.net.sdk.data.utils.UtilsResource;


public class ConfEnv extends ConfApp {

    public static final String ENV_PROD = "prod";
    public static final String ENV_STAGE = "stage";
    public static final String ENV_DEV = "dev";
    public static final String ENV_LOCAL = "local";

    private static ConfEnv confEnv;
    public static ConfEnv getInstance() {
        if(confEnv == null) {
            confEnv = new ConfEnv();
        }
        return confEnv;
    }

    public ConfEnv() {
        super();
    }

    public String getHome() {
        return UtilsText.notEmpty(get("home"), "/en");
    }

    public String getSites() {
        return UtilsText.notEmpty(get("sites"), getHome());
    }

    public String getContextPath() {
        return get("contextPath", "/");
    }

    public String getContextPath(final String path) {
        return UtilsResource.path(getContextPath(), path);
    }

    public String getError() {
        return get("error", "/en/error");
    }

    public String getError(final int code) {
        return get("error_"+ code, "/en/error/404");
    }

    public String getDomain() {
        final String domain = get("domain");
        return !UtilsText.isEmpty(domain) ? domain : (isEnvLocal() ? "127.0.0.1" : "");
    }

    public String getUrl() {
        final String url = get("url");
        return !UtilsText.isEmpty(url) ? url : getDomain();
    }

    public String getUrl(final String path) {
        return UtilsResource.path(getUrl(), getContextPath(path));
    }

    public String getHost() {
        return get("host");
    }

    public boolean isEnvProd() {
        return ENV_PROD.equalsIgnoreCase(getEnv());
    }

    public boolean isEnvStage() {
        return ENV_STAGE.equalsIgnoreCase(getEnv());
    }

    public boolean isEnvDev() {
        return ENV_DEV.equalsIgnoreCase(getEnv());
    }

    public boolean isEnvLocal() {
        return ENV_LOCAL.equalsIgnoreCase(getEnv());
    }

    public boolean isEnvLocalOrNone() {
        final String env = getEnv();
        return UtilsText.isEmpty(env) || ENV_LOCAL.equalsIgnoreCase(env);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

}
