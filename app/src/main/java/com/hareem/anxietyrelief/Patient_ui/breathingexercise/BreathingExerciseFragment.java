package com.hareem.anxietyrelief.Patient_ui.breathingexercise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.hareem.anxietyrelief.BoxBreathingExercise;
import com.hareem.anxietyrelief.Box_Breathing_description;
import com.hareem.anxietyrelief.BreathingExerciseMusic;
import com.hareem.anxietyrelief.BreathingExercisesAPI;
import com.hareem.anxietyrelief.BreathingExercisesModel;
import com.hareem.anxietyrelief.EqualBreathingExercise;
import com.hareem.anxietyrelief.Equal_Breathing_description;

import com.hareem.anxietyrelief.FourSevenEightExercise;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.RetrofitClientInstance;
import com.hareem.anxietyrelief.TriangleBreathingExercise;
import com.hareem.anxietyrelief.Triangle_Breathing_description;
import com.hareem.anxietyrelief.databinding.FragmentBreathingExerciseBinding;
import com.hareem.anxietyrelief.four_seven_eight_Breathing_description;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreathingExerciseFragment extends Fragment {

    private FragmentBreathingExerciseBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finishAffinity();
            }
        });
        binding = FragmentBreathingExerciseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        fetchBreathingCycles();

        View cardViewBackgroundSound = root.findViewById(R.id.backgroundsoundcardview);

        cardViewBackgroundSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fetchBreathingCycles();

                // Retrieve music preference from SharedPreferences with a different name
                SharedPreferences musicPreferences = requireContext().getSharedPreferences("Music", Context.MODE_PRIVATE);
                int music = musicPreferences.getInt("Music", 1);

                Intent intent = new Intent(getActivity(), BreathingExerciseMusic.class);
                intent.putExtra("music", music);
                startActivity(intent);
            }
        });


        View cardViewEqualBreathing = root.findViewById(R.id.equalbreathingcardview);

        cardViewEqualBreathing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fetchBreathingCycles();



                Intent intent = new Intent(getActivity(), EqualBreathingExercise.class);
                startActivity(intent);

            }
        });


        ImageView infoIconEqualBreathing = root.findViewById(R.id.info_icon_equalbreathing);

        // Set up shared element transition for Equal Breathing CardView
        String EqualBreathingTransition = getString(R.string.transition_equal_breathing_card);
        ViewCompat.setTransitionName(cardViewEqualBreathing, EqualBreathingTransition);


        infoIconEqualBreathing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the transition bundle
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        cardViewEqualBreathing,
                        EqualBreathingTransition
                );


                fetchBreathingCycles();
                Intent intent = new Intent(getActivity(), Equal_Breathing_description.class);
                startActivity(intent, options.toBundle());
            }
        });


        CardView changeDurationEqualBreathingButton = root.findViewById(R.id.change_duration_button_equal_breathing);


        changeDurationEqualBreathingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchBreathingCycles();
                showEqualBreathingChangeNumberofCyclesDialog();
            }
        });



        View cardViewBoxBreathing = root.findViewById(R.id.boxviewcardview);

        cardViewBoxBreathing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fetchBreathingCycles();


                Intent intent = new Intent(getActivity(), BoxBreathingExercise.class);
                startActivity(intent);
            }
        });


        ImageView infoIconBoxBreathing = root.findViewById(R.id.info_icon_box_breathing);

        // Set up shared element transition for Box Breathing CardView
        String transitionNameBoxBreathing = getString(R.string.transition_box_breathing_card);
        ViewCompat.setTransitionName(cardViewBoxBreathing, transitionNameBoxBreathing);

        infoIconBoxBreathing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the transition bundle
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        cardViewBoxBreathing, // Use the CardView as a shared element
                        transitionNameBoxBreathing
                );

                fetchBreathingCycles();
                Intent intent = new Intent(getActivity(), Box_Breathing_description.class);
                startActivity(intent, options.toBundle());
            }
        });

        CardView changeDurationBoxBreathingButton = root.findViewById(R.id.change_duration_button_box_breathing);


        changeDurationBoxBreathingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchBreathingCycles();
                showBoxBreathingChangeNumberofCyclesDialog();
            }
        });


        View cardView478 = root.findViewById(R.id.breathing478);

        cardView478.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fetchBreathingCycles();

                Intent intent = new Intent(getActivity(), FourSevenEightExercise.class);
                startActivity(intent);
            }
        });

        ImageView infoIcon478 = root.findViewById(R.id.info_icon_478);



        String transitionName478 = getString(R.string.transition_478_breathing_card);
        ViewCompat.setTransitionName(cardView478, transitionName478);

        infoIcon478.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the transition bundle
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        cardView478, // Use the CardView as a shared element
                        transitionName478
                );

                fetchBreathingCycles();
                Intent intent = new Intent(getActivity(), four_seven_eight_Breathing_description.class);
                startActivity(intent, options.toBundle());
            }
        });


        CardView changeDuration4_7_8BreathingButton = root.findViewById(R.id.change_duration_button_478_breathing);



        changeDuration4_7_8BreathingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchBreathingCycles();
                showFourSevenEightBreathingChangeNumberofCyclesDialog();
            }
        });




        View cardViewTriangle = root.findViewById(R.id.triangleBreathing);


        cardViewTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fetchBreathingCycles();

                Intent intent = new Intent(getActivity(), TriangleBreathingExercise.class);
                startActivity(intent);
            }
        });
        ImageView infoIconTriangle = root.findViewById(R.id.info_icon_triangle);


        String transitionNameTriangle = getString(R.string.transition_triangle_breathing_card);
        ViewCompat.setTransitionName(cardViewTriangle, transitionNameTriangle);


        infoIconTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the transition bundle
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        cardViewTriangle, // Use the CardView as a shared element
                        transitionNameTriangle
                );

                fetchBreathingCycles();
                Intent intent = new Intent(getActivity(), Triangle_Breathing_description.class);
                startActivity(intent, options.toBundle());
            }
        });

        CardView changeDurationTriangleBreathingButton = root.findViewById(R.id.change_duration_button_triangle_breathing);



        changeDurationTriangleBreathingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchBreathingCycles();
                showTriangleBreathingChangeNumberofCyclesDialog();
            }
        });




        return root;
    }

    private void fetchBreathingCycles() {
        // Get patientId from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        // Make a Retrofit API call to retrieve breathing exercises data for the patient
        BreathingExercisesAPI breathingExercisesAPI = RetrofitClientInstance.getRetrofitInstance().create(BreathingExercisesAPI.class);
        Call<List<BreathingExercisesModel>> call = breathingExercisesAPI.getBreathingExercises(patientId);

        call.enqueue(new Callback<List<BreathingExercisesModel>>() {
            @Override
            public void onResponse(Call<List<BreathingExercisesModel>> call, Response<List<BreathingExercisesModel>> response) {
                if (response.isSuccessful()) {
                    // Assuming that the response body contains a list of breathing exercises for the patient
                    List<BreathingExercisesModel> breathingExercisesList = response.body();

                    if (breathingExercisesList != null && !breathingExercisesList.isEmpty()) {
                        // Get the first entry from the list
                        BreathingExercisesModel breathingExercises = breathingExercisesList.get(0);

                        // Get the Equal Breathing value
                        int equalBreathingValue = breathingExercises.getEqualBreathing();

                        int boxBreathingValue = breathingExercises.getBoxBreathing();

                        int four_seven_eight_BreathingValue = breathingExercises.getFourseveneightBreathing();

                        int triangleBreathingValue = breathingExercises.getTrianglebreathing();

                        int music = breathingExercises.getMusic();


                        // Save the Equal Breathing value in SharedPreferences
                        SharedPreferences.Editor editor = requireContext().getSharedPreferences("EqualBreathingCounter", Context.MODE_PRIVATE).edit();
                        editor.putInt("EqualBreathingCounterValue", equalBreathingValue);
                        editor.apply();

                        SharedPreferences.Editor editor1 = requireContext().getSharedPreferences("BoxBreathingCounter", Context.MODE_PRIVATE).edit();
                        editor1.putInt("BoxBreathingCounterValue", boxBreathingValue);
                        editor1.apply();

                        SharedPreferences.Editor editor2 = requireContext().getSharedPreferences("FourSevenEightBreathingCounter", Context.MODE_PRIVATE).edit();
                        editor2.putInt("FourSevenEightBreathingCounterValue", four_seven_eight_BreathingValue);
                        editor2.apply();

                        SharedPreferences.Editor editor3 = requireContext().getSharedPreferences("TriangleBreathingCounter", Context.MODE_PRIVATE).edit();
                        editor3.putInt("TriangleBreathingCounterValue", triangleBreathingValue);
                        editor3.apply();

                        SharedPreferences.Editor editor4 = requireContext().getSharedPreferences("Music", Context.MODE_PRIVATE).edit();
                        editor4.putInt("Music", music);
                        editor4.apply();



                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("BreathingExerciseFragment", "Failed to fetch breathing exercises data for the patient");
                }
            }

            @Override
            public void onFailure(Call<List<BreathingExercisesModel>> call, Throwable t) {
                // Handle failure
                Log.e("BreathingExerciseFragment", "Error fetching breathing exercises data: " + t.getMessage());
            }
        });
    }


    private void updateEqualBreathingValue(int newEqualBreathingValue) {
        // Get patientId from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        // Make a Retrofit API call to update EqualBreathing value
        BreathingExercisesAPI breathingExercisesAPI = RetrofitClientInstance.getRetrofitInstance().create(BreathingExercisesAPI.class);

        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("equalBreathingValue", newEqualBreathingValue);

        Call<Void> call = breathingExercisesAPI.updateEqualBreathingValue(patientId, requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful update
                    Toast.makeText(requireContext(), "EqualBreathing value updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful response
                    Log.e("BreathingExerciseFragment", "Failed to update EqualBreathing value");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                Log.e("BreathingExerciseFragment", "Error updating EqualBreathing value: " + t.getMessage());
            }
        });
    }

    private void updateBoxBreathingValue(int newBoxBreathingValue) {
        // Get patientId from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        // Make a Retrofit API call to update EqualBreathing value
        BreathingExercisesAPI breathingExercisesAPI = RetrofitClientInstance.getRetrofitInstance().create(BreathingExercisesAPI.class);

        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("boxBreathingValue", newBoxBreathingValue);

        Call<Void> call = breathingExercisesAPI.updateBoxBreathingValue(patientId, requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful update

                } else {
                    // Handle unsuccessful response

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure

            }
        });
    }

    private void update478BreathingValue(int new478BreathingValue) {
        // Get patientId from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        // Make a Retrofit API call to update EqualBreathing value
        BreathingExercisesAPI breathingExercisesAPI = RetrofitClientInstance.getRetrofitInstance().create(BreathingExercisesAPI.class);

        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("FourseveneightBreathingValue", new478BreathingValue);

        Call<Void> call = breathingExercisesAPI.update478BreathingValue(patientId, requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful update

                } else {
                    // Handle unsuccessful response

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure

            }
        });
    }

    private void updateTriangleBreathingValue(int newTriangleBreathingValue) {
        // Get patientId from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        // Make a Retrofit API call to update EqualBreathing value
        BreathingExercisesAPI breathingExercisesAPI = RetrofitClientInstance.getRetrofitInstance().create(BreathingExercisesAPI.class);

        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("TriangleBreathingValue", newTriangleBreathingValue);

        Call<Void> call = breathingExercisesAPI.updatetriangleBreathingValue(patientId, requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful update
                    Toast.makeText(requireContext(), "TriangleBreathing Cycle value updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful response
                    Log.e("BreathingExerciseFragment", "Failed to update EqualBreathing value");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                Log.e("BreathingExerciseFragment", "Error updating EqualBreathing value: " + t.getMessage());
            }
        });
    }


    private void showEqualBreathingChangeNumberofCyclesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_equal_breathing_breath_cycle_number, null);
        builder.setView(dialogView);

        EditText editTextCycles = dialogView.findViewById(R.id.editTextCycles);
        Button minusButton = dialogView.findViewById(R.id.minusButton);
        Button plusButton = dialogView.findViewById(R.id.plusButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);



        fetchBreathingCycles();


        // Retrieve the last saved value from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("EqualBreathingCounter", Context.MODE_PRIVATE);
        int lastSavedValue = sharedPreferences.getInt("EqualBreathingCounterValue", 2); // Default value 2, change as needed


        // Set initial value for the EditText
        editTextCycles.setText(String.valueOf(lastSavedValue));

        // Declare the dialog variable
        AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(editTextCycles.getText().toString());
                if (currentValue > 1) {
                    editTextCycles.setText(String.valueOf(currentValue - 1));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(editTextCycles.getText().toString());
                if (currentValue < 100) {
                    editTextCycles.setText(String.valueOf(currentValue + 1));
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the final value
                int finalValue = Integer.parseInt(editTextCycles.getText().toString());

                // Save the value in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("EqualBreathingCounterValue", finalValue);
                editor.apply();

                updateEqualBreathingValue(finalValue);

                // Close the dialog
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog without saving
                dialog.dismiss();
            }
        });

        // Set rounded corner background for the dialog window
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_box);

        // Show the dialog
        dialog.show();
    }



    private void showBoxBreathingChangeNumberofCyclesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_box_breathing_breath_cycle_number, null);
        builder.setView(dialogView);

        EditText editTextCycles = dialogView.findViewById(R.id.editTextCycles);
        Button minusButton = dialogView.findViewById(R.id.minusButton);
        Button plusButton = dialogView.findViewById(R.id.plusButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);


        fetchBreathingCycles();

        // Retrieve the last saved value from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("BoxBreathingCounter", Context.MODE_PRIVATE);
        int lastSavedValue = sharedPreferences.getInt("BoxBreathingCounterValue", 2); // Default value 2, change as needed


        // Set initial value for the EditText
        editTextCycles.setText(String.valueOf(lastSavedValue));

        // Declare the dialog variable
        AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(editTextCycles.getText().toString());
                if (currentValue > 1) {
                    editTextCycles.setText(String.valueOf(currentValue - 1));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(editTextCycles.getText().toString());
                if (currentValue < 100) {
                    editTextCycles.setText(String.valueOf(currentValue + 1));
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the final value
                int finalValue = Integer.parseInt(editTextCycles.getText().toString());

                // Save the value in SharedPreferences
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putInt("BoxBreathingCounterValue", finalValue);
                editor1.apply();

                updateBoxBreathingValue(finalValue);

                // Close the dialog
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog without saving
                dialog.dismiss();
            }
        });

        // Set rounded corner background for the dialog window
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_box);

        // Show the dialog
        dialog.show();
    }

    private void showFourSevenEightBreathingChangeNumberofCyclesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_478_breathing_breath_cycle_number, null);
        builder.setView(dialogView);

        EditText editTextCycles = dialogView.findViewById(R.id.editTextCycles);
        Button minusButton = dialogView.findViewById(R.id.minusButton);
        Button plusButton = dialogView.findViewById(R.id.plusButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);


        fetchBreathingCycles();

        // Retrieve the last saved value from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("FourSevenEightBreathingCounter", Context.MODE_PRIVATE);
        int lastSavedValue = sharedPreferences.getInt("FourSevenEightBreathingCounterValue", 2); // Default value 2, change as needed


        // Set initial value for the EditText
        editTextCycles.setText(String.valueOf(lastSavedValue));

        // Declare the dialog variable
        AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(editTextCycles.getText().toString());
                if (currentValue > 1) {
                    editTextCycles.setText(String.valueOf(currentValue - 1));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(editTextCycles.getText().toString());
                if (currentValue < 100) {
                    editTextCycles.setText(String.valueOf(currentValue + 1));
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the final value
                int finalValue = Integer.parseInt(editTextCycles.getText().toString());

                // Save the value in SharedPreferences
                SharedPreferences.Editor editor2 = sharedPreferences.edit();
                editor2.putInt("FourSevenEightBreathingCounterValue", finalValue);
                editor2.apply();

                update478BreathingValue(finalValue);

                // Close the dialog
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog without saving
                dialog.dismiss();
            }
        });

        // Set rounded corner background for the dialog window
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_box);

        // Show the dialog
        dialog.show();
    }





    private void showTriangleBreathingChangeNumberofCyclesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_478_breathing_breath_cycle_number, null);
        builder.setView(dialogView);

        EditText editTextCycles = dialogView.findViewById(R.id.editTextCycles);
        Button minusButton = dialogView.findViewById(R.id.minusButton);
        Button plusButton = dialogView.findViewById(R.id.plusButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        fetchBreathingCycles();
        // Retrieve the last saved value from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("TriangleBreathingCounter", Context.MODE_PRIVATE);
        int lastSavedValue = sharedPreferences.getInt("TriangleBreathingCounterValue", 2); // Default value 2, change as needed


        // Set initial value for the EditText
        editTextCycles.setText(String.valueOf(lastSavedValue));

        // Declare the dialog variable
        AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(editTextCycles.getText().toString());
                if (currentValue > 1) {
                    editTextCycles.setText(String.valueOf(currentValue - 1));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(editTextCycles.getText().toString());
                if (currentValue < 100) {
                    editTextCycles.setText(String.valueOf(currentValue + 1));
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the final value
                int finalValue = Integer.parseInt(editTextCycles.getText().toString());

                // Save the value in SharedPreferences
                SharedPreferences.Editor editor3 = sharedPreferences.edit();
                editor3.putInt("TriangleBreathingCounterValue", finalValue);
                editor3.apply();

                updateTriangleBreathingValue(finalValue);

                // Close the dialog
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog without saving
                dialog.dismiss();
            }
        });

        // Set rounded corner background for the dialog window
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_box);

        // Show the dialog
        dialog.show();
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}
