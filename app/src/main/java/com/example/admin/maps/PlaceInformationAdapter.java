package com.example.admin.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.admin.maps.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;



public class PlaceInformationAdapter implements GoogleMap.InfoWindowAdapter {

    //Variables
    private View infoWindow;
    private Context CONTEXT;

    //Widgets
    TextView placeTitle, placeInformation;

    public PlaceInformationAdapter(Context CONTEXT) {
        this.CONTEXT = CONTEXT;
        infoWindow = LayoutInflater.from(CONTEXT).inflate(R.layout.layout_information_window, null);
    }

    //To set text to this custom layout
    private void displayInformation(Marker marker, View view) {

        String title = marker.getTitle();
        placeTitle = view.findViewById(R.id.placeTitle);
        if (!title.equals("")) {
            placeTitle.setText(title);
        }

        String informationSnippet = marker.getSnippet();
        placeInformation = view.findViewById(R.id.placeInformation);
        if (!informationSnippet.equals("")) {
            placeInformation.setText(informationSnippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        displayInformation(marker, infoWindow);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        displayInformation(marker, infoWindow);
        return infoWindow;
    }
}
