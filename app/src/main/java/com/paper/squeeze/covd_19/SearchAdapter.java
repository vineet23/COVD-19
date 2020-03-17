package com.paper.squeeze.covd_19;

import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.myViewHolder> {

    ArrayList<Address> addressArrayList;
    SearchInterface searchInterface;

    public SearchAdapter(ArrayList<Address> addresses,SearchInterface searchInterface) {
        this.addressArrayList = addresses;
        this.searchInterface = searchInterface;
    }

    public void update(ArrayList<Address> addresses){
        this.addressArrayList = addresses;
        notifyDataSetChanged();
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_item,parent,false);
        myViewHolder viewHolder = new myViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return addressArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        TextView title,address;
        View view;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.address);
            view = itemView;
        }

        void bind(final int position){
            title.setText(addressArrayList.get(position).getFeatureName());
            address.setText(addressArrayList.get(position).getAddressLine(0));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchInterface.clickSearch(addressArrayList.get(position));
                }
            });
        }
    }
}
