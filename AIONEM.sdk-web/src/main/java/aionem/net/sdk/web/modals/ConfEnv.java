package aionem.net.sdk.web.modals;


import aionem.net.sdk.core.Env;
import aionem.net.sdk.web.AioWeb;

public class ConfEnv extends Config {

    public static final String ENV_PROD = "prod";
    public static final String ENV_STAGE = "stage";
    public static final String ENV_DEV = "dev";
    public static final String ENV_LOCAL = "local";

    private static ConfEnv confEnv;
    public static ConfEnv getInstance(final AioWeb aioWeb) {
        if(confEnv == null) {
            confEnv = new ConfEnv(aioWeb);

            Env.IS_DEBUG = confEnv.isDebug();
            Env.IS_DEBUG_EXCEPTION = confEnv.isDebugException();
        }
        return confEnv;
    }

    public ConfEnv(final AioWeb aioWeb) {
        super(aioWeb);
    }

    public String getContextPath() {
        return get("contextPath", aioWeb.getContextPath());
    }

    public String getSenderID() {
        return get("senderID");
    }

    public String getDomain() {
        return get("domain");
    }

    public String getUrl() {
        return get("url");
    }

    public String getUrl(final String path) {
        return getUrl() + aioWeb.getContextPath(path);
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
        return aioWeb.getInitParameter("GoogleSignInClientID");
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

    public boolean isDebug() {
        return get("debug", Env.IS_DEBUG);
    }

    public boolean isDebugException() {
        return get("debug_exception", Env.IS_DEBUG_EXCEPTION);
    }

}
