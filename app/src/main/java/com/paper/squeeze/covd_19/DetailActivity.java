package com.paper.squeeze.covd_19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    ArrayList<Detail> details;

    RecyclerView recyclerView;
    DetailAdapter detailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        recyclerView = findViewById(R.id.recycler_view);
        TextView detailheader = findViewById(R.id.detail_id);
        final TextView next = findViewById(R.id.next);
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int type = getIntent().getIntExtra("type",1);

        details = new ArrayList<>();
        switch (type){
            case 1:
                detailheader.setText(getString(R.string.prevention));
                details.add(new Detail(getString(R.string.wash_header),getString(R.string.wash_detail),R.raw.handsoap,getString(R.string.wash_why)));
                details.add(new Detail(getString(R.string.social_header),getString(R.string.social_detail),R.raw.socialdist,getString(R.string.social_why)));
                details.add(new Detail(getString(R.string.eyes_header),getString(R.string.eyes_detail),R.raw.eyes,getString(R.string.eyes_why)));
                details.add(new Detail(getString(R.string.hygiene_header),getString(R.string.hygiene_detail),R.raw.coronahygiene,getString(R.string.hygiene_why)));
                details.add(new Detail(getString(R.string.medical_header),getString(R.string.medical_detail),R.raw.coronavirussick,getString(R.string.medical_why)));
                break;
            case 2:
                next.setVisibility(View.INVISIBLE);
                detailheader.setText(getString(R.string.symptoms));
                details.add(new Detail(getString(R.string.doctor_header),getString(R.string.doctor_detail),R.raw.doctores,getString(R.string.doctor_why)));
               break;
            default:
                break;
        }

        final CenterZoomLayoutManager layoutManager = new CenterZoomLayoutManager(this,RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        detailAdapter = new DetailAdapter(details);
        recyclerView.setAdapter(detailAdapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = layoutManager.findFirstVisibleItemPosition();
                if (pos!= details.size()-1) {
                    recyclerView.smoothScrollToPosition(pos + 1);
                    if (pos + 1 == details.size() - 1) {
                        next.setText(getString(R.string.back));
                    }
                }
                else{
                    next.setText(getString(R.string.next));
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }
}
