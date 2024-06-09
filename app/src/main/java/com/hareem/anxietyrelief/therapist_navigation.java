package com.hareem.anxietyrelief;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.hareem.anxietyrelief.databinding.ActivityTherapistNavigationBinding;
import com.hareem.anxietyrelief.ui.profile.ProfileFragment;

public class therapist_navigation extends AppCompatActivity   {

    private ActivityTherapistNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTherapistNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_therapist_home_page);
        NavigationUI.setupWithNavController(navView, navController);



    }


}
