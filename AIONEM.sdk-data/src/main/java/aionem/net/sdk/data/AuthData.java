package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsText;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;


@Log4j2
@EqualsAndHashCode(callSuper=false)
public @lombok.Data class AuthData extends Data {

    protected String id = "";
    protected String uid = "";
    protected String email = "";
    protected String phone = "";
    protected String password = "";
    protected String uidToken = "";
    protected String code = "";
    protected String passwordHash = "";
    protected String language = "";
    private confApp confApp;

    protected void init(final String uid, final String email, final String phone, final String password, final String uidToken, final String code) {

        this.uid = UtilsText.notNull(uid);
        this.email = UtilsText.notNull(email);
        this.phone = UtilsText.notNull(phone);
        this.password = UtilsText.notNull(password);
        this.uidToken = UtilsText.notNull(uidToken);
        this.code = UtilsText.notNull(code);
    }

    @Override
    public JsonObject toJson() {
        return super.toJson(this);
    }

    @Override
    public AuthData fromData(final JsonObject data) {
        super.init(this, data);
        return this;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JsonObject getUserProfile() {
        return null;
    }

    public confApp getConfApp() {
        if(confApp == null) confApp = new confApp();
        return confApp;
    }

    public boolean isUsePoolDataSource() {
        return false;
    }

    public String getDBDriver() {
        return getConfApp().getOr("db_driver", "spring.datasource.driver-class-name");
    }

    public String getDBConnection() {
        return getConfApp().getOr("db_connection", "");
    }

    public String getDBUrl() {
        return getConfApp().get("db_url", "spring.datasource.url", getDBHost() +":"+ getDBPort() +"/"+ getDBName());
    }

    public String getDBHost() {
        return getConfApp().get("db_host", "localhost");
    }

    public String getDBPort() {
        return getConfApp().get("db_port", "3306");
    }

    public String getDBName() {
        return getConfApp().getOr("db_name", "spring.datasource.name");
    }

    public String getDBUser() {
        return getConfApp().getOr("db_user", "spring.datasource.username");
    }

    public String getDBPassword() {
        return getConfApp().getOr("db_password", "spring.datasource.password");
    }

}
