package aionem.net.sdk.utils;

import aionem.net.sdk.data.AlnData;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;


@Log4j
public class AlnApiUtils {


    public static AlnData getData(final HttpServletRequest request) {
        final AlnData data = new AlnData();
        final Map<String, String[]> params = request.getParameterMap();
        for(final String name : params.keySet()) {
            data.put(name, params.get(name)[0]);
        }
        return data;
    }

    public static AlnData getPostData(final HttpServletRequest request) {
        AlnData data = new AlnData(getPostBody(request));
        if(data.size() == 0) {
            data = getData(request);
        }
        return data;
    }

    public static String getPostBody(final HttpServletRequest request) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            final char[] charBuffer = new char[1024];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.info("\nERROR: API - POST-BODY ::" + e +"\n");
        }
        return stringBuilder.toString();
    }

}
