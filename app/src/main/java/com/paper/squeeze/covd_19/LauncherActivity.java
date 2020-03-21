package com.paper.squeeze.covd_19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(isServicesOk()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LauncherActivity.this.startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                    finish();
                }
            }, 2250);
        }

    }

    //to check that google services are available
    public boolean isServicesOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LauncherActivity.this);
        if (available== ConnectionResult.SUCCESS){
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LauncherActivity.this,available,100);
            dialog.show();
        }else{
            Toast.makeText(LauncherActivity.this,"Can't connect to google services",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
