package com.paper.squeeze.covd_19;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Register_Dialog extends DialogFragment {

    CheckBox checkBox;
    Button done;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_dialog, container, false);

        LinearLayout linearLayout = v.findViewById(R.id.linearBack);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        done = v.findViewById(R.id.done);
        done.setEnabled(false);
        done.setClickable(false);
        checkBox = v.findViewById(R.id.agree);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    done.setClickable(true);
                    done.setEnabled(true);
                }
                else{
                    done.setEnabled(false);
                    done.setClickable(false);
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()){
                    //todo post data to server here
                    dismiss();
                }
            }
        });

        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
