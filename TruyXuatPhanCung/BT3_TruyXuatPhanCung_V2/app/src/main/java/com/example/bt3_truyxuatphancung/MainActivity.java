package com.example.bt3_truyxuatphancung;

import android.content.Intent;
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
    // Lưu thêm link và description tương ứng với mỗi title
    private ArrayList<String> links = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();

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

        // Khi bấm vào item → mở ArticleDetailActivity
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, ArticleDetailActivity.class);
            intent.putExtra("title", titles.get(position));
            intent.putExtra("link", links.get(position));
            intent.putExtra("description", descriptions.get(position));
            startActivity(intent);
        });

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        fetchRSS("https://www.nasa.gov/rss/dyn/breaking_news.rss");
        // fetchRSS("https://vnexpress.net/rss/tin-moi-nhat.rss");
    }

    private void fetchRSS(String urlString) {
        executorService.execute(() -> {

            ArrayList<String> fetchedTitles = new ArrayList<>();
            ArrayList<String> fetchedLinks = new ArrayList<>();
            ArrayList<String> fetchedDescriptions = new ArrayList<>();

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

                        // Dùng StringBuilder riêng cho từng field
                        StringBuilder titleBuilder = new StringBuilder();
                        StringBuilder linkBuilder = new StringBuilder();
                        StringBuilder descBuilder = new StringBuilder();

                        // Theo dõi đang ở tag nào bên trong <item>
                        String activeTag = "";

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            currentTag = parser.getName();

                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    if ("item".equalsIgnoreCase(currentTag)) {
                                        insideItem = true;
                                        titleBuilder.setLength(0);
                                        linkBuilder.setLength(0);
                                        descBuilder.setLength(0);
                                        activeTag = "";
                                    } else if (insideItem) {
                                        if ("title".equalsIgnoreCase(currentTag)) {
                                            titleBuilder.setLength(0);
                                            activeTag = "title";
                                        } else if ("link".equalsIgnoreCase(currentTag)) {
                                            linkBuilder.setLength(0);
                                            activeTag = "link";
                                        } else if ("description".equalsIgnoreCase(currentTag)) {
                                            descBuilder.setLength(0);
                                            activeTag = "description";
                                        } else {
                                            activeTag = "";
                                        }
                                    }
                                    break;

                                case XmlPullParser.TEXT:
                                    if (insideItem) {
                                        String txt = parser.getText();
                                        if (txt != null) {
                                            switch (activeTag) {
                                                case "title":       titleBuilder.append(txt); break;
                                                case "link":        linkBuilder.append(txt); break;
                                                case "description": descBuilder.append(txt); break;
                                            }
                                        }
                                    }
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (insideItem) {
                                        if ("item".equalsIgnoreCase(currentTag)) {
                                            String title = titleBuilder.toString().trim();
                                            String link  = linkBuilder.toString().trim();
                                            String desc  = descBuilder.toString().trim();

                                            if (!title.isEmpty()) {
                                                fetchedTitles.add(title);
                                                fetchedLinks.add(link);
                                                fetchedDescriptions.add(desc);
                                                Log.d("RSS", "Added: " + title);
                                            }
                                            insideItem = false;
                                        }
                                        activeTag = "";
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

            Log.d("RSS", "Total fetched: " + fetchedTitles.size());

            handler.post(() -> {
                titles.clear();
                links.clear();
                descriptions.clear();

                if (!fetchedTitles.isEmpty()) {
                    titles.addAll(fetchedTitles);
                    links.addAll(fetchedLinks);
                    descriptions.addAll(fetchedDescriptions);
                } else {
                    titles.add("Không lấy được dữ liệu - xem Logcat tag RSS");
                    links.add("");
                    descriptions.add("");
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