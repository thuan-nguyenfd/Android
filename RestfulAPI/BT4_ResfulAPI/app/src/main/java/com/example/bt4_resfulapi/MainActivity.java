package com.example.bt4_resfulapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText edtCity;
    private TextView txtWeather;
    private Button btnGetWeather;
    private static final String API_KEY = "175ddf2ca14f3a4da5d2aea5f629a13e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        edtCity =findViewById(R.id.edtCity);
        txtWeather=findViewById(R.id.txtWeather);
        btnGetWeather=findViewById(R.id.btnGetWeather);
        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtCity.getText().toString();
                new GetWeatherTask().execute(city);
            }
        });

    }

    private class GetWeatherTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+API_KEY+
                    "&units=metric";
            StringBuilder result = new StringBuilder();
            try{
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line ;
                while ((line = reader.readLine()) != null) {
                    result.append(line);

                }
                reader.close();
                connection.disconnect();


            }catch (Exception e){
                e.printStackTrace();
                return "Loi: "+e.getMessage();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);
                String cityName = jsonObject.getString("name");
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature=main.getDouble("temp");
                txtWeather.setText("Thanh pho: "+ cityName +"\nNhiet do: "+temperature+" " +
                        "" +
                        "do C");

            }catch (Exception e){
                txtWeather.setText("Khong lay duoc du lieu");
            }
        }
    }


}