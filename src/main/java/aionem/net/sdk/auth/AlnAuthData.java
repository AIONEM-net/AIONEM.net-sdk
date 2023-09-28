package aionem.net.sdk.auth;

import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.utils.AlnTextUtils;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Log4j
@EqualsAndHashCode(callSuper=false)
public @Data class AlnAuthData extends AlnData {

    protected String id = "";
    protected String uid = "";
    protected String email = "";
    protected String phone = "";
    protected String password = "";
    protected String uidToken = "";
    protected String code = "";
    protected String passwordHash = "";
    protected String language = "";

    protected HttpSession session;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Object resourceResolver;


    protected void init(final HttpServletRequest request, final HttpServletResponse response, final String uid, final String email, final String phone, final String password, final String uidToken, final String code) {

        this.uid = AlnTextUtils.notNull(uid);
        this.email = AlnTextUtils.notNull(email);
        this.phone = AlnTextUtils.notNull(phone);
        this.password = AlnTextUtils.notNull(password);
        this.uidToken = AlnTextUtils.notNull(uidToken);
        this.code = AlnTextUtils.notNull(code);

        this.request = request;
        this.response = response;
        this.session = request.getSession(true);
        this.resourceResolver = response;

    }

    @Override
    public JsonObject getData() {
        return super.getData(this);
    }

    @Override
    public AlnAuthData fromData(final JsonObject data) {
        super.fromData(this, data);
        return this;
    }

    @Override
    public String toString() {
        return getData().toString();
    }

    public JsonObject getUserProfile() {
        return null;
    }


    public static final String ENV_PROD = "PROD";
    public static final String ENV_STAGE = "STAGE";
    public static final String ENV_DEV = "DEV";
    public static final String ENV_LOCAL = "LOCAL";

    public String getEnv() {
        String env;
        final int port = getRequest().getLocalPort();
        final String localAddress = getRequest().getLocalAddr();
        if(localAddress.equals("66.29.143.32")) {
            if(port == 443) {
                env = ENV_PROD;
            }else if(port == 80) {
                env = ENV_STAGE;
            }else {
                env = ENV_DEV;
            }
        }else {
            env = ENV_LOCAL;
        }
        return env;
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

    public String getDBDriver() {
        return "com.mysql.cj.jdbc.Driver";
    }
    public String getDBConnection() {
        return "jdbc:mysql";
    }
    public String getDBHost() {
        String dbHost;
        switch(getEnv()) {
            case ENV_LOCAL:
                dbHost = "localhost";
                break;
            case ENV_DEV:
                dbHost = "66.29.143.32";
                break;
            case ENV_STAGE:
                dbHost = "66.29.143.32";
                break;
            case ENV_PROD:
                dbHost = "66.29.143.32";
                break;
            default:
                dbHost = "localhost";
                break;
        }
        return dbHost;
    }
    public String getDBPort() {
        String dbPort;
        switch(getEnv()) {
            case ENV_LOCAL:
                dbPort = "3307";
                break;
            case ENV_DEV:
                dbPort = "3306";
                break;
            case ENV_STAGE:
                dbPort = "3306";
                break;
            case ENV_PROD:
                dbPort = "3306";
                break;
            default:
                dbPort = "3306";
                break;
        }
        return dbPort;
    }
    public String getDBName() {
        String dbName;
        switch(getEnv()) {
            case ENV_LOCAL:
                dbName = "aionem_net_local";
                break;
            case ENV_DEV:
                dbName = "aionem_net_1";
                break;
            case ENV_STAGE:
                dbName = "aionem_net_1";
                break;
            case ENV_PROD:
                dbName = "aionem_net";
                break;
            default:
                dbName = "aionem_net_1";
                break;
        }
        return dbName;
    }
    public String getDBUser() {
        String dbUser;
        switch(getEnv()) {
            case ENV_LOCAL:
                dbUser = "root";
                break;
            case ENV_DEV:
                dbUser = "aionem_net";
                break;
            case ENV_STAGE:
                dbUser = "aionem_net";
                break;
            case ENV_PROD:
                dbUser = "aionem_net";
                break;
            default:
                dbUser = "aionem_net";
                break;
        }
        return dbUser;
    }
    public String getDBPassword() {
        String dbPassword;
        switch(getEnv()) {
            case ENV_LOCAL:
                dbPassword = "";
                break;
            case ENV_DEV:
                dbPassword = "aionem_net";
                break;
            case ENV_STAGE:
                dbPassword = "aionem_net";
                break;
            case ENV_PROD:
                dbPassword = "aionem_net";
                break;
            default:
                dbPassword = "aionem_net";
                break;
        }
        return dbPassword;
    }

}
