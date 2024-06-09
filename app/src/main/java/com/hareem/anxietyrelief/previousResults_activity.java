package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class previousResults_activity extends AppCompatActivity {

    private LinearLayout linearLayout;
    TextView noresults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_results);
        linearLayout = findViewById(R.id.owncardlist2); // Replace with the ID of your LinearLayout
         noresults=findViewById(R.id.noresults);
        // Assuming you have a patientId, replace "YOUR_PATIENT_ID" with the actual patientId
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String currentPatientId = preferences.getString("currentPatientId", "");
        getAnxietyLevels(currentPatientId);
    }

    private void getAnxietyLevels(String patientId) {
        Log.d("resultsss", "Patient Id: " + patientId);

        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

        Call<List<AnxietyLevel>> call = patientAPI.getAnxietyLevels(patientId);
        call.enqueue(new Callback<List<AnxietyLevel>>() {
            @Override
            public void onResponse(Call<List<AnxietyLevel>> call, Response<List<AnxietyLevel>> response) {
                if (response.isSuccessful()) {
                    List<AnxietyLevel> anxietyLevels = response.body();
                    if (anxietyLevels != null && !anxietyLevels.isEmpty()) {
                        for (AnxietyLevel anxietyLevel : anxietyLevels) {
                            addCardToLayout(anxietyLevel, patientId);
                            Log.d("resultsss", String.valueOf(anxietyLevel));
                        }
                    } else {

                    }
                    if (anxietyLevels.isEmpty()) {
                        noresults.setVisibility(View.VISIBLE);
                    } else {
                        noresults.setVisibility(View.GONE);
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<AnxietyLevel>> call, Throwable t) {
                Log.e("resultsss", "Error fetching anxiety levels: " + t.getMessage());
            }
        });
    }

    private void addCardToLayout(AnxietyLevel anxietyLevel, String currentPatientId) {
        // Inflate the card layout
        View cardView = getLayoutInflater().inflate(R.layout.anxiety_level_cardlist, null);

        // Set tag for identifying the card
        cardView.setTag(anxietyLevel.get_id());

        // Find views in your card layout
        TextView levelTextView = cardView.findViewById(R.id.level);
        TextView dateTimeTextView = cardView.findViewById(R.id.dateTime);

        // Set data to views
        levelTextView.setText(anxietyLevel.getLevel());
        dateTimeTextView.setText(anxietyLevel.getDateTime());

        // Set click event for delete icon
        cardView.findViewById(R.id.deleteicon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAnxietyLevel(currentPatientId, anxietyLevel.get_id());
            }
        });

        // Add the card to the linear layout
        linearLayout.addView(cardView);
    }

    private void deleteAnxietyLevel(String patientId, String anxietyLevelId) {
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("patientId", patientId);
        requestBody.addProperty("_id", anxietyLevelId);

        Call<JsonObject> call = patientAPI.deleteAnxietyLevel(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject result = response.body();
                    if (result != null && result.has("success") && result.get("success").getAsBoolean()) {
                        // Deletion successful, you may want to remove the corresponding card from the layout
                        removeCardFromLayout(anxietyLevelId);
                    } else {
                        Toast.makeText(previousResults_activity.this, "Failed to delete anxiety level", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(previousResults_activity.this, "Failed to delete anxiety level", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle failure
                Toast.makeText(previousResults_activity.this, "Failed to delete anxiety level: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeCardFromLayout(String anxietyLevelId) {
        // Iterate through your linear layout and remove the card with the matching anxietyLevelId
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View child = linearLayout.getChildAt(i);
            // Check if the child's tag matches the anxietyLevelId
            if (child.getTag() != null && child.getTag().equals(anxietyLevelId)) {
                linearLayout.removeViewAt(i);
                break; // Break the loop since we found and removed the card
            }
        }
    }
}
