package com.example.bt3_laptrinhmang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {

    private TextView chatBox;
    private EditText messageInput;
    private Button sendButton;
    private PrintWriter out;
    private Handler handler = new Handler();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatBox = findViewById(R.id.chatBox);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);


        new Thread(new ClientThread()).start();
        sendButton.setOnClickListener(v->sendMessage());

    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                Socket socket = new Socket("192.168.28.162", 43359);
                out = new PrintWriter(socket.getOutputStream(),true);

                handler.post(() ->
                        chatBox.append("\n[Client] Kết nối server thành công")
                );
            } catch (Exception e) {
                handler.post(() ->
                        chatBox.append("\n[Client] Lỗi kết nối server")
                );
                e.printStackTrace();
            }
        }
    }

    private void sendMessage() {
        String message = messageInput.getText().toString();
        if (out != null && !message.isEmpty()) {

            new Thread(() -> {
                out.println(message);
            }).start();
            String display;
            switch (message) {
                case ":))":
                    display = "😄";
                    break;
                case ":)))":
                    display = "😂";
                    break;
                case ":D":
                    display = "😁";
                    break;
                default:
                    display = message;
            }

            String finalDisplay = display;

            handler.post(() ->
                    chatBox.append("\nBạn: " + finalDisplay)
            );

            messageInput.setText("");
        }
    }







}
