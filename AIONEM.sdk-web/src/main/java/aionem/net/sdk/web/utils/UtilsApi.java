package aionem.net.sdk.web.utils;

import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.core.utils.UtilsText;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;


@Log4j2
public class UtilsApi {

    public static final String PAR_ID = "id";
    public static final String PAR_ACTION = "action";
    public static final String PAR_STATUS = "status";
    public static final String PAR_SYSTEM = "system";
    public static final String PAR_CATEGORY = "category";
    public static final String PAR_USER = "user";

    public static final int HTTP_OK = 200;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;


    public static Data getData(final HttpServletRequest request) {
        final Data data = new Data();
        final Map<String, String[]> params = request.getParameterMap();
        for(final String name : params.keySet()) {
            data.put(name, String.join(",", params.get(name)));
        }
        final String id = getId(request);
        if(!UtilsText.isEmpty(id)) {
            data.put(PAR_ID, id);
        }
        return data;
    }

    public static Data getPostData(final HttpServletRequest request) {
        Data data = new Data(getPostBody(request));
        if(data.isEmpty()) {
            data = getData(request);
        }else {
            final String id = getId(request);
            if(!UtilsText.isEmpty(id)) {
                data.put(PAR_ID, id);
            }
        }
        return data;
    }

    public static Data getPostData(final InputStream inputStream) {
        return new Data(getPostBody(inputStream));
    }

    public static Data getDataHeaders(final HttpServletRequest request) {
        final Data data = new Data();
        final Enumeration<String> params = request.getHeaderNames();
        while(params.hasMoreElements()) {
            final String name = params.nextElement();
            data.put(name, request.getHeader(name));
        }
        return data;
    }

    public static String getPostBody(final HttpServletRequest request) {
        try {
            return getPostBody(request.getInputStream());
        } catch (Exception e) {
            log.info("\nERROR: API - POST-BODY ::" + e +"\n");
        }
        return "";
    }

    public static String getPostBody(final InputStream inputStream) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final char[] charBuffer = new char[1024];
            int bytesRead;
            while((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        }catch(final Exception e) {
            log.info("\nERROR: API - POST-BODY ::" + e +"\n");
        }
        return stringBuilder.toString();
    }

    public static Part getPart(final HttpServletRequest request, final String fileName) {
        try {
            if(!request.getParts().isEmpty()) {
                return request.getPart(fileName);
            }
        }catch(final Exception e) {
            log.info("\nERROR: API - getPart ::" + e +"\n");
        }
        return null;
    }

    public static String[] getActionId(final HttpServletRequest request) {
        final String action = getAction(request);
        return new String[]{action, getId(request, action)};
    }

    public static String getAction(final HttpServletRequest request) {
        String action = UtilsText.notEmpty(request.getParameter(PAR_ACTION), request.getPathInfo());
        if(action.startsWith("/")) action = action.substring(1);
        if(action.contains("/")) action = action.substring(0, action.indexOf("/"));
        return action;
    }

    public static String getAction(final HttpServletRequest request, final Method method) {
        String action = UtilsText.notEmpty(request.getParameter(PAR_ACTION), request.getPathInfo());
        if(action.startsWith("/")) action = action.substring(1);
        if(action.contains("/")) action = action.substring(0, action.indexOf("/"));
        return action;
    }

    public static String getId(final HttpServletRequest request) {
        return getId(request, getAction(request));
    }

    public static String getId(final HttpServletRequest request, final String action) {
        String id = UtilsText.notEmpty(request.getParameter("id"), request.getPathInfo());
        if(id.startsWith("/")) id = id.substring(1);
        if(id.startsWith(action)) id = id.substring(action.length());
        if(id.endsWith("/")) id = id.substring(0, id.length()-1);
        if(id.contains("/")) id = id.substring(id.lastIndexOf("/")+1);
        return id;
    }

}
