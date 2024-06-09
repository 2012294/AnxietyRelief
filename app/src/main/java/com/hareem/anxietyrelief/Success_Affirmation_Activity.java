package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;

import com.hareem.anxietyrelief.Adapter.Adapter_Affirmation;


import java.util.ArrayList;

public class Success_Affirmation_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Affirmations> affirmations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_affirmation);
        recyclerView=findViewById(R.id.recyclerview);

        affirmations=new ArrayList<>();
        affirmations.add(new Affirmations(  "I am deserving of success, and I attract opportunities that lead me towards my goals.","16"));
        affirmations.add(new Affirmations("I am confident, capable, and have the power to overcome any challenges that come my way.","17"));
        affirmations.add(new Affirmations(   "Each day, I am moving closer to my goals, and I celebrate the small victories along the way.","18"));
        affirmations.add(new Affirmations("I trust in my ability to make smart decisions, and I create a path of success with every choice I make.","19"));
        affirmations.add(new Affirmations("I am a magnet for success, and I attract positive outcomes in all areas of my life.","20"));





















        Adapter_Affirmation adapter = new Adapter_Affirmation(this, affirmations);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        // Use PagerSnapHelper for snapping to pages
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


    }
}