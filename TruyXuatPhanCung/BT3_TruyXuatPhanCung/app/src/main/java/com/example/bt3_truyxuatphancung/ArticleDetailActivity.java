package com.example.bt3_truyxuatphancung;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ArticleDetailActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        String title       = getIntent().getStringExtra("title");
        String link        = getIntent().getStringExtra("link");
        String description = getIntent().getStringExtra("description");

        if (getSupportActionBar() != null && title != null) {
            getSupportActionBar().setTitle(title);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient());

        if (link != null && !link.isEmpty()) {
            webView.loadUrl(link);
        } else {
            String html = buildHtmlPage(title, description);
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }

        // ✅ Thay thế onBackPressed() deprecated bằng OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    private String buildHtmlPage(String title, String description) {
        if (title == null) title = "";
        if (description == null || description.isEmpty()) {
            description = "<i>Không có nội dung mô tả.</i>";
        }
        return "<!DOCTYPE html><html><head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<style>"
                + "body { font-family: sans-serif; padding: 16px; color: #222; line-height: 1.6; }"
                + "h1 { font-size: 20px; color: #1a1a2e; border-bottom: 2px solid #e63946; padding-bottom: 8px; }"
                + "p { font-size: 15px; }"
                + "img { max-width: 100%; height: auto; border-radius: 8px; margin: 8px 0; }"
                + "</style></head><body>"
                + "<h1>" + title + "</h1>"
                + "<div>" + description + "</div>"
                + "</body></html>";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}