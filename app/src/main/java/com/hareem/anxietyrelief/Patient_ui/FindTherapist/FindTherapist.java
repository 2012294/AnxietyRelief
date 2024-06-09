package com.hareem.anxietyrelief.Patient_ui.FindTherapist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.hareem.anxietyrelief.Adapter.TherapistAdapter;
import com.hareem.anxietyrelief.BreathingExercisesAPI;
import com.hareem.anxietyrelief.PatientAPI;
import com.hareem.anxietyrelief.Patient_ui.Affirmation.AffirmationFragment;
import com.hareem.anxietyrelief.Questions_activity;

import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.RetrofitClientInstance;
import com.hareem.anxietyrelief.Therapist;
import com.hareem.anxietyrelief.databinding.FragmentAnxietyLevelBinding;
import com.hareem.anxietyrelief.databinding.FragmentFindTherapistBinding;
import com.hareem.anxietyrelief.previousResults_activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTherapist extends Fragment {

    private FragmentFindTherapistBinding binding;
    private RecyclerView recyclerView;
    private TherapistAdapter therapistAdapter;

    private EditText search;
    private String[] chargesRanges = {"0 - 500", "501 - 1000", "1001 - 2000", "2001 - 3000", "> 3001"};

    private List<Therapist> allTherapists;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFindTherapistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        search = binding.editTextSearch;
        search.addTextChangedListener(textWatcher);

        recyclerView = root.findViewById(R.id.recyclerView_therapists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        therapistAdapter = new TherapistAdapter();
        recyclerView.setAdapter(therapistAdapter);




        fetchTherapists();

        ImageButton filterButton = root.findViewById(R.id.button_filter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finishAffinity();
            }
        });

        return root;
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Filter the list based on the search query
            filterTherapists(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void filterTherapists(String query) {
        List<Therapist> filteredList = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();
        for (Therapist therapist : allTherapists) {
            String therapistName = therapist.getUsername().toLowerCase();

            // Check if therapist's name contains the query
            if (therapistName.contains(lowerCaseQuery)) {
                filteredList.add(therapist);
            }
        }
        therapistAdapter.setTherapists(filteredList); // Update RecyclerView with filtered list
    }

    private void fetchTherapists() {
        // Call the API to get therapists
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);
        Call<List<Therapist>> call = patientAPI.getTherapists();
        call.enqueue(new Callback<List<Therapist>>() {
            @Override
            public void onResponse(Call<List<Therapist>> call, Response<List<Therapist>> response) {
                if (response.isSuccessful()) {
                    // Data fetched successfully
                    List<Therapist> therapists = response.body();
                    allTherapists = therapists; // Store all therapists
                    therapistAdapter.setTherapists(therapists);
                } else {

                    Toast.makeText(getContext(), "Failed to fetch therapists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Therapist>> call, Throwable t) {

                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        Spinner degreeSpinner = dialogView.findViewById(R.id.degreeSpinner1);
        ArrayAdapter<CharSequence> degreeAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.degree_array, android.R.layout.simple_spinner_item);
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        degreeSpinner.setAdapter(degreeAdapter);

        Spinner daySpinner = dialogView.findViewById(R.id.daySpinner1);
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.days_of_week1, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        Spinner spinnerCharges = dialogView.findViewById(R.id.spinnerCharges);
        ArrayAdapter<String> chargesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, chargesRanges);
        chargesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCharges.setAdapter(chargesAdapter);

        builder.setPositiveButton("Apply Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedDegree = (String) degreeSpinner.getSelectedItem();
                String selectedDay = (String) daySpinner.getSelectedItem();
                String selectedChargesRange = (String) spinnerCharges.getSelectedItem();

                if (selectedDay.equals("ALL")) {
                    if (selectedDegree.equals("ALL")) {
                        filterTherapistsByCharges(selectedChargesRange);
                    } else {
                        filterTherapistsByCharges(selectedChargesRange, selectedDegree);
                    }
                } else {
                    if (selectedDegree.equals("ALL")) {
                        filterTherapistsByDayAndCharges(selectedDay, selectedChargesRange);
                    } else {
                        filterTherapistsByDegreeAndDayAndCharges(selectedDegree, selectedDay, selectedChargesRange);
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                // Show all therapists when cancel is clicked
                fetchTherapists();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }




    private void filterTherapistsByDegreeAndDayAndCharges(String selectedDegree, String selectedDay, String selectedChargesRange) {
        List<Therapist> filteredList = new ArrayList<>();

        double minCharges;
        double maxCharges;
        if (selectedChargesRange.equals("> 3001")) {
            minCharges = 3001;
            maxCharges = Double.MAX_VALUE;
        } else {
            String[] rangeParts = selectedChargesRange.split(" - ");
            minCharges = Double.parseDouble(rangeParts[0]);
            maxCharges = Double.parseDouble(rangeParts[1]);
        }

        for (Therapist therapist : allTherapists) {
            double therapistCharges = Double.parseDouble(therapist.getCharges());
            List<String> availableTime = therapist.getAvailableTime();
            if (therapistCharges >= minCharges && therapistCharges <= maxCharges
                    && therapist.getDegree().contains(selectedDegree)
                    && availabilityContainsDay(availableTime, selectedDay)) {
                filteredList.add(therapist);
            }
        }

        therapistAdapter.setTherapists(filteredList); // Update RecyclerView with filtered list
    }

    private void filterTherapistsByDayAndCharges(String selectedDay, String selectedChargesRange) {
        List<Therapist> filteredList = new ArrayList<>();

        double minCharges;
        double maxCharges;
        if (selectedChargesRange.equals("> 3001")) {
            minCharges = 3001;
            maxCharges = Double.MAX_VALUE;
        } else {
            String[] rangeParts = selectedChargesRange.split(" - ");
            minCharges = Double.parseDouble(rangeParts[0]);
            maxCharges = Double.parseDouble(rangeParts[1]);
        }

        for (Therapist therapist : allTherapists) {
            double therapistCharges = Double.parseDouble(therapist.getCharges());
            List<String> availableTime = therapist.getAvailableTime();
            if (therapistCharges >= minCharges && therapistCharges <= maxCharges
                    && availabilityContainsDay(availableTime, selectedDay)) {
                filteredList.add(therapist);
            }
        }

        therapistAdapter.setTherapists(filteredList); // Update RecyclerView with filtered list
    }

    private boolean availabilityContainsDay(List<String> availableTime, String selectedDay) {
        for (String availability : availableTime) {
            if (extractDayFromAvailability(availability).equals(selectedDay)) {
                return true;
            }
        }
        return false;
    }

    private String extractDayFromAvailability(String availability) {
        String[] parts = availability.split(", ");
        for (String part : parts) {
            if (part.startsWith("Day: ")) {
                return part.substring(5); // Extract the day
            }
        }
        return "";
    }

    private void filterTherapistsByCharges(String selectedChargesRange) {
        List<Therapist> filteredList = new ArrayList<>();

        double minCharges;
        double maxCharges;
        if (selectedChargesRange.equals("> 3001")) {
            minCharges = 3001;
            maxCharges = Double.MAX_VALUE;
        } else {
            String[] rangeParts = selectedChargesRange.split(" - ");
            minCharges = Double.parseDouble(rangeParts[0]);
            maxCharges = Double.parseDouble(rangeParts[1]);
        }

        for (Therapist therapist : allTherapists) {
            double therapistCharges = Double.parseDouble(therapist.getCharges());
            if (therapistCharges >= minCharges && therapistCharges <= maxCharges) {
                filteredList.add(therapist);
            }
        }

        therapistAdapter.setTherapists(filteredList); // Update RecyclerView with filtered list
    }

    private void filterTherapistsByCharges(String selectedChargesRange, String selectedDegree) {
        List<Therapist> filteredList = new ArrayList<>();

        double minCharges;
        double maxCharges;
        if (selectedChargesRange.equals("> 3001")) {
            minCharges = 3001;
            maxCharges = Double.MAX_VALUE;
        } else {
            String[] rangeParts = selectedChargesRange.split(" - ");
            minCharges = Double.parseDouble(rangeParts[0]);
            maxCharges = Double.parseDouble(rangeParts[1]);
        }

        for (Therapist therapist : allTherapists) {
            double therapistCharges = Double.parseDouble(therapist.getCharges());
            if (therapistCharges >= minCharges && therapistCharges <= maxCharges
                    && therapist.getDegree().contains(selectedDegree)) {
                filteredList.add(therapist);
            }
        }

        therapistAdapter.setTherapists(filteredList); // Update RecyclerView with filtered list
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
