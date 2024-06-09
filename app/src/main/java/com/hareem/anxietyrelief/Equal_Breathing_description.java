package com.hareem.anxietyrelief;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Equal_Breathing_description extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equal_breathing_description);

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
        final ImageView arrowMethod = findViewById(R.id.arrowMethod);
        final TextView textMethod = findViewById(R.id.textMethod);
        CardView BeginExerciseButton = findViewById(R.id.beginexercisebutton);

        arrowMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(textMethod, arrowMethod);
            }
        });

        BeginExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // Retrieve the EqualBreathingCounterValue from SharedPreferences
//                SharedPreferences sharedPreferences = getSharedPreferences("EqualBreathingCounter", Context.MODE_PRIVATE);
//                int equalBreathingCounterValue = sharedPreferences.getInt("EqualBreathingCounterValue", 2);
//
//                // Pass the value to the EqualBreathingExercise activity
//                Intent intent = new Intent(Equal_Breathing_description.this, EqualBreathingExercise.class);
//                intent.putExtra("equal_breathing_counter_value", equalBreathingCounterValue);
//                startActivity(intent);

                Intent intent = new Intent(Equal_Breathing_description.this, EqualBreathingExercise.class);
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
