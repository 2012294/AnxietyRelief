package com.hareem.anxietyrelief;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
        Boolean loginPatient = sharedPreferences.getBoolean("patient_login", false);
        Boolean logintherapist = sharedPreferences.getBoolean("therapist_login", false);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loginPatient){
                    Intent i = new Intent(SplashActivity.this,PatientNavigationDrawerActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }else{
                    if(logintherapist){
                        Intent i = new Intent(SplashActivity.this,therapist_navigation.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }else {
                        Intent i = new Intent(SplashActivity.this,UserOptionActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }

                }
            }

        }, 3000);
    }

}
