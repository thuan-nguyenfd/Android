package com.example.bt5_resfulapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtAmount;
    private Spinner spinnerFrom, spinnerTo;
    private ImageButton btnSwap;
    private Button btnConvert;
    private TextView txtResult;
    private ProgressBar progressBar;

    private static final String API_KEY = "1ab12317ade580a9f983e1a1"; // thay key thật

    private List<CurrencyItem> currencyList = new ArrayList<>();
    private ArrayAdapter<CurrencyItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtAmount = findViewById(R.id.edtAmount);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        btnSwap = findViewById(R.id.btnSwap);
        btnConvert = findViewById(R.id.btnConvert);
        txtResult = findViewById(R.id.txtResult);
        progressBar = findViewById(R.id.progressBar);

        // Adapter cho Spinner (hiển thị flag + code + name)
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Load danh sách currencies khi khởi động
        new LoadCurrenciesTask().execute();

        // Nút Swap
        btnSwap.setOnClickListener(v -> {
            int fromPos = spinnerFrom.getSelectedItemPosition();
            spinnerFrom.setSelection(spinnerTo.getSelectedItemPosition());
            spinnerTo.setSelection(fromPos);
        });

        // Nút Convert
        btnConvert.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString().trim();
            if (amountStr.isEmpty() || currencyList.isEmpty()) {
                txtResult.setText("Vui lòng nhập số tiền và chờ tải danh sách tiền tệ!");
                return;
            }

            CurrencyItem from = (CurrencyItem) spinnerFrom.getSelectedItem();
            CurrencyItem to = (CurrencyItem) spinnerTo.getSelectedItem();

            new ConvertTask().execute(amountStr, from.code, to.code);
        });
    }

    // Class đại diện cho mỗi loại tiền tệ
    private static class CurrencyItem {
        String code;
        String name;
        String flag;

        CurrencyItem(String code, String name) {
            this.code = code;
            this.name = name;
            this.flag = getFlagEmoji(code.substring(0, 2)); // Lấy 2 ký tự đầu làm country code (thường đúng)
        }

        @Override
        public String toString() {
            return flag + " " + code + " - " + name;
        }
    }

    // Hàm tạo flag emoji từ country code (ISO 3166-1 alpha-2)
    private static String getFlagEmoji(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) return "";
        int first = countryCode.charAt(0) - 'A' + 0x1F1E6;
        int second = countryCode.charAt(1) - 'A' + 0x1F1E6;
        return new String(Character.toChars(first)) + new String(Character.toChars(second));
    }

    // Task tải danh sách currencies từ /codes
    private class LoadCurrenciesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String urlStr = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/codes";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } catch (Exception e) {
                return "Lỗi tải danh sách: " + e.getMessage();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject json = new JSONObject(jsonStr);
                if ("success".equals(json.getString("result"))) {
                    JSONArray codesArray = json.getJSONArray("supported_codes");
                    currencyList.clear();
                    for (int i = 0; i < codesArray.length(); i++) {
                        JSONArray item = codesArray.getJSONArray(i);
                        String code = item.getString(0);
                        String name = item.getString(1);
                        currencyList.add(new CurrencyItem(code, name));
                    }
                    adapter.clear();
                    adapter.addAll(currencyList);
                    adapter.notifyDataSetChanged();

                    // Mặc định chọn VND → USD (nếu có)
                    for (int i = 0; i < currencyList.size(); i++) {
                        if (currencyList.get(i).code.equals("VND")) spinnerFrom.setSelection(i);
                        if (currencyList.get(i).code.equals("USD")) spinnerTo.setSelection(i);
                    }
                } else {
                    txtResult.setText("API lỗi: " + json.optString("error-type"));
                }
            } catch (Exception e) {
                txtResult.setText("Lỗi parse danh sách tiền tệ");
            }
        }
    }

    // Task chuyển đổi (giống trước, nhưng dùng code từ Spinner)
    private class ConvertTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            txtResult.setText("");
        }

        @Override
        protected String doInBackground(String... params) {
            String amountStr = params[0];
            String fromCode = params[1];
            String toCode = params[2];

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (Exception e) {
                return "Số tiền không hợp lệ!";
            }

            String apiUrl = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

            StringBuilder jsonResult = new StringBuilder();
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResult.append(line);
                }
                reader.close();
            } catch (Exception e) {
                return "Lỗi kết nối: " + e.getMessage();
            }

            try {
                JSONObject json = new JSONObject(jsonResult.toString());
                if (!"success".equals(json.getString("result"))) {
                    return "API lỗi: " + json.optString("error-type");
                }

                JSONObject rates = json.getJSONObject("conversion_rates");

                double rateFromUSD_to_From = rates.optDouble(fromCode, -1);
                double rateFromUSD_to_To = rates.optDouble(toCode, -1);

                if (rateFromUSD_to_From <= 0 || rateFromUSD_to_To <= 0) {
                    return "Không hỗ trợ một trong hai mã tiền tệ";
                }

                double amountInUSD = amount / rateFromUSD_to_From;
                double resultAmount = amountInUSD * rateFromUSD_to_To;

                DecimalFormat df = new DecimalFormat("#,##0.####");
                String formatted = df.format(resultAmount);

                return amount + " " + fromCode + " = " + formatted + " " + toCode +
                        "\n\n(1 " + fromCode + " ≈ " + df.format(1 / rateFromUSD_to_From) + " USD" +
                        "\n1 USD ≈ " + df.format(rateFromUSD_to_To) + " " + toCode + ")";

            } catch (Exception e) {
                return "Lỗi xử lý dữ liệu: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            txtResult.setText(result);
        }
    }
}