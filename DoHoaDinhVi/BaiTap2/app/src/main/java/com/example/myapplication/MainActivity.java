package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ fragment bản đồ
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double lat = 10.7594;
        double lng = 106.6822;


        LatLng viTriMoi = new LatLng(lat, lng);

        mMap.clear();

        mMap.addMarker(new MarkerOptions()
                .position(viTriMoi)
                .title("Trường Đại học Sài Gòn"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viTriMoi, 17));  // Zoom gần hơn (15-18) để thấy rõ khuôn viên trường
    }
}