package aionem.net.sdk.data.beans;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;


@Log4j2
@EqualsAndHashCode(callSuper=false)
public @lombok.Data class DataAuth extends Data {

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

    public JsonObject getUserProfile() {
        return null;
    }

}
