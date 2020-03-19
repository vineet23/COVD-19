package com.paper.squeeze.covd_19;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RegisterActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    Marker marker;

    LatLng latLng;

    Button register;
    int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        latLng = new LatLng(getIntent().getDoubleExtra("lat",0.0d),getIntent().getDoubleExtra("lng",0.0d));

        ImageView back = findViewById(R.id.back);
        register = findViewById(R.id.register_btn);

        String[] COUNTRIES = new String[] {getString(R.string.active),getString(R.string.recovered),getString(R.string.fatal)};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.menu_popup,
                        COUNTRIES);

        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
        editTextFilledExposedDropdown.setText(editTextFilledExposedDropdown.getAdapter().getItem(0).toString(),false);

        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //to store the selected item position
                selected = i;
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status;
                if(selected==0)
                    status="active";
                else if (selected==1)
                    status="recovered";
                else
                    status="fatal";
                //check the distance between latlng and marker is less than 20km
                if (getKmFromLatLong(latLng.latitude,latLng.longitude,marker.getPosition().latitude,marker.getPosition().longitude)<=20.0d) {
                    Register_Dialog register_dialog = new Register_Dialog();
                    register_dialog.show(getSupportFragmentManager(), "Dialog");
                }else{
                    //todo check for spam try again and again
                    Toast.makeText(getApplicationContext(),getString(R.string.location_dist),Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapRegister);
        mapFragment.getMapAsync(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public static double getKmFromLatLong(double lat1, double lng1, double lat2, double lng2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        double distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters/1000;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //set map rotation to false
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        //set map move to location button false
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting the position for the marker on the passed latlng
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(220));
        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
        // Placing a marker on the touched position
        marker = mMap.addMarker(markerOptions);
        marker.setDraggable(true);

        //when clicked on map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(220));
                // Clears the previously touched position
                mMap.clear();
                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
                // Placing a marker on the touched position
                marker = mMap.addMarker(markerOptions);
                marker.setDraggable(true);
            }
        });
    }
}
