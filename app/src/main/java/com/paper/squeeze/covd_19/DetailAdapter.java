package com.paper.squeeze.covd_19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.customViewHolder> {

    ArrayList<Detail> details;

    public DetailAdapter(ArrayList<Detail> details){
        this.details = details;
    }

    @NonNull
    @Override
    public customViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.detail_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean attachParent = false;
        View view = layoutInflater.inflate(layoutId,parent,attachParent);
        customViewHolder viewHolder = new customViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull customViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public class customViewHolder extends RecyclerView.ViewHolder{

        LottieAnimationView lottieAnimationView;
        TextView header;
        TextView detail;
        TextView why;
        TextView reason;

        public customViewHolder(@NonNull View itemView) {
            super(itemView);
            lottieAnimationView = itemView.findViewById(R.id.lottie_view);
            header = itemView.findViewById(R.id.header);
            detail = itemView.findViewById(R.id.detail);
            why = itemView.findViewById(R.id.why);
            reason = itemView.findViewById(R.id.why_reason);
        }

        void bind(int index){
            Detail det = details.get(index);
            int raw = det.getRaw();
            String head = det.getHeader();
            String deta = det.getDetails().trim();
            String whyreas = det.getWhy().trim();

            if(raw==0)
                lottieAnimationView.setVisibility(View.GONE);
            else {
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.setAnimation(raw);
            }
            header.setText(head);
            if (deta.length()>0){
                detail.setVisibility(View.VISIBLE);
                detail.setText(deta);
            }else
                detail.setVisibility(View.GONE);
            if (whyreas.length()>0){
                why.setVisibility(View.VISIBLE);
                reason.setVisibility(View.VISIBLE);
                reason.setText(whyreas);
            }else {
                why.setVisibility(View.GONE);
                reason.setVisibility(View.GONE);
            }
        }
    }
}
