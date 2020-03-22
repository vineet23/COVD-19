package com.paper.squeeze.covd_19;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    NavigationView navigationView;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //to store the current location
    LatLng latLng;
    LatLng statuslatlng;
    MaterialCardView statuscard;
    TextView last,title,date,loading;
    static int admin=0,user=0;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //check the permissions
        request_permission();

       /* String apiKey = getString(R.string.api_key);

        *//**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         *//*
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }*/

        last = findViewById(R.id.textView2);
        title = findViewById(R.id.status);
        date = findViewById(R.id.date);
        loading = findViewById(R.id.loading);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        //set the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configure_button();
            }
        });

        statuscard = findViewById(R.id.status_card);
        statuscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latLng != null && loading.getVisibility()!=View.VISIBLE) {
                    Status_Dialog status_dialog = new Status_Dialog(admin,user,title.getText().toString(),date.getText().toString());
                    status_dialog.show(getSupportFragmentManager(), "status");
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.detecting),Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert();

    }

    //show dialog about the information
    public void alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle);
        builder.setTitle(getString(R.string.termsandcond));
        builder.setMessage(getString(R.string.info));
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
           /* // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields).setCountry("NG") //NIGERIA
                    .build(this);
            startActivityForResult(intent, 200);*/
           startActivityForResult(new Intent(MainActivity.this,SearchActivity.class),100);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if (id == R.id.nav_prevent) {
           Intent intent = new Intent(MainActivity.this,DetailActivity.class);
           intent.putExtra("type",1);
           startActivity(intent);
        } else if (id == R.id.nav_symptom) {
           Intent intent = new Intent(MainActivity.this,DetailActivity.class);
           intent.putExtra("type",2);
           startActivity(intent);
        } else if (id == R.id.nav_tracker) {
            startActivity(new Intent(MainActivity.this,TrackerActivity.class));
        } else if (id == R.id.nav_register) {
           //check gps enable
           LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
           if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
               //if users lat lng is available
               if (latLng!=null) {
                   Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                   intent.putExtra("lat",latLng.latitude);
                   intent.putExtra("lng",latLng.longitude);
                   startActivity(intent);
               }else
                   //get the users lat lng
                   Toast.makeText(MainActivity.this,getString(R.string.detecting_gps),Toast.LENGTH_SHORT).show();
                   configure_button();
           }else{
               //enable gps and get the users lat lng
               buildAlertMessageNoGps();
           }
        } else if (id == R.id.nav_share) {
           String shareBody = "\n https://rebrand.ly/covid19app \n\n"+"https://drive.google.com/open?id=1V8qTcnjZuWlPtxUXjQGkX3J2MCk3t2Ry";
           Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
           sharingIntent.setType("text/plain");
           sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
           sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_text)+shareBody);
           startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_title)));
        }else if(id == R.id.nav_feedback){
           //open feedback form
           String url = "https://www.papersqueeze.com/form/covid";
           Intent i = new Intent(Intent.ACTION_VIEW);
           i.setData(Uri.parse(url));
           // Always use string resources for UI text. This says something like "Share this photo with"
           String title = getString(R.string.chooser_title);
           // Create and start the chooser
           Intent chooser = Intent.createChooser(i, title);
           startActivity(chooser);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //update the status
    public void updateStatus(final double lat,final double lng){
        //to make views gone and fetch data
        loading.setText(getString(R.string.loading));
        loading.setVisibility(View.VISIBLE);
        date.setVisibility(View.GONE);
        last.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.url) + "getstatus/" + String.format("%.6f", lat) + "/" + String.format("%.6f",lng) + "/5/",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<LocationData> locationDatas = new ArrayList<>();
                    JSONArray array = response.getJSONArray("data");
                    String datetime = response.getString("datetime");
                    admin =0;user=0;
                    for(int i=0;i<array.length();i++){
                        JSONObject jsonObject = array.getJSONObject(i);
                        locationDatas.add(new LocationData(jsonObject.getString("usertype"),
                                jsonObject.getString("condition"),jsonObject.getDouble("latitude"),
                                jsonObject.getDouble("longitude")));
                        if (jsonObject.getString("usertype").equals("admin"))
                            admin++;
                        else
                            user++;
                    }
                    if(mMap!=null){
                        MarkerOptions mOption;
                        for(LocationData locationData:locationDatas){
                            // Creating a marker
                            mOption = new MarkerOptions();
                            // Setting the position for the marker
                            mOption.position(new LatLng(locationData.getLat(),locationData.getLng()))
                                    .snippet(locationData.getCondition());
                            if (locationData.getUsertype().equals("admin")) {
                                mOption.icon(BitmapDescriptorFactory.defaultMarker())
                                .title(getString(R.string.confirmed));
                            }
                            else {
                                mOption.icon(BitmapDescriptorFactory.defaultMarker(41))
                                .title(getString(R.string.unsure));
                            }
                            // Placing a marker
                            mMap.addMarker(mOption);
                        }
                    }
                    //Safe
                    if (admin==0 && user<=3){
                        title.setText(getString(R.string.status));
                        statuscard.setBackgroundColor(getResources().getColor(R.color.green));
                        if (mMap!=null)
                            mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(lat, lng))
                                    .radius(5000)
                                    .strokeColor(getResources().getColor(R.color.green))
                                    .strokeWidth(1f)
                                    .fillColor(getResources().getColor(R.color.green_fade)));
                    }
                    //Unsafe
                    else if(admin==0){
                        title.setText(getString(R.string.unsafe));
                        statuscard.setBackgroundColor(getResources().getColor(R.color.orange));
                        if (mMap!=null)
                            mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(lat, lng))
                                    .radius(5000)
                                    .strokeWidth(1f)
                                    .strokeColor(getResources().getColor(R.color.orange))
                                    .fillColor(getResources().getColor(R.color.orange_fade)));
                    }
                    //Danger
                    else{
                        title.setText(getString(R.string.danger));
                        statuscard.setBackgroundColor(getResources().getColor(R.color.red));
                        if (mMap!=null)
                            mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(lat, lng))
                                    .radius(5000)
                                    .strokeWidth(1f)
                                    .strokeColor(getResources().getColor(R.color.red))
                                    .fillColor(getResources().getColor(R.color.red_fade)));
                    }
                    date.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'").parse(datetime)));
                    loading.setVisibility(View.GONE);
                    //set them then make visible
                    date.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                    last.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    loading.setText(getString(R.string.Error));
                    statuscard.setBackgroundColor(getResources().getColor(R.color.red));
                    loading.setVisibility(View.VISIBLE);
                    date.setVisibility(View.GONE);
                    last.setVisibility(View.GONE);
                    title.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError)
                    try {
                        loading.setText(getString(R.string.Gps));
                        statuscard.setBackgroundColor(getResources().getColor(R.color.red));
                        loading.setVisibility(View.VISIBLE);
                        date.setVisibility(View.GONE);
                        last.setVisibility(View.GONE);
                        title.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){}
                else
                    try {
                        loading.setText(getString(R.string.Error));
                        statuscard.setBackgroundColor(getResources().getColor(R.color.red));
                        loading.setVisibility(View.VISIBLE);
                        date.setVisibility(View.GONE);
                        last.setVisibility(View.GONE);
                        title.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){}
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", getString(R.string.auth));
                return headers;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    //show message for gps enabling
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gps_enable))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("Result", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                Toast.makeText(MainActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(MainActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i("Result", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }*/
        if(requestCode == 100){
            if (resultCode == RESULT_OK){
                try {
                    double lat = data.getDoubleExtra("lat", 0.0d);
                    double lng = data.getDoubleExtra("lng", 0.0d);
                    statuslatlng = new LatLng(lat,lng);
                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    // Setting the position for the marker
                    markerOptions.position(new LatLng(lat, lng));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(220));
                    // Clears the previously touched position
                    if(marker!=null)
                        marker.remove();
                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),15f));
                    mMap.clear();
                    updateStatus(lat,lng);
                    // Placing a marker on the touched position
                    marker = mMap.addMarker(markerOptions);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,getString(R.string.try_again),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 10:
                //to get the current location
                configure_button();
                break;
            default:
                break;
        }
    }

    //to check about location permission and fetch current location
    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request_permission();
            }
        } else {
            // permission has been granted
            //get the current location
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            try{
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            try {
                                //may be null
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
                                if(latLng!=null) {
                                    if (getKmFromLatLong(latLng.latitude, latLng.longitude,
                                            currentLocation.getLatitude(), currentLocation.getLongitude()) > 5) {
                                        updateStatus(currentLocation.getLatitude(), currentLocation.getLongitude());
                                    }
                                }else{
                                    updateStatus(currentLocation.getLatitude(), currentLocation.getLongitude());
                                }
                                //store the current location
                                latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            }catch (Exception e){
                                //may gps will be off

                                Toast.makeText(MainActivity.this,getString(R.string.detecting),Toast.LENGTH_SHORT).show();
                                if (e instanceof NullPointerException && latLng!=null){
                                    moveCamera(latLng,15f);
                                }
                            }
                        }else{
                            Toast.makeText(MainActivity.this,getString(R.string.unable_detect),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (Exception e){
                Log.e("Error",e.toString());
            }
        }
    }

    //to move the map
    private void moveCamera(LatLng latLng,float zoom){
        try {
            // Clears the previously touched position
            if (marker!=null)
                marker.remove();
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }catch (Exception e){}
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

    //to request the permissions for location
    private void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                ACCESS_COARSE_LOCATION)) {
            //show the snack bar to request permission
            Snackbar.make(findViewById(R.id.map), getString(R.string.location_needed),
                    Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
                        }
                    })
                    .show();
        } else {
            // permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        //set map rotation to false
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        //set map move to location button false
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        //get the current location
        configure_button();

        try {
            mMap.setMyLocationEnabled(true);
        }catch (Exception e){}

        //when clicked on map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng ltLng) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(ltLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(220));
                // Clears the previously touched position
                if (marker!=null && statuslatlng!=null) {
                    if (getKmFromLatLong(statuslatlng.latitude,statuslatlng.longitude, ltLng.latitude, ltLng.longitude)>5) {
                        mMap.clear();
                        statuslatlng = ltLng;
                        updateStatus(ltLng.latitude, ltLng.longitude);
                    }
                    marker.remove();
                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(ltLng));
                }else{
                    statuslatlng = ltLng;
                    mMap.clear();
                    updateStatus(ltLng.latitude, ltLng.longitude);
                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltLng,15f));
                }
                // Placing a marker on the touched position
                marker = mMap.addMarker(markerOptions);
            }
        });
    }

}
