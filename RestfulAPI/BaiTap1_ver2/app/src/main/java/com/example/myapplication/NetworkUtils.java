package com.example.myapplication;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NetworkUtils {
    private static final String API_URL = "https://restcountries.com/v3.1/all?fields=name,capital,region,flags";

    public static ArrayList<Country> fetchCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // optString: gặp rỗng vẫn in ra chuỗi rỗng thay vì dừng
                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject countryJson = jsonArray.getJSONObject(i);

                    String name = countryJson.getJSONObject("name").optString("common");

                    String capital = countryJson.has("capital") ?
                            countryJson.getJSONArray("capital").optString(0) : "N/A";

                    String region = countryJson.optString("region");


                    String flagUrl = "";
                    if (countryJson.has("flags")) {
                        JSONObject flags = countryJson.getJSONObject("flags");
                        flagUrl = flags.optString("png");  // hoặc "svg" nếu bạn muốn vector
                    }

                    countries.add(new Country(name, capital, region, flagUrl));
                }
            }
        } catch (Exception e) {
            Log.e("NetworkUtils", "Error fetching API", e);
        }
        return countries;
    }
}