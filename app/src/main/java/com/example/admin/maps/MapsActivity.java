package com.example.admin.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.maps.models.PlaceInformation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    //Constants
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PEERMISSION_REQUEST_CODE = 1234;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168), new LatLng(71,136));

    //Variables
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private PlacesAutocompleteAdapter placesAutocompleteAdapter;
    private PlaceInformation placeInformation;
    private Marker mMarker;


    //Elements
    private AutoCompleteTextView editText;
    private ImageButton myLocation, locationInformation, placePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        editText = findViewById(R.id.searchEditText);

         googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                 .enableAutoManage(this, this)
                 .build();

        myLocation = findViewById(R.id.myLocation);

        locationInformation = findViewById(R.id.locationInformation);

        placePicker = findViewById(R.id.placePicker);

        getLocationPermission();
    }

    //To Get Permissions
    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == getPackageManager().PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == getPackageManager().PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PEERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PEERMISSION_REQUEST_CODE);
        }
    }


    //Check Permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PEERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    //Initialize the Map
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //Map CallBack
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady: Called");
        Toast.makeText(this, "Map is Ready!", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }

    //To get Current Location
    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: LocationFound!");

                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My location");
                        } else {
                            Log.d(TAG, "onComplete: Location Not found!");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation Security exception " + e.getMessage());
        }
    }

    //To Move camera and get Information of the Location
    private void moveCamera(LatLng latlng, float zoom, PlaceInformation placeInformation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        mMap.clear();
        mMap.setInfoWindowAdapter(new PlaceInformationAdapter(MapsActivity.this));

        if(placeInformation != null) {
            try {
                String snippet = "Name: " + placeInformation.getPlaceName() + "\n" +
                        "Address: " + placeInformation.getPlaceAddress() + "\n" +
                        "Contact: " + placeInformation.getPlaceContact() + "\n" +
                        "Website: " + placeInformation.getPlaceWebsiteURI() + "\n" +
                        "Ratings: " + placeInformation.getPlaceRatings() + "\n";

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latlng)
                        .title(placeInformation.getPlaceName())
                        .snippet(snippet);

                mMarker = mMap.addMarker(markerOptions);


            } catch(NullPointerException e) {
                Log.d(TAG, "moveCamera: Null Place Information: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latlng));
        }

        hideSoftwareKeyboard();
    }


    //To set Camera to the Location
    private void moveCamera(LatLng latlng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        if (!title.equals("My location")) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latlng)
                    .title(title);

            mMap.addMarker(markerOptions);
        }

        hideSoftwareKeyboard();
    }

    //To Initialize Location Search & Auto complete
    private void init() {

        placesAutocompleteAdapter = new PlacesAutocompleteAdapter(getApplicationContext(), googleApiClient, LAT_LNG_BOUNDS, null);

        editText.setAdapter(placesAutocompleteAdapter);
        editText.setOnItemClickListener(autoCompleteOnclickListner);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if( actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //For Searching
                    geoLocate();
                    return true;
                }

                return false;
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                getDeviceLocation();
            }
        });

        locationInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mMarker.isInfoWindowShown()) {
                        Log.d(TAG, "onClick: Information is Already displayed!");
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onClick: Place Information: " + placeInformation.toString());
                        mMarker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "onClick: NullPointerException: " + e.getMessage());
                }
            }
        });

        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int PLACE_PICKER_REQUEST = 1;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
                }
            }
        });
    }

    //To pick a location
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(googleApiClient, place.getId());
                placeResult.setResultCallback(updatePlaceDetailCallback);
            }
        }
    }

    //To get a Location
    private void geoLocate() {
        String searchString = editText.getText().toString();

        Geocoder geoCoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();

        try {
            list = geoCoder.getFromLocationName(searchString, 1);
        } catch(IOException e) {
            Log.e(TAG, "geoLocate : IOException " + e.getMessage());
        }

        if(list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "GeoLocate : found a Location - " + address.toString());
            //Toast.makeText(getApplicationContext(), "Address is : " + address, Toast.LENGTH_LONG).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    //GoogleApiClient Method
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //To hide the software keyboard
    private void hideSoftwareKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //Select Item from Search Results
    private AdapterView.OnItemClickListener autoCompleteOnclickListner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftwareKeyboard();

            final AutocompletePrediction item = placesAutocompleteAdapter.getItem(position);
            final String placeID = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeID);
            placeResult.setResultCallback(updatePlaceDetailCallback);
        }
    };

    //Get Information of Selected Location
    private ResultCallback<PlaceBuffer> updatePlaceDetailCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()) {
                Log.d(TAG, "UpdatePlaceDetailCallBack: Failed to retrive information: " + places.getStatus().toString());
                places.release();
                return;
            }

            final Place place = places.get(0);

            try {
                placeInformation = new PlaceInformation();
                placeInformation.setPlaceName(place.getName().toString());
                Log.d(TAG,"UpdatePlaceDetailCallBack: onResult: Name");

                placeInformation.setPlaceAddress(place.getAddress().toString());
                Log.d(TAG,"UpdatePlaceDetailCallBack: onResult: Address");

                placeInformation.setLatLng(place.getLatLng());
                Log.d(TAG,"UpdatePlaceDetailCallBack: onResult: LatLng");

                placeInformation.setPlaceContact(place.getPhoneNumber().toString());
                Log.d(TAG,"UpdatePlaceDetailCallBack: onResult: Contact");

                placeInformation.setPlaceID(place.getId());
                Log.d(TAG,"UpdatePlaceDetailCallBack: onResult: ID");

                placeInformation.setPlaceRatings(place.getRating());
                Log.d(TAG,"UpdatePlaceDetailCallBack: onResult: Ratings");

                placeInformation.setPlaceWebsiteURI(place.getWebsiteUri());

                Log.d(TAG, "UpdatePlaceDetailCallBack: onResult: " + placeInformation.toString());
            } catch (NullPointerException e) {
                Log.d(TAG,"UpdatePlaceDetailCallBack: onResult: NullException: " + e.getMessage());
                }

            //To move camera with Place Information
            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, placeInformation);

            places.release();
        }
    };


}
