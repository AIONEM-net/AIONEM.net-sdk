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
        final int port = getRequest().getLocalPort();
        final String localName = getRequest().getLocalAddr();
        return localName.equals("66.29.143.32") ? (port == 2027 ? ENV_PROD : ENV_STAGE) : ENV_DEV;
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
        return "66.29.143.32";
    }
    public String getDBPort() {
        return "3306";
    }
    public String getDBName() {
        return isEnvProd() ? "aionem_net" : "aionem_net_1";
    }
    public String getDBUser() {
        return "aionem_net";
    }
    public String getDBPassword() {
        return "aionem_net";
    }

}
