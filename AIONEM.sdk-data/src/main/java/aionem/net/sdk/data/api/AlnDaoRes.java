package aionem.net.sdk.data.api;

import aionem.net.sdk.core.config.AlnEnv;
import aionem.net.sdk.core.utils.AlnUtilsText;
import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.data.AlnDataArray;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;


@Log4j2
@EqualsAndHashCode(callSuper = true)
public @Data class AlnDaoRes extends AlnData {

    private long id = -1;
    private int status = -1;
    private boolean success = false;
    private String response = "";
    private AlnData data = new AlnData();
    private AlnDataArray datas = new AlnDataArray();
    private String error = "";
    private Exception exception;


    public void setData(final JsonObject jsonObject) {
        setData(new AlnData(jsonObject));
    }
    public void setData(final AlnData data) {
        this.data = data;
    }

    public void setDataArray(final JsonArray jsonArray) {
        setDataArray(new AlnDataArray(jsonArray));
    }
    public void setDataArray(final AlnDataArray dataArray) {
        this.datas = dataArray;
    }

    public boolean hasResponse() {
        return !AlnUtilsText.isEmpty(response);
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
        fromData(new AlnData(response));
    }

    public void setError(final String error) {
        this.error = error;
        this.success = false;
    }

    public void setException(final String error) {
        if(!AlnUtilsText.isEmpty(error)) {
            setException(new Exception(error));
        }
    }
    public void setException(final Exception e) {
        this.exception = e;
        if(AlnEnv.IS_DEBUG_EXCEPTION && e != null && AlnUtilsText.isEmpty(error)) {
            this.error = AlnUtilsText.notEmpty(e.getMessage(), error);
        }
    }

    public Exception getException() {
        if(exception == null) {
            if(!AlnUtilsText.isEmpty(error)) {
                setException(error);
            }
        }
        return exception;
    }

}
