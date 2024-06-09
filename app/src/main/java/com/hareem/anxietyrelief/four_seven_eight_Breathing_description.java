package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class four_seven_eight_Breathing_description extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_seven_eight_breathing_description);

        // BENEFITS section
        final LinearLayout layoutBenefits = findViewById(R.id.layoutBenefits);
        final ImageView arrowBenefits = findViewById(R.id.arrowBenefits);
        final TextView textBenefits = findViewById(R.id.textBenefits);

        arrowBenefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(textBenefits, arrowBenefits);
            }
        });

        // METHOD section (similar structure)
        final LinearLayout layoutMethod = findViewById(R.id.layoutMethod);
        final ImageView arrowMethod = findViewById(R.id.arrowMethod);
        final TextView textMethod = findViewById(R.id.textMethod);

        arrowMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(textMethod, arrowMethod);
            }
        });

        CardView BeginExerciseButton = findViewById(R.id.beginexercisebutton);

        BeginExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the EqualBreathingCounterValue from SharedPreferences
//                SharedPreferences sharedPreferences = getSharedPreferences("FourSevenEightBreathingCounter", Context.MODE_PRIVATE);
//                int fourseveneightBreathingCounterValue = sharedPreferences.getInt("FourSevenEightBreathingCounterValue", 2);
//
//                // Pass the value to the EqualBreathingExercise activity
//                Intent intent = new Intent(four_seven_eight_Breathing_description.this, FourSevenEightExercise.class);
//                intent.putExtra("four_seven_eight_breathing_counter_value", fourseveneightBreathingCounterValue);
//                startActivity(intent);
                Intent intent = new Intent(four_seven_eight_Breathing_description.this, FourSevenEightExercise.class);
                startActivity(intent);

            }
        });
    }

    private void toggleVisibility(TextView textView, ImageView arrow) {
        if (textView.getVisibility() == View.VISIBLE) {
            textView.setVisibility(View.GONE);
            arrow.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        } else {
            textView.setVisibility(View.VISIBLE);
            arrow.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
        }
    }
}