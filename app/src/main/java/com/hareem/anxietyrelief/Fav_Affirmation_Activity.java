package com.hareem.anxietyrelief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hareem.anxietyrelief.Adapter.Adapter_Affirmation;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fav_Affirmation_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Affirmations> affirmations;

    ProgressBar progressBar;
    TextView noFavoritesTextView;
    Adapter_Affirmation adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_affirmation);
        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progressBar);

        affirmations = new ArrayList<>();

      noFavoritesTextView = findViewById(R.id.noFavoritesTextView);

        SharedPreferences preferences =getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String currentPatientId = preferences.getString("currentPatientId", "");
        retrieveAffirmations(currentPatientId);


        adapter = new Adapter_Affirmation(this, affirmations);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // Using PagerSnapHelper for snapping to pages
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);




    }


    private void retrieveAffirmations(String patientId) {
        progressBar.setVisibility(View.VISIBLE);
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);


        Call<List<Affirmations>> call = patientAPI.getFavoriteAffirmations(patientId);
        call.enqueue(new Callback<List<Affirmations>>() {
            @Override
            public void onResponse(Call<List<Affirmations>> call, Response<List<Affirmations>> response) {
                if (response.isSuccessful()) {
                    List<Affirmations> affirmationsList = response.body();
                    Log.d("Affirmations", "Received affirmations: " + affirmationsList);

                    if (affirmationsList != null && !affirmationsList.isEmpty()) {
                        // Iterate through each affirmation

                        for (Affirmations affirmation : affirmationsList) {
                            Log.d("Affirmation", "Affirmation: " + affirmation.toString());

                            // Access the list of favorites for each affirmation
                            List<Affirmations.Favorite> favoritesList = affirmation.getFavorites();
                            if (favoritesList != null && !favoritesList.isEmpty()) {
                                for (Affirmations.Favorite favorite : favoritesList) {
                                    Log.d("Favorite", "Favorite: " + favorite.toString());


                                    affirmations.add(new Affirmations( favorite.getId(),favorite.isFavorite(),favorite.getQuote()));
                                }
                            } else {
                                Log.d("Favorite", "Favorites list is null or empty");
                                noFavoritesTextView.setVisibility(View.VISIBLE);
                            }

                        }

                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Affirmations", "Affirmations list is null or empty");
                    }
                } else {
                    Log.e("Affirmations", "Unsuccessful response: " + response.errorBody());
                }
                if (affirmations.isEmpty()) {
                    noFavoritesTextView.setVisibility(View.VISIBLE);
                } else {
                    noFavoritesTextView.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.GONE);
            }



            @Override
            public void onFailure(Call<List<Affirmations>> call, Throwable t) {
                // Handle failure
            }
        });

    }








}
