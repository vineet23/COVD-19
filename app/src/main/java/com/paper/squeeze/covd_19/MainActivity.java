package com.paper.squeeze.covd_19;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    NavigationView navigationView;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //to store the current location
    LatLng latLng;


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

        }else if(id == R.id.nav_feedback){

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    // Setting the position for the marker
                    markerOptions.position(new LatLng(lat, lng));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(220));
                    // Clears the previously touched position
                    mMap.clear();
                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),15f));
                    // Placing a marker on the touched position
                    mMap.addMarker(markerOptions);
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
            mMap.clear();
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }catch (Exception e){}
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
            public void onMapClick(LatLng latLng) {
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(220));
                // Clears the previously touched position
                mMap.clear();
                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
            }
        });
    }
}
