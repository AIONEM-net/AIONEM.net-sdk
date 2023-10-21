package aionem.net.sdk.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MomoApiUserDetails {

    public static void main(String[] args) {

        String userUUID = "c40a132c-e520-4f56-a85f-c59e7218982e";
        userUUID = "78adacd0-621d-4772-b6f4-c6d724aa8acb";

        momoApiUserDetails(userUUID);

    }

    public static void momoApiUserDetails(String x_Reference_Id) {

        try {

            String primaryKey = "7af9c6786eb3446f9f699d8a8c0ce600";
            primaryKey = "9e82c5f1eb524ea19b9e68f5a7b3b473";
            String baseUrl = "https://mtndeveloperapi.portal.mtn.co.rw";
            baseUrl = "https://sandbox.momodeveloper.mtn.com/v1_0";
            String link = baseUrl + "/apiuser/"+ x_Reference_Id;

            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Ocp-Apim-Subscription-Key", primaryKey);

            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response status: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

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
