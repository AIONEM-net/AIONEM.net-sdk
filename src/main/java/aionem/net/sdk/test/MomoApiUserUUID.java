package aionem.net.sdk.test;

import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class MomoApiUserUUID {

    public static void main(String[] args) {

        String userUUID = "c40a132c-e520-4f56-a85f-c59e7218982e";
        userUUID = "78adacd0-621d-4772-b6f4-c6d724aa8acb";

        if(userUUID == null || userUUID.length() == 0) {
            userUUID = UUID.randomUUID().toString();
        }

        System.out.println(userUUID);

        momoApiUserUUID(userUUID);

    }

    public static void momoApiUserUUID(String x_Reference_Id) {
        try {

            String domainName = "aionem.net";
            String primaryKey = "7af9c6786eb3446f9f699d8a8c0ce600";
            primaryKey = "9e82c5f1eb524ea19b9e68f5a7b3b473";
            String baseUrl = "https://mtndeveloperapi.portal.mtn.co.rw";
            baseUrl = "https://sandbox.momodeveloper.mtn.com/v1_0";
            String link = baseUrl + "/apiuser";

            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setRequestProperty("X-Reference-Id", x_Reference_Id);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Ocp-Apim-Subscription-Key", primaryKey);

            httpURLConnection.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("providerCallbackHost", domainName);

            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response status: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_CREATED) {

            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}