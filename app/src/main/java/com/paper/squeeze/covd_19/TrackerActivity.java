package com.paper.squeeze.covd_19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackerActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    TextView activenum,recoverednum,fatalnum,name,namenum,loading,active,fatal,recovered;
    ImageView drop;
    LinearLayout linearLayout;
    static ArrayList<CountryData> countryData;
    static boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        ImageView back = findViewById(R.id.back_tracker);
        activenum = findViewById(R.id.active_num);
        recoverednum = findViewById(R.id.recovered_num);
        fatalnum = findViewById(R.id.fatal_num);
        name = findViewById(R.id.name);
        namenum = findViewById(R.id.name_num);
        linearLayout = findViewById(R.id.tracker_main);
        loading = findViewById(R.id.loading);
        fatal = findViewById(R.id.fatal);
        active = findViewById(R.id.active);
        drop = findViewById(R.id.dropdown);
        recovered = findViewById(R.id.recovered);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loaded) {
                    startActivityForResult(new Intent(TrackerActivity.this, CountryActivity.class), 300);
                }
            }
        });

        //set the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTracker);
        mapFragment.getMapAsync(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //if first time opening the activity
        if(!loaded && countryData==null) {
            //making json request for country data
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    getString(R.string.url) + "gettracker/", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //get the global
                                //Log.e("response",response.toString());
                                countryData = new ArrayList<>();
                                countryData.add(new CountryData(response.getString("displayName"),
                                        response.getInt("totalConfirmed"), response.getInt("totalDeaths"),
                                        response.getInt("totalRecovered"), response.getDouble("lat"), response.getLong("long")
                                        , response.getString("country"), false));
                                JSONArray jsonArray = response.getJSONArray("areas");
                                //for every country
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    countryData.add(new CountryData(jsonObject.getString("displayName"),
                                            jsonObject.getInt("totalConfirmed"), jsonObject.getInt("totalDeaths"),
                                            jsonObject.getInt("totalRecovered"), jsonObject.getDouble("lat"), jsonObject.getLong("long")
                                            , jsonObject.getString("country"), false));
                                    JSONArray subarray = jsonObject.getJSONArray("areas");
                                    //for sub country
                                    for (int j = 0; j < subarray.length(); j++) {
                                        JSONObject subObject = subarray.getJSONObject(j);
                                        countryData.add(new CountryData(subObject.getString("displayName") + " (" + jsonObject.getString("country") + ")",
                                                subObject.getInt("totalConfirmed"), subObject.getInt("totalDeaths"),
                                                subObject.getInt("totalRecovered"), subObject.getDouble("lat"), subObject.getLong("long")
                                                , jsonObject.getString("country"), true));
                                       /* try{
                                            //for sub region
                                            JSONArray jrArray = subObject.getJSONArray("areas");
                                            for(int k =0;k<jrArray.length();k++){
                                                JSONObject jrObject = jrArray.getJSONObject(k);
                                                countryData.add(new CountryData(jrObject.getString("displayName")+" ("+subObject.getString("displayName")+")",
                                                        jrObject.getInt("totalConfirmed"),jrObject.getInt("totalDeaths"),
                                                        jrObject.getInt("totalRecovered"),jrObject.getDouble("lat"),jrObject.getLong("long"),
                                                        subObject.getString("displayName"),true));
                                            }
                                        }catch (Exception e){
                                            Log.e("error jr",e.toString());
                                        }*/
                                    }
                                }
                                name.setText(countryData.get(0).getName());
                                namenum.setText(countryData.get(0).getTotalConfirmed() + "");
                                activenum.setText(countryData.get(0).getTotalActive() + "");
                                fatalnum.setText(countryData.get(0).getTotalDeaths() + "");
                                recoverednum.setText(countryData.get(0).getTotalRecovered() + "");
                                loaded = true;
                                LoadingFinish();
                                addMarkers();
                            } catch (JSONException e) {
                                //Log.e("error",e.toString());
                                loading.setText(getString(R.string.try_again));
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
        }else{
            try{
            //else load from previously loaded data
            name.setText(countryData.get(0).getName());
            namenum.setText(countryData.get(0).getTotalConfirmed() + "");
            activenum.setText(countryData.get(0).getTotalActive() + "");
            fatalnum.setText(countryData.get(0).getTotalDeaths() + "");
            recoverednum.setText(countryData.get(0).getTotalRecovered() + "");
            loaded = true;
            LoadingFinish();
            }catch (Exception e){
                loaded= false;
                loading.setVisibility(View.VISIBLE);
                loading.setText(getString(R.string.try_again));
            }
        }
    }


    //to set the view visible
    public void LoadingFinish(){
        namenum.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        drop.setVisibility(View.VISIBLE);
        active.setVisibility(View.VISIBLE);
        activenum.setVisibility(View.VISIBLE);
        fatal.setVisibility(View.VISIBLE);
        fatalnum.setVisibility(View.VISIBLE);
        recovered.setVisibility(View.VISIBLE);
        recoverednum.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    //to move the camera on move
    public void move(double lat,double lng){
        if(lat==0.0d && lng==0.0d){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 0));
        }else{
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 5));
        }
    }

    //to mark markers on the map
    public void addMarkers(){
        if (loaded && mMap!=null){
            mMap.clear();
            MarkerOptions markerOptions;
            //get the total and first country
            int total = countryData.get(0).getTotalConfirmed()/2;
            int first = countryData.get(1).getTotalConfirmed();
            int offset;
            //initialize offset
            if(first<total)
                offset = 100 - (first/total);
            else
                offset = 0;
            //mark the first
            markerOptions = new MarkerOptions().position(new LatLng(countryData.get(1).getLat(),countryData.get(1).getLng()))
                    .title(countryData.get(1).getName())
                    .snippet(countryData.get(1).getTotalConfirmed()+" ").anchor(0.5f,0.5f)
                    .icon(getBitmapFromVector(getApplicationContext(),R.drawable.tracker_marker,100,100));
            mMap.addMarker(markerOptions);
            //for each country
            for(int i=2;i<countryData.size();i++){
                CountryData country = countryData.get(i);
                //if it is not the sub country
                if(!country.isSub()){
                    //calculate the percentage
                    int percent = country.getTotalConfirmed()/total;
                    //if percentage is greater than zero
                    if(percent>0){
                        markerOptions = new MarkerOptions().position(new LatLng(country.getLat(),country.getLng()))
                            .title(country.getName())
                            .snippet(country.getTotalConfirmed()+" ").anchor(0.5f,0.5f)
                            .icon(getBitmapFromVector(getApplicationContext(),R.drawable.tracker_marker,percent+offset,percent+offset));
                        mMap.addMarker(markerOptions);
                    }else{
                        //else check if offset is greater than zero
                        if(offset>5)
                            offset=offset-1;
                        markerOptions = new MarkerOptions().position(new LatLng(country.getLat(),country.getLng()))
                            .title(country.getName())
                            .snippet(country.getTotalConfirmed()+" ").anchor(0.5f,0.5f)
                            .icon(getBitmapFromVector(getApplicationContext(),R.drawable.tracker_marker,offset,offset));
                        mMap.addMarker(markerOptions);
                    }
                }else{
                    //if a sub country
                    markerOptions = new MarkerOptions().position(new LatLng(country.getLat(),country.getLng()))
                            .title(country.getName())
                            .snippet(country.getTotalConfirmed()+" ").anchor(0.5f,0.5f)
                            .icon(getBitmapFromVector(getApplicationContext(),R.drawable.tracker_marker,3,3));
                    mMap.addMarker(markerOptions);
                }

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //set map rotation to false
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        //set map move to location button false
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        addMarkers();
    }

    public static BitmapDescriptor getBitmapFromVector(@NonNull Context context,@DrawableRes int vectorResourceId,int width,int height) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(
                context.getResources(), vectorResourceId, null);
        if (vectorDrawable == null) {
            Log.e("Error", "Requested vector resource was not found");
            return BitmapDescriptorFactory.defaultMarker();
        }
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        bitmap = Bitmap.createScaledBitmap(
                bitmap, 2*width, 2*height, false);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //DrawableCompat.setTint(vectorDrawable, tintColor);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300) {
            if (resultCode == RESULT_OK) {
                try {
                    int position = data.getIntExtra("result", 0);
                    name.setText(countryData.get(position).getName());
                    namenum.setText(countryData.get(position).getTotalConfirmed()+"");
                    activenum.setText(countryData.get(position).getTotalActive()+"");
                    fatalnum.setText(countryData.get(position).getTotalDeaths()+"");
                    recoverednum.setText(countryData.get(position).getTotalRecovered()+"");
                    if(mMap!=null){
                        move(countryData.get(position).getLat(),countryData.get(position).getLng());
                    }
                }catch (Exception e){}
            }
        }
    }
}
