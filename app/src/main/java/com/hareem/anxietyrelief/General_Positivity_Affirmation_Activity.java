package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.database.FirebaseDatabase;
import com.hareem.anxietyrelief.Adapter.Adapter_Affirmation;


import java.util.ArrayList;

public class General_Positivity_Affirmation_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Affirmations> affirmations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_general_positivity_affirmation);
        recyclerView=findViewById(R.id.recyclerview);


        affirmations=new ArrayList<>();
        affirmations.add(new Affirmations("I radiate positivity and attract good vibes into every area of my life.","11"));
        affirmations.add(new Affirmations("I am grateful for the abundance of positivity that surrounds me, and I choose to focus on the bright side of every situation.","12"));
        affirmations.add(new Affirmations("Each day is a new opportunity for joy and growth, and I embrace it with a positive mindset.","13"));
        affirmations.add(new Affirmations("I am a beacon of positivity, and my optimism empowers me to overcome challenges with grace and resilience.","14"));
        affirmations.add(new Affirmations("I choose to see the good in myself and others, and I approach life with an open heart and a positive attitude.","15"));










        Adapter_Affirmation adapter = new Adapter_Affirmation(this, affirmations);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        // Use PagerSnapHelper for snapping to pages
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);



    }
}