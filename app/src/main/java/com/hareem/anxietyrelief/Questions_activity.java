package com.hareem.anxietyrelief;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.HashMap;
import java.util.Map;

public class Questions_activity extends AppCompatActivity {
    private Map<Integer, Integer> userResponses = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qustions);

        Button btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areAllQuestionsAnswered()) {
                    // All questions are answered, proceed with your logic
                    int totalScore = calculateTotalScore();
                   String result= displayResult(totalScore);
                    navigateToResult(result);
                } else {
                    // Display a message if not all questions are answered
                    Toast.makeText(Questions_activity.this, "Please answer all the questions.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean areAllQuestionsAnswered() {
        // Iterate through each RadioGroup to check if an option is selected for each question
        for (int i = 1; i <= 10; i++) {
            RadioGroup radioGroup = findViewById(R.id.radioGroup+i);

            // Check if the RadioGroup is null
            if (radioGroup == null || radioGroup.getCheckedRadioButtonId() == -1) {
                // No option selected for this question
                return false;
            } else {
                // Store the selected option for later use
                userResponses.put(i, radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId())) + 1);
            }
        }
        return true;
    }


    private int calculateTotalScore() {
        int totalScore = 0;

        // Iterate through the stored user responses and calculate the total score
        for (int i = 1; i <= 10; i++) {
            int selectedOptionIndex = userResponses.get(i);
            Log.d("answerss", String.valueOf(selectedOptionIndex));

            // Assign scores based on the provided calculation method
            int optionScore;
            switch (selectedOptionIndex-1) {
                case 0:
                    optionScore = 0;
                    break;
                case 1:
                    optionScore = 1;
                    break;
                case 2:
                    optionScore = 2;
                    break;
                case 3:
                    optionScore = 3;
                    break;
                default:
                    optionScore = 0;
                    break;
            }


            totalScore += optionScore;
            Log.d("answerss", String.valueOf(totalScore));
        }

        return totalScore;
    }

    private void navigateToResult(String result) {
        Intent intent = new Intent(this, Level_Result_Activity.class);
        intent.putExtra("result", result);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    private String displayResult(int totalScore) {
        if (totalScore <= 5) {
            return "Low";
        } else if (totalScore <= 10) {
            return "Mild";
        } else if (totalScore <= 15) {
            return "Moderate";
        } else {
            return "High";
        }
    }

}

