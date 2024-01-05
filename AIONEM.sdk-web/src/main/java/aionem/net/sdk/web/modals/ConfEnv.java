package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.Env;
import aionem.net.sdk.core.utils.UtilsText;

public class ConfEnv extends Config {

    public static final String ENV_PROD = "prod";
    public static final String ENV_STAGE = "stage";
    public static final String ENV_DEV = "dev";
    public static final String ENV_LOCAL = "local";

    private String env = "";

    private static ConfEnv confEnv;
    public static ConfEnv getInstance() {
        if(confEnv == null) {
            confEnv = new ConfEnv();

            Env.IS_DEBUG = confEnv.isDebug();
            Env.IS_DEBUG_EXCEPTION = confEnv.isDebugException();
        }
        return confEnv;
    }

    public ConfEnv() {
        super();
    }

    public String getEnv() {
        env = !UtilsText.isEmpty(env) ? env : get("env");
        return env;
    }

    public static String getHome() {
        return UtilsText.notEmpty(getInstance().get("home"), "/en");
    }

    public static String getSites() {
        return UtilsText.notEmpty(getInstance().get("sites"), getHome());
    }

    public String getContextPath() {
        return get("contextPath", "/");
    }

    public String getContextPath(final String path) {
        String contextPath = getContextPath() +"/"+ path;
        contextPath = contextPath.replace("//", "/");
        if(contextPath.endsWith("/")) contextPath = contextPath.substring(0, contextPath.length()-1);
        return contextPath;
    }

    public String getSenderID() {
        return get("senderID");
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
        return getUrl() + getContextPath(path);
    }

    public String getHost() {
        return get("host");
    }

    public String getSenderName() {
        return get("senderName");
    }

    public String getSenderEmail() {
        return get("senderEmail");
    }

    public String getSenderPassword() {
        return get("senderPassword");
    }

    public String getSupportEmail() {
        return get("supportEmail");
    }

    public String getCcEmail() {
        return get("ccEmail");
    }

    public String getInitGoogleSignInClientID() {
        return get("GoogleSignInClientID");
    }

    public String getAPI_SMS_KEY() {
        return get("API_SMS_KEY");
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

    public boolean isDebug() {
        return get("debug", Env.IS_DEBUG);
    }

    public boolean isDebugException() {
        return get("debug_exception", Env.IS_DEBUG_EXCEPTION);
    }

    @Override
    public void invalidate() {
        this.env = "";
        super.invalidate();
    }

}
