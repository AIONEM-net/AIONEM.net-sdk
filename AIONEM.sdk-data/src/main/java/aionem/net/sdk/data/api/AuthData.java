package aionem.net.sdk.data.api;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.Data;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.util.ResourceBundle;


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
        super.fromData(this, data);
        return this;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JsonObject getUserProfile() {
        return null;
    }


    private ResourceBundle resourceBundle;
    public ResourceBundle getResourceBundle() {
        if(resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle("application");
        }
        return resourceBundle;
    }
    public String getResourceBundle(final String key) {
        return getResourceBundle(key, "");
    }
    public String getResourceBundle(final String key, final String defaultValue) {
        String value;
        try {
            value = getResourceBundle().getString(key);
        }catch (Exception e) {
            log.error("Error getting resource bundle {}", e.getMessage());
            value = defaultValue;
        }
        return value;
    }

    public boolean isUsePoolDataSource() {
        return false;
    }
    public String getDBDriver() {
        return getResourceBundle("db_driver");
    }
    public String getDBConnection() {
        return getResourceBundle("db_connection");
    }
    public String getDBHost() {
        return getResourceBundle("db_host");
    }
    public String getDBPort() {
        return getResourceBundle("db_port");
    }
    public String getDBName() {
        return getResourceBundle("db_name");
    }
    public String getDBUser() {
        return getResourceBundle("db_user");
    }
    public String getDBPassword() {
        return getResourceBundle("db_password");
    }

}
