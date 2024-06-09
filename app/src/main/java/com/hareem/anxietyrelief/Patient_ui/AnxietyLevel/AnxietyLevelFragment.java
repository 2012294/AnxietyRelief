package com.hareem.anxietyrelief.Patient_ui.AnxietyLevel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.hareem.anxietyrelief.Patient_ui.Affirmation.AffirmationFragment;
import com.hareem.anxietyrelief.Questions_activity;

import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.databinding.FragmentAnxietyLevelBinding;
import com.hareem.anxietyrelief.previousResults_activity;

public class AnxietyLevelFragment extends Fragment {

    private FragmentAnxietyLevelBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAnxietyLevelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.startAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Questions_activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);



            }
        });
        binding.previousResultsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), previousResults_activity.class);
                startActivity(i);
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Finish the entire app when the back button is pressed in the fragment
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
