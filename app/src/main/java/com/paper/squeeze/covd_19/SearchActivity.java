package com.paper.squeeze.covd_19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchInterface {

    EditText searchText;
    ImageView back,searchbtn;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    ArrayList<Address> addressArrayList;
    Geocoder geocoder;
    TextView search_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchText = findViewById(R.id.search_input);
        search_empty = findViewById(R.id.search_empty);
        back = findViewById(R.id.back_search);
        searchbtn = findViewById(R.id.search_button);
        recyclerView = findViewById(R.id.recyclerView);

        addressArrayList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        searchAdapter = new SearchAdapter(addressArrayList,this);
        recyclerView.setAdapter(searchAdapter);

        geocoder = new Geocoder(SearchActivity.this);

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

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                geoLocate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void geoLocate(){
        String searchString = searchText.getText().toString();
        if(searchString.length()>0) {
            List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(searchString, 10);
            } catch (Exception e) {
                Toast.makeText(SearchActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                Log.e("GeoCoder", e.toString());
            }
            if (list.size() > 0) {
                search_empty.setVisibility(View.GONE);
                addressArrayList = (ArrayList<Address>) list;
                searchAdapter.update(addressArrayList);
            } else {
                addressArrayList.clear();
                search_empty.setVisibility(View.VISIBLE);
                searchAdapter.update(addressArrayList);
            }
        }else{
            addressArrayList.clear();
            search_empty.setVisibility(View.VISIBLE);
            searchAdapter.update(addressArrayList);
        }
    }

    //interface to send the clicked location from the recycler view
    @Override
    public void clickSearch(Address address) {
        Intent intent = new Intent();
        intent.putExtra("lat",address.getLatitude());
        intent.putExtra("lng",address.getLongitude());
        setResult(RESULT_OK,intent);
        finish();
    }
}

