package aionem.net.sdk.web.modals;

import aionem.net.sdk.data.DaoRes;
import aionem.net.sdk.data.DataAuth;
import aionem.net.sdk.core.Env;
import aionem.net.sdk.data.Datas;
import aionem.net.sdk.data.utils.UtilsDB;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.data.utils.UtilsData;
import aionem.net.sdk.data.utils.UtilsJson;
import aionem.net.sdk.core.utils.UtilsText;
import com.google.gson.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;


@Log4j2
@Getter
public class ApiRes {

    private int status = 200;
    private boolean success = false;
    private String message = "";
    private String error = "";
    private Exception exception;
    private long counts = -1;
    private long total = -1;
    private long page = -1;
    private long pages = -1;
    private long draw = -1;
    private String token = "";
    private JsonObject data = UtilsJson.jsonObject();
    private JsonArray dataArray;


    public ApiRes() {

    }

    public ApiRes(final int status, final boolean success, final String message, final String error, final long counts, final JsonObject jsonData) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.error = error;
        this.counts = counts;
        this.data = jsonData;
    }

    public ApiRes(final int status, final boolean success, final String message, final String error, final long counts, final JsonArray jsonArray) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.error = error;
        this.counts = counts;
        this.dataArray = jsonArray;
    }

    public static ApiRes withSuccess(final int status, final String message) {
        return new ApiRes(status, true, message, "", -1, UtilsJson.jsonObject());
    }

    public static ApiRes withError(final int status, final String error) {
        return new ApiRes(status, false, "", error, -1, UtilsJson.jsonObject());
    }

    public static ApiRes noAction(final String action) {
        if(!UtilsText.isEmpty(action)) {
            return ApiRes.withError(400, "wrong action");
        }else {
            return ApiRes.withError(400, "action required");
        }
    }

    public void onSuccess(final int status, final String message) {
        onSuccess(status, message, "");
    }

    public void onSuccess(final int status, final String message, final String message1) {
        this.success = true;
        this.status = status;
        this.message = message;
        setMessage(message, message1);
    }

    public void onFailure(final int status, final String error) {
        onFailure(status, error, null, null);
    }

    public void onFailure(final int status, final String error, final Exception e) {
        onFailure(status, error, "", e);
    }

    public void onFailure(final int status, final String error, final String error1) {
        onFailure(status, error, error1, null);
    }

    public void onFailure(final int status, final String error, final String error1, final Exception e) {
        this.success = false;
        this.status = status;
        setError(error, error1);
        setException(e);
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setMessage(final String... messages) {
        String separator = " : ";
        if(messages == null) message = "";
        else if(messages.length == 1) message = messages[0];
        else if(messages.length == 2) message = messages[0] + (!UtilsText.isEmpty(messages[1]) ?separator+ messages[1] : "");
        else this.message = UtilsText.join(messages, separator);
    }

    public void setError(final String... errors) {
        String separator = " : ";
        if(errors == null) error = "";
        else if(errors.length == 1) error = errors[0];
        else if(errors.length == 2) error = errors[0] + (!UtilsText.isEmpty(errors[1]) ?separator+ errors[1] : "");
        else this.error = UtilsText.join(errors, separator);
    }

    public void setException(final Exception e) {
        this.exception = e;
        if(Env.IS_DEBUG_EXCEPTION && e != null && UtilsText.isEmpty(error)) {
            this.error = UtilsText.notEmpty(e.getMessage(), error);
        }
    }

    public void setException(final String... messages) {
        if(messages != null) {
            setException(new Exception(UtilsText.join(messages, " : ")));
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

    public void setCounts(final long counts) {
        this.counts = counts;
    }

    public void setCounts(final long counts, final long max) {
        this.counts = counts;
        this.pages = max > 0 ? (long) Math.ceil(counts / (double) max) : -1;
    }

    public void setTotal(final long total) {
        this.total = total;
    }

    public void setPage(final long page) {
        this.page = page;
    }

    public void setPages(final long pages) {
        this.pages = pages;
    }

    public void setDraw(final long draw) {
        this.draw = draw;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setData(final Data data) {
        this.data = data.toJson();
    }

    public void setData(final DataAuth dataAuth) {
        this.data = dataAuth.getUserProfile();
    }

    public void setData(final JsonObject jsonObject) {
        this.data = jsonObject;
    }

    public void setDataArray(final Datas datas) {
        this.dataArray = datas.toJson();
    }

    public void setDataArray(final JsonArray jsonArray) {
        this.dataArray = jsonArray;
    }

    @Override
    public String toString() {
        return toJsonResponse().toString();
    }

    public JsonObject toJsonResponse() {
        final JsonObject jsonResponse = UtilsJson.jsonObject();
        try {
            UtilsJson.add(jsonResponse, "status", status);
            UtilsJson.add(jsonResponse, "success", success);
            if(success) {
                if(counts > -1) {
                    UtilsJson.add(jsonResponse, "counts", counts);
                    UtilsJson.add(jsonResponse, "iTotalRecords", counts);
                    UtilsJson.add(jsonResponse, "iTotalDisplayRecords", counts);
                }
                if(total > -1) {
                    UtilsJson.add(jsonResponse, "total", total);
                }
                if(page > -1) {
                    UtilsJson.add(jsonResponse, "page", page);
                }
                if(pages > -1) {
                    UtilsJson.add(jsonResponse, "pages", pages);
                }
                if(draw > -1) {
                    UtilsJson.add(jsonResponse, UtilsDB.PAR_DRAW, draw);
                }
                if(!UtilsText.isEmpty(token)) {
                    UtilsJson.add(data, "token", token);
                }
                if(dataArray != null) {
                    jsonResponse.add("data", dataArray);
                }else {
                    jsonResponse.add("data", data);
                }
                UtilsJson.add(jsonResponse, "message", message);
            }else {
                UtilsJson.add(jsonResponse, "error", error);
                if(Env.IS_DEBUG_EXCEPTION && exception != null) {
                    final String eMessage = exception.getLocalizedMessage() +" : "+ exception.getStackTrace()[0].toString();
                    UtilsJson.add(jsonResponse, "exception", eMessage);
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : toJsonResponse " + e +"\n");
        }
        return jsonResponse;
    }

    public void setResponse(final HttpServletResponse response) throws IOException {
        setResponse(response, null);
    }
    public void setResponse(final HttpServletResponse response, final Data dataHeaders) throws IOException {
        setResponse(response, dataHeaders, "application/json");
    }
    public void setResponse(final HttpServletResponse response, final Data dataHeaders, final String contentType) throws IOException {

        if(dataHeaders != null) {
            for(final Map.Entry<String, String> header : dataHeaders.getValuesString().entrySet()) {
                response.setHeader(header.getKey(), header.getValue());
            }
        }

        response.setStatus(HttpURLConnection.HTTP_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(contentType);

        if("application/json".equalsIgnoreCase(contentType)) {
            final Gson gson = UtilsJson.getGsonPretty();
            response.getWriter().write(gson.toJson(toJsonResponse()));
        }else {
            response.getWriter().write(getMessage());
        }

        response.getWriter().close();
    }

    public void putData(final String key, final Object data) {
        try {
            if(data instanceof DataAuth) {
                UtilsJson.add(this.data, key, ((DataAuth) data).getUserProfile());
            }else if(data instanceof DaoRes) {
                UtilsJson.add(this.data, key, ((DaoRes) data).toJson());
            }else if(data instanceof Data) {
                UtilsJson.add(this.data, key, ((Data) data).toJson());
            }else if(data instanceof Datas) {
                UtilsJson.add(this.data, key, ((Datas) data).toJson());
            }else if(data instanceof JsonElement) {
                UtilsJson.add(this.data, key, data);
            }else {
                final JsonObject value = UtilsJson.toJsonObject(data);
                if(!value.isEmpty()) {
                    UtilsJson.add(this.data, key, value);
                }else {
                    this.data.addProperty(key, UtilsText.toString(data));
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Response - Put Data :: " + e +"\n");
            if(Env.IS_DEBUG) setException(e);
        }
    }

    public <T> T getData(final Class<T> type) {
        try {
            return UtilsData.adaptTo(type, data);
        }catch(Exception ignore) {
        }
        return null;
    }

    public Object getDataValue(final String key) {
        try {
            return data.get(key);
        }catch(Exception ignore) {
        }
        return null;
    }

}
