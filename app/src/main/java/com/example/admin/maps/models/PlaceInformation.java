package com.example.admin.maps.models;

import android.net.Uri;
import com.google.android.gms.maps.model.LatLng;

public class PlaceInformation {

    private String placeName, placeAddress, placeID, placeContact;
    private LatLng latLng;
    private Uri placeWebsiteURI;
    private float placeRatings;

    public PlaceInformation(String placeName, String placeAddress, String placeID, String placeContact, String placeAttributions, LatLng latLng, Uri placeWebsiteURI, float placeRatings) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeID = placeID;
        this.placeContact = placeContact;
        this.latLng = latLng;
        this.placeWebsiteURI = placeWebsiteURI;
        this.placeRatings = placeRatings;
    }

    public PlaceInformation() {

    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceContact() {
        return placeContact;
    }

    public void setPlaceContact(String placeContact) {
        this.placeContact = placeContact;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Uri getPlaceWebsiteURI() {
        return placeWebsiteURI;
    }

    public void setPlaceWebsiteURI(Uri placeWebsiteURI) {
        this.placeWebsiteURI = placeWebsiteURI;
    }

    public float getPlaceRatings() {
        return placeRatings;
    }

    public void setPlaceRatings(float placeRatings) {
        this.placeRatings = placeRatings;
    }

    @Override
    public String toString() {
        return "PlaceInformation{" +
                "placeName='" + placeName + '\'' +
                ", placeAddress='" + placeAddress + '\'' +
                ", placeID='" + placeID + '\'' +
                ", placeContact='" + placeContact + '\'' +
                ", latLng=" + latLng +
                ", placeWebsiteURI=" + placeWebsiteURI +
                ", placeRatings=" + placeRatings +
                '}';
    }
}
