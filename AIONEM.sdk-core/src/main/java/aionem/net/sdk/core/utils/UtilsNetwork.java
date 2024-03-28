package aionem.net.sdk.core.utils;

import javax.net.ssl.*;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


public class UtilsNetwork {


    public static String addParameter(String url, final HashMap<String, String> params) {
        for(final String name : params.keySet()) {
            final String value = params.get(name);
            url += getParameter(url, name, value);
        }
        return url;
    }
    public static String addParameter(final String url, final Map<String, String[]> paramsMap) {
        final HashMap<String, String> params = new HashMap<>();
        for(final String name : paramsMap.keySet()) {
            params.put(name, paramsMap.get(name)[0]);
        }
        return addParameter(url, params);
    }
    public static String addParameter(final String url, final String name, final String value) {
        return url + getParameter(url, name, value);
    }

    public static String getParameter(final String url, final String name, final String value) {
        final int indexQ = url.indexOf('?');
        final int indexH = url.indexOf('#');
        final char separator = indexQ == -1 ? '?' : '&';
        final String param = separator + encodeUrl(name) + '=' + encodeUrl(value);
        return indexH == -1 ? param : url.substring(0, indexH) + param + url.substring(indexH);
    }

    public static String encodeUrl(final String url) {
        return encodeUrl(url, "UTF-8");
    }
    public static String encodeUrl(final String url, final String encoding) {
        try {
            return URLEncoder.encode(url, encoding);
        }catch(final Exception ignore) {
            return url;
        }
    }

    private static boolean isDisabledSslVerification = false;
    public static void disableSslVerification() throws NoSuchAlgorithmException, KeyManagementException {
        if(!isDisabledSslVerification) {

            final TrustManager[] dummyTrustManager = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                }

                public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                }
            }};

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, dummyTrustManager, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            final HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            };

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            isDisabledSslVerification = true;
        }
    }

}

