package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hareem.anxietyrelief.Patient_ui.FindTherapist.FindTherapist;

public class SucessfullyBooked extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucessfully_booked);

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SucessfullyBooked.this, PatientNavigationDrawerActivity.class);
                startActivity(i);
//
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Do nothing to disable back button functionality
    }


}
