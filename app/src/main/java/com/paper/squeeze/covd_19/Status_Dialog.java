package com.paper.squeeze.covd_19;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

public class Status_Dialog extends DialogFragment {

    int admin,user;
    String title,date;
    TextView status,confirmed,unsure,datestr;
    CardView cardView;
    public Status_Dialog(int admin,int user,String title,String date) {
        this.admin = admin;
        this.user = user;
        this.title = title;
        this.date = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.status_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(true);

        cardView = v.findViewById(R.id.cardView);
        status = v.findViewById(R.id.status);
        confirmed = v.findViewById(R.id.confirmed_case_num);
        unsure = v.findViewById(R.id.unsure_case_num);
        datestr = v.findViewById(R.id.date);

        if (title.equals(getString(R.string.status)))
            cardView.setCardBackgroundColor(getResources().getColor(R.color.green));
        else if(title.equals(getString(R.string.unsafe)))
            cardView.setCardBackgroundColor(getResources().getColor(R.color.orange));
        else
            cardView.setCardBackgroundColor(getResources().getColor(R.color.red));

        status.setText(title);
        confirmed.setText(admin+" ");
        unsure.setText(user+" ");
        datestr.setText(date);

        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public int getTheme() {
        return R.style.MyCustomTheme;
    }
}
