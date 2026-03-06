package com.example.bt6_laptrinhmang;

public class Message {
    private String content;
    private boolean isSentByMe;  // true = tin mình gửi, false = tin nhận được

    public Message(String content, boolean isSentByMe) {
        this.content = content;
        this.isSentByMe = isSentByMe;
    }

    public String getContent() { return content; }
    public boolean isSentByMe() { return isSentByMe; }
}
