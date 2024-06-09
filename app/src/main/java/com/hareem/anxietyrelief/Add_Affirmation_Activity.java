package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add_Affirmation_Activity extends AppCompatActivity {
    AppCompatButton add;
    LinearLayout linearLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_affirmation);

        add=findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_add_affirmation bottomAddAffirmation=new bottom_add_affirmation();


                bottomAddAffirmation.show(getSupportFragmentManager(), bottomAddAffirmation.getTag());
            }
        });
        linearLayout = findViewById(R.id.owncardlist1);
        loadAffirmationsFromdb();

    }

    public void updateAffirmations(String quote_, String Pid, String affirmationId) {
        // Check if the view already exists in the layout
        View existingView = linearLayout.findViewWithTag(affirmationId);

        if (existingView != null) {
            // Remove the existing view from its current position
            ((ViewGroup) existingView.getParent()).removeView(existingView);
        }

        // Create a new view or use the existing one
        View view = getLayoutInflater().inflate(R.layout.own_affirmation_cardlist, null);

        // Set a tag to identify the view later
        view.setTag(affirmationId);

        TextView quoteTextView = view.findViewById(R.id.quotetextview);
        TextView dateTextView = view.findViewById(R.id.date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        Date currentDate = new Date();
        String date_ = dateFormat.format(currentDate);

        ImageView deleteIcon = view.findViewById(R.id.deleteiconarrow);
        ImageView editIcon = view.findViewById(R.id.editiconarrow);
        deleteIcon.setImageResource(R.drawable.baseline_delete_24);
        editIcon.setImageResource(R.drawable.baseline_edit_24);
        dateTextView.setText(date_);
        quoteTextView.setText(quote_);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the parent view (the entire card)
                ((ViewGroup) view.getParent()).removeView(view);
                deleteAffirmationOnServer(Pid, affirmationId);

            }
        });

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_edit_affirmation bottomEditAffirmation = new bottom_edit_affirmation(quote_, affirmationId);
                bottomEditAffirmation.show(getSupportFragmentManager(), bottomEditAffirmation.getTag());
            }
        });

        // Add the view to the layout (at the end)
        linearLayout.addView(view);
    }
    private void deleteAffirmationOnServer(String patientId, String affirmationId) {
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

        // Create a JSONObject containing patientId and affirmationId
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("patientId", patientId);
            requestBodyJson.put("affirmationId", affirmationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a RequestBody from the JSONObject
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString());

        Call<JsonObject> call = patientAPI.deleteAffirmation(requestBody);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Handle the response
                if (!response.isSuccessful()) {
                    // Handle unsuccessful response
                    // You might want to show an error message
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle failure
                // You might want to show an error message
            }
        });
    }
    private void loadAffirmationsFromdb() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

        Call<List<Affirmations>> call = patientAPI.getOwnAffirmations(patientId);

        call.enqueue(new Callback<List<Affirmations>>() {
            @Override
            public void onResponse(Call<List<Affirmations>> call, Response<List<Affirmations>> response) {
                if (response.isSuccessful()) {
                    List<Affirmations> ownAffirmations = response.body();

                    // Process and display the loaded affirmations
                    for (Affirmations affirmation : ownAffirmations) {
                        updateAffirmations(affirmation.getQuote(), patientId, affirmation.getId());
                    }
                } else {
                    // Handle unsuccessful response
                    // You might want to show an error message
                }
            }

            @Override
            public void onFailure(Call<List<Affirmations>> call, Throwable t) {
                // Handle failure
                // You might want to show an error message
            }
        });
    }






}