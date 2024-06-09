package com.hareem.anxietyrelief.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.hareem.anxietyrelief.Aboutus;
import com.hareem.anxietyrelief.Patient_change_password;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.Therapist_change_password;
import com.hareem.anxietyrelief.databinding.FragmentSettingsBinding;
import com.hareem.anxietyrelief.logoutdialoguebox; // Import your dialog

public class    SettingsFragment extends Fragment {

    TextView therpaist_username;
    TextView therapist_email;

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel dashboardViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CardView changePasswordCardView = root.findViewById(R.id.changepasswordicon);

        changePasswordCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ChangePasswordActivity
                Intent intent = new Intent(getActivity(), Therapist_change_password.class);
                startActivity(intent);
            }
        });

        CardView AboutUsCardView = root.findViewById(R.id.about);

        AboutUsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ChangePasswordActivity
                Intent intent = new Intent(getActivity(), Aboutus.class);
                startActivity(intent);
            }
        });

        // Add an OnClickListener for the Logout CardView
        CardView logoutCardView = root.findViewById(R.id.logouticon);

        logoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the logout dialog
                logoutdialoguebox dialog = new logoutdialoguebox();
                dialog.show(getFragmentManager(), "logout_dialog");
            }
        });
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("therapist_username2", null);
        String email = sharedPreferences.getString("therapist_email", null);

        // Find the TextInputEditText for the username and email
        TextView therapistusername = binding.therapistusername;
        TextView therapistemail = binding.therapistemail;

        // Set the retrieved username and email in the TextInputEditText fields
        if (username != null) {
            therapistusername.setText(username);
        }

        if (email != null) {
            therapistemail.setText(email);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
