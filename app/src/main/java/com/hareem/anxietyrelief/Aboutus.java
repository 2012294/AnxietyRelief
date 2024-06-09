package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Aboutus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        ImageView backArrow = findViewById(R.id.BackArrow);

        // Set a click listener on the back arrow to navigate back to the previous activity
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Close the current activity and go back to the previous one
            }
        });

    }
}