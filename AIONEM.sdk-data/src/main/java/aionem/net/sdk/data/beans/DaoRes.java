package aionem.net.sdk.data.beans;

import aionem.net.sdk.core.Env;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsData;
import com.google.gson.JsonElement;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.net.HttpURLConnection;
import java.sql.SQLException;


@Log4j2
@EqualsAndHashCode(callSuper=false)
public @lombok.Data class DaoRes {

    private long id = -1;
    private int status = -1;
    private double progress = 0;
    private boolean success = false;
    private String response = "";
    private Data data = new Data();
    private Datas datas = new Datas();
    private String error = "";
    private Exception exception;

    public void put(final String key, final Object value) {
        data.put(key, value);
    }

    public void setData(final Data data) {
        this.data.setValues(data.getValues());
    }

    public void setDataArray(final Datas datas) {
        this.datas = datas;
    }

    public void setSuccess(boolean success) {
        this.success = success;
        this.status = this.status == -1 ? HttpURLConnection.HTTP_OK : this.status;
    }

    public void setResponse(final String response) {
        this.response = response;
        data.setValues(response);
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

    public <T> T getData(final Class<T> type, final T defaultValue) throws Exception {
        try {
            return getData(type);
        }catch(final Exception e) {
            return defaultValue;
        }
    }

    public <T> T getData(final Class<T> type) throws Exception {
        return UtilsData.adaptTo(type, this.data);
    }

    public int getStatus() {
        if(status == -1) {
            status = success ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
        return status;
    }

    public Exception getException() {
        if(exception == null) {
            if(!UtilsText.isEmpty(error)) {
                setException(error);
            }
        }
        return exception;
    }

    public boolean hasResponse() {
        return !UtilsText.isEmpty(response);
    }

    public boolean hasData() {
        return !data.isEmpty() || !datas.isEmpty();
    }

    public JsonElement toJson() {
        return !datas.isEmpty() ? datas.toJson() : data.toJson();
    }

}
