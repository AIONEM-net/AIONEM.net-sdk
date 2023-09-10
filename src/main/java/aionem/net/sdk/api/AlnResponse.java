package aionem.net.sdk.api;

import aionem.net.sdk.auth.AlnAuthData;
import aionem.net.sdk.config.AlnConfig;
import aionem.net.sdk.data.AlnDatas;
import aionem.net.sdk.utils.AlnDBUtils;
import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.utils.AlnDataUtils;
import aionem.net.sdk.utils.AlnJsonUtils;
import aionem.net.sdk.utils.AlnTextUtils;
import com.google.gson.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Map;


@Log4j
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
    private JsonObject jsonData = AlnJsonUtils.jsonObject();
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
        return new AlnResponse(status, true, message, "", -1, AlnJsonUtils.jsonObject());
    }
    public static AlnResponse withError(final int status, final String error) {
        return new AlnResponse(status, false, "", error, -1, AlnJsonUtils.jsonObject());
    }
    public static AlnResponse noAction(final String action) {
        if(!AlnTextUtils.isEmpty(action)) {
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
        this.message = message + (!AlnTextUtils.isEmpty(message1) ? ": "+ message1 : "");
    }
    public void onFailure(final int status, final String error) {
        onFailure(status, error, "", null);
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
    public void setError(final String error) {
        this.error = error;
    }
    public void setError(final String error, String error1) {
        this.error = error + (AlnConfig.IS_DEBUG_EXCEPTION && !AlnTextUtils.isEmpty(error1) ? ": "+ error1 : "");
    }
    public void setException(final Exception e) {
        this.exception = e;
        if(AlnConfig.IS_DEBUG_EXCEPTION && e != null && AlnTextUtils.isEmpty(error)) {
            this.error = AlnTextUtils.notEmpty(e.getMessage(), error);
        }
    }
    public void setException(final String message) {
        if(message != null) {
            setException(new Exception(message));
        }
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
        this.jsonData = AlnJsonUtils.toJsonObject(data);
    }

    @Override
    public String toString() {
        return toJsonResponse().toString();
    }

    public JsonObject toJsonResponse() {
        final JsonObject jsonResponse = AlnJsonUtils.jsonObject();
        try {
            AlnJsonUtils.add(jsonResponse, "status", status);
            AlnJsonUtils.add(jsonResponse, "success", success);
            if(success) {
                if(counts > -1) {
                    AlnJsonUtils.add(jsonResponse, "counts", counts);
                    AlnJsonUtils.add(jsonResponse, "iTotalRecords", counts);
                    AlnJsonUtils.add(jsonResponse, "iTotalDisplayRecords", counts);
                }
                if(total > -1) {
                    AlnJsonUtils.add(jsonResponse, "total", total);
                }
                if(page > -1) {
                    AlnJsonUtils.add(jsonResponse, "page", page);
                }
                if(pages > -1) {
                    AlnJsonUtils.add(jsonResponse, "pages", pages);
                }
                if(draw > -1) {
                    AlnJsonUtils.add(jsonResponse, AlnDBUtils.PAR_DRAW, draw);
                }
                if(!AlnTextUtils.isEmpty(token)) {
                    AlnJsonUtils.add(jsonData, "token", token);
                }
                if(jsonData.size() > 0) {
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
                        jsonResponse.add("data", AlnJsonUtils.toJson(data));
                    }
                }
                AlnJsonUtils.add(jsonResponse, "message", message);
            }else {
                AlnJsonUtils.add(jsonResponse, "error", error);
                if(AlnConfig.IS_DEBUG_EXCEPTION && exception != null) {
                    String eMessage = Arrays.toString(exception.getStackTrace());
                    AlnJsonUtils.add(jsonResponse, "exception", AlnTextUtils.notEmpty(eMessage, AlnTextUtils.toString(exception)));
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
            final Gson gson = AlnJsonUtils.getGsonPretty();
            response.getWriter().write(gson.toJson(toJsonResponse()));
        }else {
            response.getWriter().write(getMessage());
        }

        response.getWriter().close();
    }

    public void putData(final String key, final Object data) {
        try {
            if(data instanceof AlnAuthData) {
                AlnJsonUtils.add(jsonData, key, ((AlnAuthData) data).getUserProfile());
            }else if(data instanceof AlnDaoRes) {
                AlnJsonUtils.add(jsonData, key, ((AlnDaoRes) data).getData());
            }else if(data instanceof AlnData) {
                AlnJsonUtils.add(jsonData, key, ((AlnData) data).getData());
            }else if(data instanceof AlnDatas) {
                AlnJsonUtils.add(jsonData, key, ((AlnDatas) data).getDatas());
            }else if(data instanceof JsonElement) {
                AlnJsonUtils.add(jsonData, key, data);
            }else {
                final JsonObject value = AlnJsonUtils.toJsonObject(data);
                if(value != null && value.size() > 0) {
                    AlnJsonUtils.add(jsonData, key, value);
                }else {
                    this.jsonData.addProperty(key, AlnTextUtils.toString(data));
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: AIONEM.NET_SDK : Response - Put Data :: " + e +"\n");
            if(AlnConfig.IS_DEBUG) setException(e);
        }
    }

    public <T> T getData(final Class<T> type) {
        try {
            return AlnDataUtils.adaptTo(type, data);
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
