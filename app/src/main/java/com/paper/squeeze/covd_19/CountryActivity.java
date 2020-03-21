package com.paper.squeeze.covd_19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CountryActivity extends AppCompatActivity implements CountryInterface{

    RecyclerView recyclerView;
    ImageView back;
    CountryAdapter countryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        back = findViewById(R.id.back_country);
        recyclerView = findViewById(R.id.recycler_view_country);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        countryAdapter = new CountryAdapter(this,TrackerActivity.countryData);
        recyclerView.setAdapter(countryAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void clickedCountry(int pos) {
        //return result with pos
        //todo change pos with country custom class
        Intent intent = new Intent();
        intent.putExtra("result",pos);
        setResult(RESULT_OK,intent);
        finish();
    }
}
