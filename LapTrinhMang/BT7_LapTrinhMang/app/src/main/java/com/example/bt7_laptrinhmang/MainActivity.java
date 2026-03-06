package com.example.bt7_laptrinhmang;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText etHost;
    private Button btnPing;
    private TextView tvResult;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etHost = findViewById(R.id.etHost);
        btnPing = findViewById(R.id.btnPing);
        tvResult = findViewById(R.id.tvResult);
        progressBar = findViewById(R.id.progressBar);

        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = etHost.getText().toString().trim();
                if (host.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập server!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Chạy ping trong luồng nền
                new PingTask().execute(host);
            }
        });
    }

    // AsyncTask để chạy lệnh ping không chặn UI
    private class PingTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvResult.setText("Đang đo...");
            progressBar.setVisibility(View.VISIBLE);
            btnPing.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String host = params[0];
            String urlStr = "https://connectivitycheck.gstatic.com/generate_204";  // hoặc "https://www.google.com/generate_204"

            long startTime = System.currentTimeMillis();
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setInstanceFollowRedirects(false);
                connection.connect();

                int responseCode = connection.getResponseCode();
                long timeTaken = System.currentTimeMillis() - startTime;

                if (responseCode == 204 || responseCode == 200) {
                    return "Kết nối thành công!\nThời gian phản hồi: " + timeTaken + " ms";
                } else {
                    return "Kết nối thất bại (mã: " + responseCode + ")\nThời gian: " + timeTaken + " ms";
                }
            } catch (Exception e) {
                long timeTaken = System.currentTimeMillis() - startTime;
                return "Lỗi kết nối: " + e.getMessage() + "\nThời gian thử: " + timeTaken + " ms";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            btnPing.setEnabled(true);
            tvResult.setText(result);
        }
    }
}