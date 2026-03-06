package com.example.bt2_laptrinhmang;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Button startButton, stopButton;
    private ProgressBar progressBar;
    private Handler handler = new Handler(Looper.getMainLooper());

    // Biến cờ để kiểm soát việc chạy/dừng của Thread
    private volatile boolean isCounting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        progressBar = findViewById(R.id.progressBar);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCounting) { // Chỉ bắt đầu nếu chưa đếm
                    startCounting();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCounting = false; // Gán bằng false để dừng vòng lặp trong Thread
                textView.setText("Đã dừng!");
            }
        });
    }

    private void startCounting() {
        isCounting = true;
        progressBar.setProgress(0); // Reset progress về 0

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    // Nếu bấm Stop (isCounting = false) thì thoát vòng lặp ngay
                    if (!isCounting) return;

                    final int count = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(String.valueOf(count));
                            progressBar.setProgress(count); // Cập nhật progress bar
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Khi đếm xong 10 và không bị dừng giữa chừng
                if (isCounting) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Hoàn thành!");
                            isCounting = false;
                        }
                    });
                }
            }
        }).start();
    }
}