package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.hareem.anxietyrelief.Adapter.Adapter_Affirmation;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Own_Affirmation_Activity extends AppCompatActivity {
    ImageView forawrd;
    RecyclerView recyclerView;
    ArrayList<Affirmations> affirmations;
    ProgressBar progressBar;
    TextView noFavoritesTextView;
    Adapter_Affirmation adapter;
    String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_affirmation);
        forawrd=findViewById(R.id.forward);
       forawrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Own_Affirmation_Activity.this, Add_Affirmation_Activity.class);

                startActivity(i);

            }
        });
        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progressBar);

        affirmations = new ArrayList<>();

        noFavoritesTextView = findViewById(R.id.noFavoritesTextView);

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        patientId = preferences.getString("currentPatientId", "");




        // Initialize the adapter
        adapter = new Adapter_Affirmation(this, affirmations);

        // Use PagerSnapHelper for snapping to pages
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // Retrieve affirmations
        retrieveAffirmations(patientId);





    }


    private void retrieveAffirmations(String patientId) {

        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

        Call<List<Affirmations>> call = patientAPI.getOwnAffirmations(patientId);

        call.enqueue(new Callback<List<Affirmations>>() {

            @Override
            public void onResponse(Call<List<Affirmations>> call, Response<List<Affirmations>> response) {
                if (response.isSuccessful()) {
                    List<Affirmations> ownAffirmations = response.body();

                    // Clear the existing affirmations list
                    affirmations.clear();

                    // Add the retrieved affirmations to the list
                    for (Affirmations affirmation : ownAffirmations) {
                        Log.d("own1", "own: " + affirmation.toString());
                        Affirmations affirmation1 = new Affirmations(affirmation.getQuote(), affirmation.getId());
                        Log.d("own1", "own1: " + affirmation1);
                        affirmations.add(affirmation1);
                    }

                    // Notify the adapter about the data change
                    adapter.notifyDataSetChanged();

                    if (affirmations.isEmpty()) {
                        noFavoritesTextView.setVisibility(View.VISIBLE);
                    } else {
                        noFavoritesTextView.setVisibility(View.GONE);
                    }
                } else {
                    // Handle unsuccessful response
                }
            
            }


            @Override
            public void onFailure(Call<List<Affirmations>> call, Throwable t) {
                // Handle failure
                // You might want to show an error message
            }
        });


    }






    public void onBackPressed() {

      //  ((PatientNavigationDrawerActivity) getParent()).loadAffirmationFragment();
        super.onBackPressed();
    }
    protected void onResume() {
        super.onResume();
        affirmations.clear();

        // Retrieve affirmations
        retrieveAffirmations(patientId);

        // Notify the adapter about the data change
        adapter.notifyDataSetChanged();

    }



}

