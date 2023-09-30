package aionem.net.sdk.utils;

import aionem.net.sdk.data.AlnData;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;


@Log4j2
public class AlnApiUtils {

    public static final String PAR_ID = "id";
    public static final String PAR_STATUS = "status";
    public static final String PAR_SYSTEM = "system";
    public static final String PAR_CATEGORY = "category";
    public static final String PAR_USER = "user";

    public static final int HTTP_OK = 200;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;


    public static AlnData getData(final HttpServletRequest request) {
        final AlnData data = new AlnData();
        final Map<String, String[]> params = request.getParameterMap();
        for(final String name : params.keySet()) {
            data.put(name, params.get(name)[0]);
        }
        final String id = getId(request);
        if(!AlnTextUtils.isEmpty(id)) {
            data.put(PAR_ID, id);
        }
        return data;
    }

    public static AlnData getPostData(final HttpServletRequest request) {
        AlnData data = new AlnData(getPostBody(request));
        if(data.isEmpty()) {
            data = getData(request);
        }else {
            final String id = getId(request);
            if(!AlnTextUtils.isEmpty(id)) {
                data.put(PAR_ID, id);
            }
        }
        return data;
    }

    public static String getPostBody(final HttpServletRequest request) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            final char[] charBuffer = new char[1024];
            int bytesRead;
            while((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        }catch(Exception e) {
            log.info("\nERROR: API - POST-BODY ::" + e +"\n");
        }
        return stringBuilder.toString();
    }

    public static Part getPart(final HttpServletRequest request, final String fileName) {
        try {
            if(!request.getParts().isEmpty()) {
                return request.getPart(fileName);
            }
        }catch(Exception e) {
            log.info("\nERROR: API - getPart ::" + e +"\n");
        }
        return null;
    }

    public static String[] getActionId(final HttpServletRequest request) {
        final String action = getAction(request);
        return new String[]{action, getId(request, action)};
    }

    public static String getAction(final HttpServletRequest request) {
        String action = AlnTextUtils.notEmpty(request.getParameter("action"), request.getPathInfo());
        if(action.startsWith("/")) action = action.substring(1);
        if(action.contains("/")) action = action.substring(0, action.indexOf("/"));
        return action;
    }

    public static String getId(final HttpServletRequest request) {
        return getId(request, getAction(request));
    }
    public static String getId(final HttpServletRequest request, final String action) {
        String id = AlnTextUtils.notEmpty(request.getParameter("id"), request.getPathInfo());
        if(id.startsWith("/")) id = id.substring(1);
        if(id.startsWith(action)) id = id.substring(action.length());
        if(id.endsWith("/")) id = id.substring(0, id.length()-1);
        if(id.contains("/")) id = id.substring(id.lastIndexOf("/")+1);
        return id;
    }

}
