package com.paper.squeeze.covd_19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText searchText;
    ImageView back,searchbtn;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchText = findViewById(R.id.search_input);
        back = findViewById(R.id.back_search);
        searchbtn = findViewById(R.id.search_button);
        recyclerView = findViewById(R.id.recyclerView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoLocate();
            }
        });
    }

    private void geoLocate(){
        String searchString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(SearchActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,10);
        }catch (Exception e){
            Log.e("GeoCoder",e.toString());
        }
        if (list.size()>0){
            for(Address address:list){
                Log.d("address",address.toString());
            }
        }
    }
}
