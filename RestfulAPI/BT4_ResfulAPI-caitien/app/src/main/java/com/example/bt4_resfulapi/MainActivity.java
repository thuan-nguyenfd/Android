package com.example.bt4_resfulapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText edtCity;
    private TextView txtTemperature, txtUnit, txtDescription, txtCityName, txtExtraInfo;
    private Button btnGetWeather, btnToggleUnit;
    private ProgressBar progressBar;

    private static final String API_KEY = "175ddf2ca14f3a4da5d2aea5f629a13e";
    private boolean isCelsius = true;
    private double currentTempC = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCity = findViewById(R.id.edtCity);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtUnit = findViewById(R.id.txtUnit);
        txtDescription = findViewById(R.id.txtDescription);
        txtCityName = findViewById(R.id.txtCityName);
        txtExtraInfo = findViewById(R.id.txtExtraInfo);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        btnToggleUnit = findViewById(R.id.btnToggleUnit);
        progressBar = findViewById(R.id.progressBar);

        btnGetWeather.setOnClickListener(v -> {
            String city = edtCity.getText().toString().trim();
            if (city.isEmpty()) {
                txtDescription.setText("Vui lòng nhập tên thành phố!");
                return;
            }
            new GetWeatherTask().execute(city);
        });

        btnToggleUnit.setOnClickListener(v -> {
            isCelsius = !isCelsius;
            updateTemperatureDisplay();
        });
    }

    private void updateTemperatureDisplay() {
        if (currentTempC == 0.0) return;

        double displayTemp = isCelsius ? currentTempC : (currentTempC * 9/5) + 32;
        String unit = isCelsius ? "°C" : "°F";

        txtTemperature.setText(String.format("%.1f", displayTemp));
        txtUnit.setText(unit);
    }

    private class GetWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            txtTemperature.setText("");
            txtUnit.setText("°C");
            txtDescription.setText("");
            txtCityName.setText("");
            txtExtraInfo.setText("");
        }

        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                    "&appid=" + API_KEY + "&units=metric&lang=vi";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    return "Lỗi: Thành phố không tồn tại hoặc lỗi API (" + responseCode + ")";
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();

                return result.toString();
            } catch (Exception e) {
                return "Lỗi kết nối: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            progressBar.setVisibility(View.GONE);

            try {
                JSONObject json = new JSONObject(jsonStr);

                if (json.has("cod") && json.getInt("cod") != 200) {
                    txtDescription.setText("Không tìm thấy thành phố!");
                    return;
                }

                String cityName = json.getString("name");
                JSONObject main = json.getJSONObject("main");
                double temp = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double feelsLike = main.getDouble("feels_like");

                currentTempC = temp;
                updateTemperatureDisplay();

                txtCityName.setText(cityName);

                JSONObject weatherObj = json.getJSONArray("weather").getJSONObject(0);
                String description = weatherObj.getString("description");
                txtDescription.setText(description.substring(0, 1).toUpperCase() + description.substring(1));

                txtExtraInfo.setText("Độ ẩm: " + humidity + "%");

            } catch (Exception e) {
                txtDescription.setText("Lỗi xử lý dữ liệu");
            }
        }
    }
}