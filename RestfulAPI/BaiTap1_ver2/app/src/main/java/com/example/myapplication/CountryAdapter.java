package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;  // hoặc dùng Glide
import java.util.ArrayList;

public class CountryAdapter extends ArrayAdapter<Country> {

    public CountryAdapter(Context context, ArrayList<Country> countries) {
        super(context, 0, countries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_country, parent, false);
        }

        Country country = getItem(position);

        ImageView flagImage = convertView.findViewById(R.id.flagImage);
        TextView countryText = convertView.findViewById(R.id.countryText);

        // Load ảnh cờ bằng Picasso (rất tiện)
        if (country.getFlagUrl() != null && !country.getFlagUrl().isEmpty()) {
            Picasso.get()
                    .load(country.getFlagUrl())
                    .resize(120, 80)          // optional: tối ưu kích thước
                    .centerCrop()
                    .into(flagImage);
        } else {
            flagImage.setImageResource(android.R.drawable.ic_menu_gallery); // ảnh mặc định
        }

        countryText.setText(country.toString());

        return convertView;
    }
}