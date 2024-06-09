package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;

import com.hareem.anxietyrelief.Adapter.Adapter_Affirmation;

import java.util.ArrayList;

public class Health_Affirmation_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Affirmations> affirmations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_affirmation);
        recyclerView=findViewById(R.id.recyclerview);

        affirmations=new ArrayList<>();
        affirmations.add(new Affirmations("I am grateful for my strong and vibrant body, and I treat it with love and care every day.","6"));
        affirmations.add(new Affirmations("Every breath I take fills me with vitality, and I am grateful for the abundance of energy in my life.","7"));
        affirmations.add(new Affirmations("I choose nourishing foods that support my well-being, and I am committed to a healthy and balanced lifestyle.","8"));
        affirmations.add(new Affirmations( "I am in tune with my body's needs, and I trust its signals for rest, movement, and nourishment.","9"));
        affirmations.add(new Affirmations(   "My mind and body are in harmony, and I radiate health, happiness, and positivity.","10"));
















        Adapter_Affirmation adapter = new Adapter_Affirmation(this, affirmations);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        // Use PagerSnapHelper for snapping to pages
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


    }
}