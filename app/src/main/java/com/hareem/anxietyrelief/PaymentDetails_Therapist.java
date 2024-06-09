package com.hareem.anxietyrelief;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentDetails_Therapist extends AppCompatActivity {
    Button Addbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details_therapist);

        Addbutton = findViewById(R.id.AddButton);
        Addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TherapistLogin", "Login button clicked");

                Intent mainIntent = new Intent(PaymentDetails_Therapist.this, therapist_navigation.class);
                startActivity(mainIntent);
            }
        });

    }
}