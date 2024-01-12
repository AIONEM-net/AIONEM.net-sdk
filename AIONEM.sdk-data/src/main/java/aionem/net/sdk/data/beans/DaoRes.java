package aionem.net.sdk.data.beans;

import aionem.net.sdk.core.Env;
import aionem.net.sdk.core.utils.UtilsText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;


@Log4j2
@EqualsAndHashCode(callSuper=false)
public @lombok.Data class DaoRes extends Data {

    private long id = -1;
    private int status = -1;
    private boolean success = false;
    private String response = "";
    private Datas datas = new Datas();
    private String error = "";
    private Exception exception;


    public void setData(final JsonObject jsonObject) {
        setData(new Data(jsonObject));
    }

    public void setData(final Data data) {
        setValues(data);
    }

    public void setDataArray(final JsonArray jsonArray) {
        setDataArray(new Datas(jsonArray));
    }

    public void setDataArray(final Datas datas) {
        this.datas = datas;
    }

    public boolean hasResponse() {
        return !UtilsText.isEmpty(response);
    }

    public boolean hasData() {
        return !datas.isEmpty();
    }

    public void setSuccess(boolean success) {
        this.success = success;
        this.status = this.status == -1 ? 200 : this.status;
    }

    public void setResponse(final String response) {
        this.response = response;
        setValues(response);
    }

    public void setError(final String error) {
        this.error = error;
        this.success = false;
    }

    public void setException(final String error) {
        if(!UtilsText.isEmpty(error)) {
            setException(new Exception(error));
        }
    }
    public void setException(final Exception e) {
        this.exception = e;
        if(e != null && UtilsText.isEmpty(error)) {

            log.error("\nERROR: " + e +" : "+ e.getStackTrace()[0] +"\n");

            if(e instanceof SQLException) {
                this.error = "Connection failed";
            }else if(Env.IS_DEBUG_EXCEPTION) {
                this.error = UtilsText.notEmpty(e.getMessage(), "Something went wrong");
            }
        }
    }

    public Exception getException() {
        if(exception == null) {
            if(!UtilsText.isEmpty(error)) {
                setException(error);
            }
        }
        return exception;
    }

    public JsonElement getData() {
        return !datas.isEmpty() ? datas.toJson() : toJson();
    }

}