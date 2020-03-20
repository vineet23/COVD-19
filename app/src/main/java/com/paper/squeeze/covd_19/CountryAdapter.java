package com.paper.squeeze.covd_19;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.myViewHolder> {

    CountryInterface countryInterface;
    public CountryAdapter(CountryInterface countryInterface) {
        this.countryInterface = countryInterface;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.country_list_item,parent,false);
        myViewHolder viewHolder = new myViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView country,num;
        View view;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            country = itemView.findViewById(R.id.country);
            num = itemView.findViewById(R.id.country_count);
            view = itemView;
        }

        public void onBind(final int pos){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    countryInterface.clickedCountry(pos);
                }
            });
        }
    }
}
