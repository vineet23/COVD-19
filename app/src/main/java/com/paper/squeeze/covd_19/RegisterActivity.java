package com.paper.squeeze.covd_19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements OnMapReadyCallback,RegisterInterface {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    Marker marker;

    LatLng latLng;

    Button register;
    AutoCompleteTextView editTextFilledExposedDropdown;
    TextInputLayout layout;
    TextView loading;
    int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        latLng = new LatLng(getIntent().getDoubleExtra("lat",0.0d),getIntent().getDoubleExtra("lng",0.0d));

        ImageView back = findViewById(R.id.back);
        register = findViewById(R.id.register_btn);
        loading = findViewById(R.id.loading);
        layout = findViewById(R.id.textInputLayout);

        String[] COUNTRIES = new String[] {getString(R.string.active),getString(R.string.recovered),getString(R.string.fatal)};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.menu_popup,
                        COUNTRIES);

        editTextFilledExposedDropdown =
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
                    if(marker.getPosition().latitude!=0.0d && marker.getPosition().longitude!=0.0d) {
                        Register_Dialog register_dialog = new Register_Dialog(status, marker.getPosition().
                                latitude, marker.getPosition().longitude,RegisterActivity.this);
                        register_dialog.show(getSupportFragmentManager(), "Dialog");
                    }else{
                        Toast.makeText(getApplicationContext(),getString(R.string.loading),Toast.LENGTH_SHORT).show();
                    }
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
        //if same day
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        String day = preferences.getString("day","");
        if (day.equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))){
            loading.setText(R.string.spam);
        }
        //else check the window
        else {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.url) + "getwindow/",
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean live = response.getBoolean("live");
                        String datetime = response.getString("datetime");
                        if (live)
                            finishLoading();
                        else {
                            try {
                                SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                SimpleDateFormat destFormat = new SimpleDateFormat("MMM d, yyyy hh:mm:ss a"); //here 'a' for AM/PM
                                Date date = sourceFormat.parse(datetime);
                                String formattedDate = destFormat.format(date);
                                loading.setText(getString(R.string.registraion) + "\n" + formattedDate + " UTC");
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                loading.setText(getString(R.string.registraion));
                            }
                        }
                    } catch (JSONException e) {
                        loading.setText(R.string.try_again);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (volleyError instanceof NetworkError)
                        loading.setText(getString(R.string.no_internet));
                    else
                        loading.setText(getString(R.string.try_again));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", getString(R.string.auth));
                    return headers;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
    }

    //to hide when done loading
    public void finishLoading(){
        register.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }


    //to get the distance from the points in km
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

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker markerUpdate) {
                marker = markerUpdate;
            }
        });
    }

    @Override
    public void Done(boolean b) {
        if(b)
            finish();
    }
}
