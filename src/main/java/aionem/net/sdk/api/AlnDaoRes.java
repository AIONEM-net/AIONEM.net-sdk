package aionem.net.sdk.api;

import aionem.net.sdk.config.AlnConfig;
import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.data.AlnDatas;
import aionem.net.sdk.utils.AlnTextUtils;
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
    private AlnDatas datas = new AlnDatas();
    private String error = "";
    private Exception exception;


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
        if(!AlnTextUtils.isEmpty(error)) {
            setException(new Exception(error));
        }
    }
    public void setException(final Exception e) {
        this.exception = e;
        if(AlnConfig.IS_DEBUG_EXCEPTION && e != null && AlnTextUtils.isEmpty(error)) {
            this.error = AlnTextUtils.notEmpty(e.getMessage(), error);
        }
    }

}
