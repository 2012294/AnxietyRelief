package com.hareem.anxietyrelief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class UserOptionActivity extends AppCompatActivity {
    Button therapist,patient;
    private FrameLayout selectedFrameLayout = null;
    FrameLayout frameLayout1,frameLayout2;
    AppCompatButton register;
    int select=0;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_option);
        frameLayout1=findViewById(R.id.frame_layout1);
        frameLayout2=findViewById(R.id.frame_layout2);
        register=findViewById(R.id.registerbutton);

        frameLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFrameLayout(frameLayout1);
                select=1;
            }
        });

        frameLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFrameLayout(frameLayout2);
                select=2;
            }
        });
        requestNotificationPermission();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select==1){
                    Intent registerIntent = new Intent(UserOptionActivity.this, TherapistRegister.class);
                    registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(registerIntent);
                    finish();
                } else if (select==2) {
                    Intent registerIntent = new Intent(UserOptionActivity.this, PatientRegister.class);
                    registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(registerIntent);
                    finish();
                }else {
                    Toast.makeText(UserOptionActivity.this, "please choose your role", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectFrameLayout(FrameLayout frameLayout) {
        if (selectedFrameLayout != null) {
            // Reset the background of the previously selected frame
            selectedFrameLayout.setBackgroundResource(R.drawable.circle);
        }
        // Set the background for the newly selected frame
        frameLayout.setBackgroundResource(R.drawable.circle_pressed); // Set the selected background
        // Update the selectedFrameLayout
        selectedFrameLayout = frameLayout;
    }

//        therapist=findViewById(R.id.btnTherapistOpt);
//        patient=findViewById(R.id.btnPatientOpt);
//
//        patient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                patient.animate().alpha(0.8f);
//                Intent mainIntent = new Intent(UserOptionActivity.this, PatientLogin.class);
//                startActivity(mainIntent);
//                patient.animate().alpha(1);
//            }
//        });
//        therapist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                therapist.animate().alpha(0.8f);
//                Intent mainIntent = new Intent(UserOptionActivity.this, TherapistLogin.class);
//                startActivity(mainIntent);
//                therapist.animate().alpha(1);
//
//            }
//        });

    private void requestNotificationPermission() {
        Log.d("Notification1", "called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 and above
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
                Log.d("Notification1", "called1");

            } else {

                Log.d("Notification1", "called2");
            }
        } else {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d("Notification1", "Permission denied for posting notifications.");

            } else {
                Log.d("Notification1", "Permission denied for posting notifications.");
            }
        }
    }
}