package com.example.bt3_laptrinhmang;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {

    private TextView chatBox;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatBox = findViewById(R.id.chatBox);


        new Thread(new ServerThread()).start();
    }

    class ServerThread implements Runnable {
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(43359);

                handler.post(() ->
                        chatBox.append("\n[Server] Đang lắng nghe...")
                );

                Socket client = serverSocket.accept();

                handler.post(() ->
                        chatBox.append("\n[Server] Client đã kết nối")
                );

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(),true);

                while (true) {

                    final String message = in.readLine();
                    if (message == null) break;



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
                                chatBox.append("\nClient: " + finalDisplay)
                        );
                        if(finalDisplay.equalsIgnoreCase("exit")) break;
                    }


                     client.close();
                        serverSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
