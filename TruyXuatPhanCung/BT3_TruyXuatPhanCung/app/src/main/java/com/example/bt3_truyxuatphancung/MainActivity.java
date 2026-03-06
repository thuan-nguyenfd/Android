package com.example.bt3_truyxuatphancung;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> titles = new ArrayList<>();
    private ExecutorService executorService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                titles);

        listView.setAdapter(adapter);

        // Khởi tạo ExecutorService và Handler
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Gọi hàm lấy dữ liệu RSS
        //fetchRSS("https://vnexpress.net/rss/tin-moi-nhat.rss");
        fetchRSS("https://www.nasa.gov/rss/dyn/breaking_news.rss");

        // Dòng test (có thể comment nếu không cần)
        // titles.add("Test dữ liệu");
        // adapter.notifyDataSetChanged();
    }

    private void fetchRSS(String urlString) {
        executorService.execute(() -> {

            ArrayList<String> fetchedTitles = new ArrayList<>();

            try {
                Log.d("RSS", "Fetching from URL: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                Log.d("RSS", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    try (InputStream inputStream = connection.getInputStream()) {
                        XmlPullParser parser = Xml.newPullParser();
                        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                        parser.setInput(inputStream, "UTF-8");

                        int eventType = parser.getEventType();
                        boolean insideItem = false;
                        String currentTag = "";
                        StringBuilder textBuilder = new StringBuilder();  // <-- quan trọng: dùng StringBuilder để append nhiều TEXT
                        String title = "";

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            currentTag = parser.getName();

                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    if ("item".equalsIgnoreCase(currentTag)) {
                                        insideItem = true;
                                        title = "";
                                        textBuilder.setLength(0);  // reset builder
                                    } else if (insideItem && "title".equalsIgnoreCase(currentTag)) {
                                        textBuilder.setLength(0);  // reset cho title mới
                                    }
                                    break;

                                case XmlPullParser.TEXT:
                                    if (insideItem) {
                                        String txt = parser.getText();
                                        if (txt != null) {
                                            textBuilder.append(txt);  // append tất cả text, kể cả space
                                        }
                                    }
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (insideItem) {
                                        if ("title".equalsIgnoreCase(currentTag)) {
                                            title = textBuilder.toString().trim();
                                            Log.d("RSS", "Parsed title: [" + title + "]");  // debug xem title có nội dung không
                                        } else if ("item".equalsIgnoreCase(currentTag)) {
                                            if (!title.isEmpty()) {
                                                fetchedTitles.add(title);
                                                Log.d("RSS", "Added title: " + title);
                                            }
                                            insideItem = false;
                                        }
                                    }
                                    break;
                            }
                            eventType = parser.next();
                        }
                    }

                } else {
                    Log.e("RSS", "HTTP Error: " + responseCode);
                }

            } catch (Exception e) {
                Log.e("RSS", "Error fetching RSS: " + e.getMessage(), e);
            }

            Log.d("RSS", "Total fetched titles: " + fetchedTitles.size());

            handler.post(() -> {
                titles.clear();
                if (!fetchedTitles.isEmpty()) {
                    titles.addAll(fetchedTitles);
                } else {
                    titles.add("Không lấy được dữ liệu - xem Logcat tag RSS");
                }
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}