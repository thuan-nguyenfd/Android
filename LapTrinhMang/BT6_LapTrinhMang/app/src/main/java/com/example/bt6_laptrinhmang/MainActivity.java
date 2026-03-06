package com.example.bt6_laptrinhmang;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private static final int PORT = 12345;
    private TextView tvStatus;
    private EditText etServerIp, etMessage;
    private Button btnStartServer, btnConnect, btnSend;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected = false;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tv_status);
        etServerIp = findViewById(R.id.et_server_ip);
        etMessage = findViewById(R.id.et_message);
        btnStartServer = findViewById(R.id.btn_start_server);
        btnConnect = findViewById(R.id.btn_connect);
        btnSend = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new MessageAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Hiển thị IP của thiết bị này (dùng để nhập ở thiết bị kia)
        String myIp = getLocalIpAddress();
        tvStatus.setText("IP của thiết bị này: " + myIp + "\nStatus: Ready");

        btnStartServer.setOnClickListener(v -> startServer());
        btnConnect.setOnClickListener(v -> connectAsClient());
        btnSend.setOnClickListener(v -> sendMessage());

        btnSend.setEnabled(false); // chỉ bật khi đã kết nối
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);
                updateUI("Đang chờ kết nối trên port " + PORT + "...");

                socket = serverSocket.accept();
                updateUI("Đã kết nối từ: " + socket.getInetAddress().getHostAddress());

                isConnected = true;
                mainHandler.post(() -> btnSend.setEnabled(true));

                // Lấy input stream để nhận tin
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Thread đọc tin nhắn đến
                new Thread(this::readIncomingMessages).start();

            } catch (IOException e) {
                updateUI("Lỗi Server: " + e.getMessage());
                Log.e("TCP", "Server error", e);
            }
        }).start();
    }

    private void connectAsClient() {
        String serverIp = etServerIp.getText().toString().trim();
        if (serverIp.isEmpty()) {
            Toast.makeText(this, "Nhập IP của Server", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                socket = new Socket(serverIp, PORT);
                updateUI("Đã kết nối tới " + serverIp);

                isConnected = true;
                mainHandler.post(() -> btnSend.setEnabled(true));

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Thread đọc tin nhắn đến
                new Thread(this::readIncomingMessages).start();

            } catch (IOException e) {
                updateUI("Lỗi kết nối: " + e.getMessage());
                Log.e("TCP", "Client error", e);
            }
        }).start();
    }

    private void sendMessage() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty() || !isConnected || out == null) {
            Toast.makeText(this, "Không thể gửi: chưa kết nối hoặc tin nhắn rỗng", Toast.LENGTH_SHORT).show();
            return;
        }

        // PHẢI chạy gửi trong thread riêng
        new Thread(() -> {
            try {
                out.println(msg);
                out.flush();  // đảm bảo gửi ngay
                addMessageToUI(msg, true);
                runOnUiThread(() -> etMessage.setText(""));
            } catch (Exception e) {
                Log.e("TCP-Send", "Lỗi gửi tin: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void readIncomingMessages() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                final String received = line;
                addMessageToUI(received, false); // tin nhận được
            }
        } catch (IOException e) {
            updateUI("Kết nối bị ngắt: " + e.getMessage());
        } finally {
            isConnected = false;
            mainHandler.post(() -> btnSend.setEnabled(false));
        }
    }

    private void addMessageToUI(String content, boolean isSentByMe) {
        mainHandler.post(() -> adapter.addMessage(new Message(content, isSentByMe)));
    }

    private void updateUI(String text) {
        mainHandler.post(() -> tvStatus.setText(text));
    }

    // Lấy IP WiFi của thiết bị
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return "Không lấy được IP";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            // ignore
        }
    }
}