package aionem.net.sdk.test;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class MomoApiCreateVoucher {

    public static void main(String[] args) {

        String x_Reference_Id = "";

        if(x_Reference_Id == null || x_Reference_Id.isEmpty()) {
            x_Reference_Id = UUID.randomUUID().toString();
        }

        System.out.println(x_Reference_Id);

        String bearerToken = MomoApi3AccessToken.getToken();

        momoApiCreateVoucher(bearerToken, x_Reference_Id);

    }

    public static void momoApiCreateVoucher(String bearerToken, String x_Reference_Id) {

        try {

            String callbackUrl = "aionem.net/api/pay/momo/callback";
            String primaryKey = "7af9c6786eb3446f9f699d8a8c0ce600";
            primaryKey = "9e82c5f1eb524ea19b9e68f5a7b3b473";
            String baseUrl = "https://mtndeveloperapi.portal.mtn.co.rw";
            baseUrl = "https://sandbox.momodeveloper.mtn.com";
            String link = baseUrl + "/collection/v2_0/voucher";

            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("X-Reference-Id", x_Reference_Id);
            httpURLConnection.setRequestProperty("X-Target-Environment", "sandbox"); // mtnrwanda, sandbox
            httpURLConnection.setRequestProperty("Ocp-Apim-Subscription-Key", primaryKey);
            // httpURLConnection.setRequestProperty("X-Callback-Url ", callbackUrl);
            httpURLConnection.setRequestProperty("Authorization", bearerToken);

            httpURLConnection.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();

            JsonObject jsonMoney = new JsonObject();
            jsonMoney.addProperty("amount", "100");
            jsonMoney.addProperty("currency", "EUR");
            jsonObject.add("money", jsonMoney);

            jsonObject.addProperty("externalTransactionId", "e12d9661-4684-4be5-94b7-1d57ba0502ef");
            jsonObject.addProperty("customerReference", "1234");
            jsonObject.addProperty("serviceProviderUserName", "test");

            String jsonString = "{\n" +
                    "  \"externalId\": \"e12d9661-4684-4be5-94b7-1d57ba0502ef\",\n" +
                    "  \"sender\": {\n" +
                    "    \"msisdn\": \"250782027141\"\n" +
                    "  },\n" +
                    "  \"receiver\": {\n" +
                    "    \"firstName\": \"string\",\n" +
                    "    \"surname\": \"string\",\n" +
                    "    \"birthDate\": \"string\",\n" +
                    "    \"identificationType\": \"PASS\",\n" +
                    "    \"identification\": \"string\",\n" +
                    "    \"msisdn\": \"string\",\n" +
                    "    \"alias\": \"string\",\n" +
                    "    \"email\": \"test@gmail.com\",\n" +
                    "    \"username\": \"string\",\n" +
                    "    \"externalId\": \"string\",\n" +
                    "    \"languageCode\": \"string\"\n" +
                    "  },\n" +
                    "  \"money\": {\n" +
                    "    \"amount\": \"10\",\n" +
                    "    \"currency\": \"UGX\"\n" +
                    "  },\n" +
                    "  \"secret\": \"string\",\n" +
                    "  \"message\": \"string\"\n" +
                    "}";

            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                input = jsonString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response status: " + responseCode);

            if(responseCode == HttpURLConnection.HTTP_ACCEPTED) {

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
