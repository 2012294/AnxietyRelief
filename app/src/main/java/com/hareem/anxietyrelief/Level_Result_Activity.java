package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Level_Result_Activity extends AppCompatActivity {
    private PatientAPI patientAPI;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_result);
        ImageView back=findViewById(R.id.back_Arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // Use Intent to get the result
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("result")) {
             result = intent.getStringExtra("result");

            // Example: Display the result in a TextView
            TextView resultTextView = findViewById(R.id.resulttext);
            TextView resultTextViewheading = findViewById(R.id.resulttextheading);
            resultTextViewheading.setText(result + " Level");

            // Show different positive messages based on the result
            String message;
            switch (result) {
                case "Low":
                    message = "Congratulations! Your anxiety level is Low. Keep up the positive mindset and continue practicing self-care. Remember, taking care of your mental health is a crucial part of your overall well-being.";
                    break;
                case "Mild":
                    message = "Great news! Your anxiety level is Mild. Continue to focus on self-care and well-being. Remember to prioritize your mental health and take breaks when needed.";
                    break;
                case "Moderate":
                    message = "Your anxiety level is Moderate. Consider incorporating relaxation techniques into your routine. Take some time for yourself and engage in activities that bring you joy and relaxation.";
                    break;
                case "High":
                    message = "Your anxiety level is High. It's important to reach out to a mental health professional for support. Remember, seeking help is a sign of strength, and you don't have to face this alone.";
                    break;
                default:
                    message = "Invalid result. Please try again.";
                    break;
            }

            resultTextView.setText(message);
        }

        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve patient ID from SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                String currentPatientId = preferences.getString("currentPatientId", "");

                // Get the current date and time
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);

                patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);
                Call<Void> call = patientAPI.saveAnxietyLevel(currentPatientId, result, formattedDateTime);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Handle success, e.g., show a toast message
                            Toast.makeText(Level_Result_Activity.this, "Results saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle failure, e.g., show an error message
                            Toast.makeText(Level_Result_Activity.this, "Failed to save anxiety level", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Handle failure, e.g., show an error message
                        Toast.makeText(Level_Result_Activity.this, "Network error. Failed to save anxiety level", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
