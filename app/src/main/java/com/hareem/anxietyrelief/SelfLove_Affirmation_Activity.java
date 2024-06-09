package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;

import com.hareem.anxietyrelief.Adapter.Adapter_Affirmation;

import java.util.ArrayList;

public class SelfLove_Affirmation_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Affirmations> affirmations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_love_affirmation);
        recyclerView = findViewById(R.id.recyclerview);

        affirmations = new ArrayList<>();
        affirmations.add(new Affirmations("I love and accept myself unconditionally.","1"));
        affirmations.add(new Affirmations("I am worthy of love and kindness.","2"));
        affirmations.add(new Affirmations("My worth is not determined by external validation; I am enough as I am.","3"));
        affirmations.add(new Affirmations("I treat myself with compassion and respect.","4"));
        affirmations.add(new Affirmations("I embrace my flaws and imperfections; they make me unique and special.","5"));



        Adapter_Affirmation adapter = new Adapter_Affirmation(this, affirmations);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // Use PagerSnapHelper for snapping to pages
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }


}