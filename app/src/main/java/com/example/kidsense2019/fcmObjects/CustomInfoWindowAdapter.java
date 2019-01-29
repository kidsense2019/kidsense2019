package com.example.kidsense2019.fcmObjects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.kidsense2019.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context Context) {
        mContext = Context;
        mWindow = LayoutInflater.from(Context).inflate(R.layout.custom_location_info, null);
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = (TextView)view.findViewById(R.id.title);

        if (!title.equals("")){
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = (TextView)view.findViewById(R.id.snippet);

        if (!snippet.equals("")){
            tvSnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,mWindow);
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,mWindow);
        return mWindow;
    }
}
