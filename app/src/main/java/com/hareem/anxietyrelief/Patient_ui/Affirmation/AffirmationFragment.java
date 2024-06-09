package com.hareem.anxietyrelief.Patient_ui.Affirmation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.hareem.anxietyrelief.Fav_Affirmation_Activity;
import com.hareem.anxietyrelief.General_Positivity_Affirmation_Activity;
import com.hareem.anxietyrelief.Health_Affirmation_Activity;
import com.hareem.anxietyrelief.Own_Affirmation_Activity;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.SelfLove_Affirmation_Activity;
import com.hareem.anxietyrelief.Success_Affirmation_Activity;
import com.hareem.anxietyrelief.Theme_Affirmation_Activity;
import com.hareem.anxietyrelief.TherapistLogin;
import com.hareem.anxietyrelief.databinding.FragmentAffirmationBinding;

public class AffirmationFragment extends Fragment {

    private FragmentAffirmationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAffirmationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

           binding.selflove.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent i = new Intent(getActivity(), SelfLove_Affirmation_Activity.class);
                   startActivity(i);

               }
           });
           binding.theme.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent i = new Intent(getActivity(), Theme_Affirmation_Activity.class);
                   startActivity(i);
               }
           });
        binding.success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Success_Affirmation_Activity.class);
                startActivity(i);

            }
        });
        binding.health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Health_Affirmation_Activity.class);
                startActivity(i);

            }
        });
        binding.generalpositivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), General_Positivity_Affirmation_Activity.class);
                startActivity(i);

            }
        });
        binding.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Fav_Affirmation_Activity.class);
                startActivity(i);

            }
        });
        binding.ownAffirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Own_Affirmation_Activity.class);
                startActivity(i);

            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Finish the entire app when the back button is pressed in the fragment
                //requireActivity().finishAffinity();
                requireActivity().finishAffinity();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}