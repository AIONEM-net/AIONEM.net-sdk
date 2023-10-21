package aionem.net.sdk.test;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class MomoApi2UserKey {

    public static void main(String[] args) {

        String userUUID = "c40a132c-e520-4f56-a85f-c59e7218982e"; // apiKey =
        userUUID = "78adacd0-621d-4772-b6f4-c6d724aa8acb"; // apiKey = a1ac56c5e6c14bb19a2df029569c1e5f

        momoApiUserKey(userUUID);

    }

    public static void momoApiUserKey(String userUUID) {

        try {

            String domainName = "aionem.net";
            String primaryKey = "7af9c6786eb3446f9f699d8a8c0ce600";
            primaryKey = "9e82c5f1eb524ea19b9e68f5a7b3b473";
            String baseUrl = "https://mtndeveloperapi.portal.mtn.co.rw";
            baseUrl = "https://sandbox.momodeveloper.mtn.com/v1_0";
            String link = baseUrl + "/apiuser/"+userUUID+"/apikey";

            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");

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

                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
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

}
