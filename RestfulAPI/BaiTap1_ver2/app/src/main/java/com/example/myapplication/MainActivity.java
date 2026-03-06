package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnFetch;
    private CountryAdapter adapter;
    private ArrayList<Country> countries;
    private ExecutorService executorService;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnFetch = findViewById(R.id.btnFetch);

        countries = new ArrayList<>();
        adapter = new CountryAdapter(this, countries);
        listView.setAdapter(adapter);

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        btnFetch.setOnClickListener(v -> fetchCountries());
    }

    // Sửa updateUI
    private void updateUI(ArrayList<Country> fetchedCountries) {
        countries.clear();
        countries.addAll(fetchedCountries);
        adapter.notifyDataSetChanged();
    }

    // Sửa fetchCountries nếu cần
    private void fetchCountries() {
        executorService.execute(() -> {
            ArrayList<Country> fetched = NetworkUtils.fetchCountries();
            handler.post(() -> {
                if (fetched.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Lỗi lấy dữ liệu!", Toast.LENGTH_SHORT).show();
                } else {
                    updateUI(fetched);
                }
            });
        });
    }
}