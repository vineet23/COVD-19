package com.paper.squeeze.covd_19;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Register_Dialog extends DialogFragment {

    CheckBox checkBox;
    Button done;
    String status;
    double lat,lng;
    boolean clicked = false;
    RegisterInterface registerInterface;

    public Register_Dialog(String status,double lat,double lng,RegisterInterface registerInterface) {
        this.status = status;
        this.lat = lat;
        this.lng = lng;
        this.registerInterface = registerInterface;
    }

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
                if (checkBox.isChecked() && !clicked){
                    try {
                        clicked = true;
                        //get the place
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                        String place = "";
                        if (addresses.size() > 0)
                            place = addresses.get(0).getLocality();
                        JSONObject object = new JSONObject();
                        object.put("usertype","user");
                        object.put("condition",status);
                        object.put("place",place);
                        object.put("latitude",lat);
                        object.put("longitude",lng);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.url) + "addlocation/",
                                object, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    SharedPreferences preferences = getContext().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("day", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                                    editor.apply();
                                    dismiss();
                                    registerInterface.Done(true);
                                }catch (Exception e){}
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                if (volleyError instanceof NetworkError)
                                    Toast.makeText(getContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getContext(),getString(R.string.try_again),Toast.LENGTH_SHORT).show();
                                dismiss();
                                registerInterface.Done(true);
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Authorization", getString(R.string.auth));
                                return headers;
                            }
                        };
                        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
                    }catch (Exception e){
                        Toast.makeText(getContext(),getString(R.string.try_again),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //to show when clicked once
                    Toast.makeText(getContext(),getString(R.string.loading),Toast.LENGTH_SHORT).show();
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
