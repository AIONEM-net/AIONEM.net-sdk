package aionem.net.sdk.core.api;

import aionem.net.sdk.core.auth.AlnAuthData;
import aionem.net.sdk.core.config.AlnEnv;
import aionem.net.sdk.core.data.AlnDatas;
import aionem.net.sdk.core.utils.AlnUtilsDB;
import aionem.net.sdk.core.data.AlnData;
import aionem.net.sdk.core.utils.AlnUtilsData;
import aionem.net.sdk.core.utils.AlnUtilsJson;
import aionem.net.sdk.core.utils.AlnUtilsText;
import com.google.gson.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;


@Log4j2
@Getter
public class AlnResponse {

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
    private JsonObject jsonData = AlnUtilsJson.jsonObject();
    private Object data;


    public AlnResponse() {
    }

    public AlnResponse(final int status, final boolean success, final String message, final String error, final long counts, final JsonObject jsonData) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.error = error;
        this.counts = counts;
        this.jsonData = jsonData;
    }

    public AlnResponse(final int status, final boolean success, final String message, final String error, final long counts, final Object data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.error = error;
        this.counts = counts;
        this.data = data;
    }

    public static AlnResponse withSuccess(final int status, final String message) {
        return new AlnResponse(status, true, message, "", -1, AlnUtilsJson.jsonObject());
    }
    public static AlnResponse withError(final int status, final String error) {
        return new AlnResponse(status, false, "", error, -1, AlnUtilsJson.jsonObject());
    }
    public static AlnResponse noAction(final String action) {
        if(!AlnUtilsText.isEmpty(action)) {
            return AlnResponse.withError(400, "wrong action");
        }else {
            return AlnResponse.withError(400, "action required");
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
        else if(messages.length == 2) message = messages[0] + (!AlnUtilsText.isEmpty(messages[1]) ?separator+ messages[1] : "");
        else this.message = AlnUtilsText.join(messages, separator);
    }
    public void setError(final String... errors) {
        String separator = " : ";
        if(errors == null) error = "";
        else if(errors.length == 1) error = errors[0];
        else if(errors.length == 2) error = errors[0] + (!AlnUtilsText.isEmpty(errors[1]) ?separator+ errors[1] : "");
        else this.error = AlnUtilsText.join(errors, separator);
    }
    public void setException(final Exception e) {
        this.exception = e;
        if(AlnEnv.IS_DEBUG_EXCEPTION && e != null && AlnUtilsText.isEmpty(error)) {
            this.error = AlnUtilsText.notEmpty(e.getMessage(), error);
        }
    }
    public void setException(final String... messages) {
        if(messages != null) {
            setException(new Exception(AlnUtilsText.join(messages, " : ")));
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
    public void setData(final Object data) {
        this.data = data;
        this.jsonData = AlnUtilsJson.toJsonObject(data);
    }

    @Override
    public String toString() {
        return toJsonResponse().toString();
    }

    public JsonObject toJsonResponse() {
        final JsonObject jsonResponse = AlnUtilsJson.jsonObject();
        try {
            AlnUtilsJson.add(jsonResponse, "status", status);
            AlnUtilsJson.add(jsonResponse, "success", success);
            if(success) {
                if(counts > -1) {
                    AlnUtilsJson.add(jsonResponse, "counts", counts);
                    AlnUtilsJson.add(jsonResponse, "iTotalRecords", counts);
                    AlnUtilsJson.add(jsonResponse, "iTotalDisplayRecords", counts);
                }
                if(total > -1) {
                    AlnUtilsJson.add(jsonResponse, "total", total);
                }
                if(page > -1) {
                    AlnUtilsJson.add(jsonResponse, "page", page);
                }
                if(pages > -1) {
                    AlnUtilsJson.add(jsonResponse, "pages", pages);
                }
                if(draw > -1) {
                    AlnUtilsJson.add(jsonResponse, AlnUtilsDB.PAR_DRAW, draw);
                }
                if(!AlnUtilsText.isEmpty(token)) {
                    AlnUtilsJson.add(jsonData, "token", token);
                }
                if(!jsonData.isEmpty()) {
                    jsonResponse.add("data", jsonData);
                }else if(data != null) {
                    if(data instanceof AlnAuthData) {
                        jsonResponse.add("data", ((AlnAuthData) data).getUserProfile());
                    }else if(data instanceof AlnDaoRes) {
                        jsonResponse.add("data", ((AlnDaoRes) data).getData());
                    }else if(data instanceof AlnData) {
                        jsonResponse.add("data", ((AlnData) data).getData());
                    }else if(data instanceof AlnDatas) {
                        jsonResponse.add("data", ((AlnDatas) data).getDatas());
                    }else if(data instanceof JsonObject) {
                        jsonResponse.add("data", (JsonObject) data);
                    }else if(data instanceof JsonArray) {
                        jsonResponse.add("data", (JsonArray) data);
                    }else if(data instanceof JsonElement) {
                        jsonResponse.add("data", (JsonElement) data);
                    }else {
                        jsonResponse.add("data", AlnUtilsJson.toJson(data));
                    }
                }
                AlnUtilsJson.add(jsonResponse, "message", message);
            }else {
                AlnUtilsJson.add(jsonResponse, "error", error);
                if(AlnEnv.IS_DEBUG_EXCEPTION && exception != null) {
                    final String eMessage = exception.getLocalizedMessage() +" : "+ exception.getStackTrace()[0].toString();
                    AlnUtilsJson.add(jsonResponse, "exception", eMessage);
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
    public void setResponse(final HttpServletResponse response, final AlnData dataHeaders) throws IOException {
        setResponse(response, dataHeaders, "application/json");
    }
    public void setResponse(final HttpServletResponse response, final AlnData dataHeaders, final String contentType) throws IOException {

        if(dataHeaders != null) {
            for(final Map.Entry<String, String> header : dataHeaders.getValuesString().entrySet()) {
                response.setHeader(header.getKey(), header.getValue());
            }
        }

        response.setStatus(HttpURLConnection.HTTP_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(contentType);

        if("application/json".equalsIgnoreCase(contentType)) {
            final Gson gson = AlnUtilsJson.getGsonPretty();
            response.getWriter().write(gson.toJson(toJsonResponse()));
        }else {
            response.getWriter().write(getMessage());
        }

        response.getWriter().close();
    }

    public void putData(final String key, final Object data) {
        try {
            if(data instanceof AlnAuthData) {
                AlnUtilsJson.add(jsonData, key, ((AlnAuthData) data).getUserProfile());
            }else if(data instanceof AlnDaoRes) {
                AlnUtilsJson.add(jsonData, key, ((AlnDaoRes) data).getData());
            }else if(data instanceof AlnData) {
                AlnUtilsJson.add(jsonData, key, ((AlnData) data).getData());
            }else if(data instanceof AlnDatas) {
                AlnUtilsJson.add(jsonData, key, ((AlnDatas) data).getDatas());
            }else if(data instanceof JsonElement) {
                AlnUtilsJson.add(jsonData, key, data);
            }else {
                final JsonObject value = AlnUtilsJson.toJsonObject(data);
                if(!value.isEmpty()) {
                    AlnUtilsJson.add(jsonData, key, value);
                }else {
                    this.jsonData.addProperty(key, AlnUtilsText.toString(data));
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Response - Put Data :: " + e +"\n");
            if(AlnEnv.IS_DEBUG) setException(e);
        }
    }

    public <T> T getData(final Class<T> type) {
        try {
            return AlnUtilsData.adaptTo(type, data);
        }catch(Exception ignore) {
        }
        return null;
    }

    public Object getDataValue(final String key) {
        try {
            return jsonData.get(key);
        }catch(Exception ignore) {
        }
        return null;
    }

}
