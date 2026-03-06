package com.example.bt6_laptrinhmang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message msg) {
        messages.add(msg);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.textView.setText(msg.getContent());

        if (msg.isSentByMe()) {
            // Tin mình gửi: xanh dương nhạt, chữ trắng cho dễ đọc
            holder.textView.setBackgroundColor(0xFF2196F3);  // xanh dương Material
            holder.textView.setTextColor(0xFFFFFFFF);        // trắng
            holder.textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.textView.setPadding(20, 12, 20, 12);      // padding cho đẹp bubble
        } else {
            // Tin nhận: xám nhạt, chữ đen
            holder.textView.setBackgroundColor(0xFFE0E0E0);  // xám nhạt
            holder.textView.setTextColor(0xFF000000);        // đen
            holder.textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.textView.setPadding(20, 12, 20, 12);
        }

        // Thêm margin để giống bubble hơn
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        params.setMargins(16, 8, 16, 8);  // trái phải trên dưới
        holder.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}