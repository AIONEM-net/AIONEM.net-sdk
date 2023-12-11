package aionem.net.sdk.data.api;

import aionem.net.sdk.core.config.Env;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.data.DataArray;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;


@Log4j2
@EqualsAndHashCode(callSuper = true)
public @lombok.Data class DaoRes extends Data {

    private long id = -1;
    private int status = -1;
    private boolean success = false;
    private String response = "";
    private Data data = new Data();
    private DataArray datas = new DataArray();
    private String error = "";
    private Exception exception;


    public void setData(final JsonObject jsonObject) {
        setData(new Data(jsonObject));
    }
    public void setData(final Data data) {
        this.data = data;
    }

    public void setDataArray(final JsonArray jsonArray) {
        setDataArray(new DataArray(jsonArray));
    }
    public void setDataArray(final DataArray dataArray) {
        this.datas = dataArray;
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
        fromData(new Data(response));
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
        if(Env.IS_DEBUG_EXCEPTION && e != null && UtilsText.isEmpty(error)) {
            this.error = UtilsText.notEmpty(e.getMessage(), error);
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

}
