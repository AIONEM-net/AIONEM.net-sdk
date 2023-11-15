package aionem.net.sdk.core.auth;

import aionem.net.sdk.core.data.AlnData;
import aionem.net.sdk.web.jsp.AlnJsp;
import aionem.net.sdk.core.utils.AlnUtilsText;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Log4j2
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
    protected AlnJsp alnJsp;


    protected void init(final HttpServletRequest request, final HttpServletResponse response, final String uid, final String email, final String phone, final String password, final String uidToken, final String code) {

        this.uid = AlnUtilsText.notNull(uid);
        this.email = AlnUtilsText.notNull(email);
        this.phone = AlnUtilsText.notNull(phone);
        this.password = AlnUtilsText.notNull(password);
        this.uidToken = AlnUtilsText.notNull(uidToken);
        this.code = AlnUtilsText.notNull(code);

        this.request = request;
        this.response = response;
        this.session = request.getSession(true);
        this.alnJsp = new AlnJsp(request, response);

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
        return alnJsp.getConfigEnv();
    }

    public String getDBDriver() {
        return alnJsp.getConfig().get("db_driver");
    }
    public String getDBConnection() {
        return alnJsp.getConfig().get("db_connection");
    }
    public String getDBHost() {
        return alnJsp.getConfig().get("db_host");
    }
    public String getDBPort() {
        return alnJsp.getConfig().get("db_port");
    }
    public String getDBName() {
        return alnJsp.getConfig().get("db_name");
    }
    public String getDBUser() {
        return alnJsp.getConfig().get("db_user");
    }
    public String getDBPassword() {
        return alnJsp.getConfig().get("db_password");
    }

}
