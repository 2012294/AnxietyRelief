package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PatientSettings extends AppCompatActivity {
    CardView changepassword;
    CardView DeleteAccount, payment, about;
    ImageView backarrow;
    TextView usernameText;

    TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_settings);
        changepassword = findViewById(R.id.changepasswordicon);

        payment = findViewById(R.id.payment);
        about = findViewById(R.id.about);
        backarrow = findViewById(R.id.back_Arrow);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("patient_username2", null);
        String patientemail = sharedPreferences.getString("patient_email", null);

        if (username != null) {
            usernameText.setText(username);
            emailText.setText(patientemail);
        }

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientSettings.this, Patient_payment_setting.class);
                startActivity(intent);
            }
        });

        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientSettings.this, Patient_change_password.class);
                startActivity(intent);
            }
        });

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletedialoguebox customDialog = new deletedialoguebox();
                customDialog.show(getSupportFragmentManager(), "CustomDialogFragment");
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientSettings.this, Aboutus.class);
                startActivity(intent);
            }
        });
    }
}
