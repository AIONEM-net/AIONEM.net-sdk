package aionem.net.sdk.test;

import com.google.gson.JsonObject;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;


public class MomoApiAccessToken {

    public static void main(String[] args) {

        String userUUID = "c40a132c-e520-4f56-a85f-c59e7218982e";
        userUUID = "78adacd0-621d-4772-b6f4-c6d724aa8acb";

        String apiKey = "a1ac56c5e6c14bb19a2df029569c1e5f";

        String bearerToken = createBearerToken(userUUID, apiKey);

        System.out.println("Basic Token: " + bearerToken);

        momoApiAccessToken(bearerToken);

    }

    public static String getToken(){
        String bearerToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSMjU2In0.eyJjbGllbnRJZCI6Ijc4YWRhY2QwLTYyMWQtNDc3Mi1iNmY0LWM2ZDcyNGFhOGFjYiIsImV4cGlyZXMiOiIyMDIzLTA4LTIxVDExOjE0OjUzLjQ0MCIsInNlc3Npb25JZCI6IjU1ZTQ4NmM2LWQyYjQtNDgyMy1iNWUwLWQxNDI0ZWI3ZWMwYiJ9.YpoYbRpQZGaYfmtmDk1VpsTuY35mgpf6w8TGMLU-BY0BAjQYdKweNPdhyOiUpUUQ_64lVJJ8iXa7X0RI24fcP-rMucPf-_PEC-Swx6-wonw62AzEQNBq8qcq0iAkm76IKmvLiHrYl_NAPkMvb-zbLbvKC_x4mK2mxXXuWmsAOYgg46aqm4o_Yy_btWMn3SQ6axi2a8FVEnaUZnyERxJPJwCNc01dvA10b_T7nYM0Ua8-Q9Z5d1BsFXleRSLiDq1tVnsOcxv1MvHk2NFx0-RISZW18CE0yBOBWTlF_NkC2ZpVesFe-L2PaC1mW8SC-pgTV4d99OAthD5lWJrbs8Byuw";
        return bearerToken;
    }

    public static String createBearerToken(String userUUID, String apiKey) {
        String authString = userUUID + ":" + apiKey;
        byte[] authBytes = authString.getBytes();
        String encodedAuthString = Base64.getEncoder().encodeToString(authBytes);
        return "Basic " + encodedAuthString;
    }

    public static void momoApiAccessToken(String basicToken) {

        try {

            String domainName = "aionem.net";
            String primaryKey = "7af9c6786eb3446f9f699d8a8c0ce600";
            primaryKey = "9e82c5f1eb524ea19b9e68f5a7b3b473";
            String baseUrl = "https://mtndeveloperapi.portal.mtn.co.rw";
            baseUrl = "https://sandbox.momodeveloper.mtn.com";
            String link = baseUrl + "/collection/token/";

            disableSslVerification();

            URL url = new URL(link);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("POST");

            httpsURLConnection.setRequestProperty("Content-Type", "application/json");
            httpsURLConnection.setRequestProperty("Ocp-Apim-Subscription-Key", primaryKey);
            httpsURLConnection.setRequestProperty("Authorization", basicToken);

            httpsURLConnection.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("providerCallbackHost", domainName);

            try (OutputStream os = httpsURLConnection.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = httpsURLConnection.getResponseCode();
            System.out.println("Response status: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Response body: " + response.toString());

            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void disableSslVerification() throws NoSuchAlgorithmException, KeyManagementException {

        TrustManager[] dummyTrustManager = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, dummyTrustManager, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

}
