package aionem.net.sdk.test;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class MomoApiRequestPayDeliveryNotification {

    public static void main(String[] args) {

        String x_Reference_Id = "d91f9a58-b6ad-4c78-9e22-6446073dfbec";

        String bearerToken = MomoApi3AccessToken.getToken();

        momoApiRequestPay(bearerToken, x_Reference_Id);

    }

    public static void momoApiRequestPay(String bearerToken, String x_Reference_Id) {

        try {

            String primaryKey = "7af9c6786eb3446f9f699d8a8c0ce600";
            primaryKey = "9e82c5f1eb524ea19b9e68f5a7b3b473";
            String baseUrl = "https://mtndeveloperapi.portal.mtn.co.rw";
            baseUrl = "https://sandbox.momodeveloper.mtn.com";
            String link = baseUrl + "/collection/v1_0/requesttopay/"+ x_Reference_Id +"/deliverynotification";

            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("X-Target-Environment", "sandbox"); // mtnrwanda, sandbox
            httpURLConnection.setRequestProperty("Ocp-Apim-Subscription-Key", primaryKey);
            httpURLConnection.setRequestProperty("Authorization", bearerToken);

            httpURLConnection.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("notificationMessage", "Type Your Message Here");

            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response status: " + responseCode);

            if(responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while((inputLine = in.readLine()) != null) {
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
