package com.example.bt5_laptrinhmang;  // thay bằng package của bạn

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus, tvResult;
    private Button btnTest;
    private ProgressBar progressBar;

    // File test lớn, ổn định (từ Cloudflare hoặc server speed test)
    private static final String TEST_URL = "https://speed.cloudflare.com/__down?bytes=25000000";  // ~25MB
    // Hoặc: "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png" (nhỏ hơn, test nhanh)
    // Để chính xác hơn: dùng file 100MB từ https://testdebit.info (nhưng cần tìm link direct)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvResult = findViewById(R.id.tvResult);
        btnTest = findViewById(R.id.btnTest);
        progressBar = findViewById(R.id.progressBar);

        btnTest.setOnClickListener(v -> {
            new SpeedTestTask().execute();
        });
    }

    private class SpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvStatus.setText("Đang kiểm tra tốc độ...");
            progressBar.setVisibility(View.VISIBLE);
            btnTest.setEnabled(false);
            tvResult.setText("");
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();  // Có thể thêm timeout nếu cần: .connectTimeout(10, TimeUnit.SECONDS)

            Request request = new Request.Builder()
                    .url(TEST_URL)
                    .build();

            long startTime = System.currentTimeMillis();
            long totalBytes = 0;

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "Lỗi: " + response.code() + " - " + response.message();
                }

                InputStream inputStream = response.body().byteStream();
                byte[] buffer = new byte[8192];  // buffer 8KB
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalBytes += bytesRead;
                }

                long endTime = System.currentTimeMillis();
                long timeTakenMs = endTime - startTime;

                if (timeTakenMs == 0) timeTakenMs = 1;  // tránh chia 0

                // Tính tốc độ Mbps
                double speedMbps = (totalBytes * 8.0) / (timeTakenMs / 1000.0) / 1_000_000.0;

                return String.format("Tốc độ download: %.2f Mbps\nThời gian: %d ms\nDữ liệu tải: %.2f MB",
                        speedMbps,
                        timeTakenMs,
                        totalBytes / (1024.0 * 1024.0));
            } catch (IOException e) {
                return "Lỗi kết nối: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            btnTest.setEnabled(true);
            tvStatus.setText("Kiểm tra hoàn tất");
            tvResult.setText(result);
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}