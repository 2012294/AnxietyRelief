package com.hareem.anxietyrelief.Patient_ui.home;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hareem.anxietyrelief.AnxietyLevel;
import com.hareem.anxietyrelief.Appointment;
import com.hareem.anxietyrelief.ChatActivity;
import com.hareem.anxietyrelief.PatientAPI;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.RetrofitClientInstance;
import com.hareem.anxietyrelief.databinding.FragmentPatientHomeBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HomeFragment extends Fragment {

    private FragmentPatientHomeBinding binding;
    private ProgressBar horizontalProgressBar;
    private TextView lowLabel, mildLabel, moderateLabel, highLabel,nosessionchat,nosessionvideo;
    TableLayout  tableContainerchat,tableContainervideo;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPatientHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("patient_username2", null);
        if (username != null) {
            binding.usernametxt.setText(username);
            Log.d("HomeFragment", "Retrieved username from SharedPreferences: " + username);
        } else {
            Log.d("HomeFragment", "Username retrieved from SharedPreferences is null.");
        }


        nosessionchat=binding.nosessionchat;
        nosessionvideo=binding.nosessionvideo;
        horizontalProgressBar = root.findViewById(R.id.horizontalProgressBar);
        lowLabel = root.findViewById(R.id.lowLabel);
        mildLabel = root.findViewById(R.id.mildLabel);
        moderateLabel = root.findViewById(R.id.moderateLabel);
        highLabel = root.findViewById(R.id.highLabel);
        tableContainerchat=root.findViewById(R.id.chatsessiontable);
        tableContainervideo=root.findViewById(R.id.videosessiontable);

        getLatestAnxietyLevel();
        getchatsession();
        getsessionvideo();
        CardView JournalCardview= root.findViewById(R.id.journalingcardview);
        JournalCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetoJournalFragment();
            }
        });
        CardView BreathingExerciseCardview= root.findViewById(R.id.BreathignExercisesCardView);
        BreathingExerciseCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetoBreathingExercisesFragment();
            }
        });
        CardView AffirmationCardView=root.findViewById(R.id.affirmationCardView);
        AffirmationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetoAffrimationFragment();
            }
        });
        CardView FindTherapistCardView=root.findViewById(R.id.Findtherapistcardview);
        FindTherapistCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetoFindTherapistFragment();
            }
        });
        TextView AnxietyLevelLink=root.findViewById(R.id.anxietylevelLink);
        AnxietyLevelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetoAnxietyLevelFragment();
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

    private void navigatetoAnxietyLevelFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_nav_home_to_nav_anxiety_level);
    }

    private void navigatetoAffrimationFragment() {

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_nav_home_to_nav_affirmation);
    }

    private void navigatetoBreathingExercisesFragment() {

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_nav_home_to_nav_breathingexercise);
    }

    private void navigatetoJournalFragment() {

        NavController navController = Navigation.findNavController(requireView());

        navController.navigate(R.id.action_nav_home_to_nav_journal);
    }

    private void navigatetoFindTherapistFragment() {

        NavController navController = Navigation.findNavController(requireView());

        navController.navigate(R.id.action_nav_home_to_nav_FindTherapist);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("FragmentLifecycle", "onDestroyView");
    }
    private void getLatestAnxietyLevel() {
        // Get the patientId from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        if (patientId != null) {
            // Create a Retrofit instance
            PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

            // Make the network request
            Call<List<AnxietyLevel>> call = patientAPI.getAnxietyLevels(patientId);
            call.enqueue(new Callback<List<AnxietyLevel>>() {

                @Override
                public void onResponse(Call<List<AnxietyLevel>> call, Response<List<AnxietyLevel>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        // Get the last anxiety level from the response
                        AnxietyLevel lastAnxietyLevel = response.body().get(response.body().size() - 1);
                        Log.d("level12", "on response anxiety level: " + lastAnxietyLevel.getLevel());

                        setAnxietyLevelInProgressBar(lastAnxietyLevel.getLevel());
                    } else {
                        Log.d("level12", "on response else");

                        // If there's no response, set the progress to 0
                        setAnxietyLevelInProgressBar("none");
                    }
                }

                @Override
                public void onFailure(Call<List<AnxietyLevel>> call, Throwable t) {
                    Log.e("level12", "Failed to get anxiety levels", t);

                    // If there's a failure, set the progress to 0
                    setAnxietyLevelInProgressBar("none");
                }
            });
        }
    }

    private void setAnxietyLevelInProgressBar(String anxietyLevel) {
        lowLabel.setTypeface(null, Typeface.NORMAL);
        mildLabel.setTypeface(null, Typeface.NORMAL);
        moderateLabel.setTypeface(null, Typeface.NORMAL);
        highLabel.setTypeface(null, Typeface.NORMAL);
        int progress = 0;

        switch (anxietyLevel.toLowerCase()) {
            case "low":
                progress = 1;
                lowLabel.setTypeface(null, Typeface.BOLD);
                mildLabel.setTypeface(null, Typeface.NORMAL);
                moderateLabel.setTypeface(null, Typeface.NORMAL);
                highLabel.setTypeface(null, Typeface.NORMAL);
                break;
            case "mild":
                progress = 2;
                lowLabel.setTypeface(null, Typeface.BOLD);
                mildLabel.setTypeface(null, Typeface.BOLD);
                moderateLabel.setTypeface(null, Typeface.NORMAL);
                highLabel.setTypeface(null, Typeface.NORMAL);
                break;
            case "moderate":
                progress = 3;
                lowLabel.setTypeface(null, Typeface.BOLD);
                mildLabel.setTypeface(null, Typeface.BOLD);
                moderateLabel.setTypeface(null, Typeface.BOLD);
                highLabel.setTypeface(null, Typeface.NORMAL);
                break;
            case "high":
                progress = 4;
                lowLabel.setTypeface(null, Typeface.BOLD);
                mildLabel.setTypeface(null, Typeface.BOLD);
                moderateLabel.setTypeface(null, Typeface.BOLD);
                highLabel.setTypeface(null, Typeface.BOLD);
                break;
        }

        horizontalProgressBar.setProgress(progress);


    }

    public void getchatsession(){
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();

        // Create PatientAPI instance
        PatientAPI   patientAPI = retrofit.create(PatientAPI.class);

        // Call API method to fetch appointments
        Call<List<Appointment>> call = patientAPI.getAppointmentsOfChat();
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    List<Appointment> appointments = response.body();
                    for (Appointment appointment : appointments) {
                        chattable(appointment);
                    }

                    if (appointments.isEmpty()){
                        nosessionchat.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                // Handle network errors
            }
        });
    }
    private void chattable(Appointment appointment) {
        // If no duplicate row exists, proceed to add the new row
        View rowView = getLayoutInflater().inflate(R.layout.upcommingsessionrow, null);

        TextView usernameTextView = rowView.findViewById(R.id.text_username);
        TextView dayTextView = rowView.findViewById(R.id.day);
        TextView timeTextView = rowView.findViewById(R.id.time);
        Button startButton = rowView.findViewById(R.id.button_start);




        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ChatActivity.class);
                i.putExtra("APPOINTMENT_OBJECT", appointment);
                i.putExtra("username", usernameTextView.getText());
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        usernameTextView.setText(appointment.getTherapistname());
        dayTextView.setText(appointment.getDay());
        timeTextView.setText(appointment.getTime());

        // Get current date and time
        Calendar currentTime = Calendar.getInstance();

        // Parse appointment date and time
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma", Locale.ENGLISH);
        try {
            Date appointmentDate = sdf.parse(appointment.getDay());
            Calendar appointmentDay = Calendar.getInstance();
            appointmentDay.setTime(appointmentDate);

            // Check if the appointment day has arrived
            if (currentTime.compareTo(appointmentDay) < 0) {
                // Disable start button if the appointment day hasn't arrived yet
                startButton.setEnabled(false);
                startButton.setBackgroundResource(R.drawable.grey_round_background);
                // Add the row to the table container
                tableContainerchat.addView(rowView);
                return; // Exit the method if the appointment day hasn't arrived yet
            }

            // Parse appointment time
            Date appointmentTime = timeFormat.parse(appointment.getTime());
            Calendar appointmentTimeCal = Calendar.getInstance();
            appointmentTimeCal.setTime(appointmentTime);

            // Set appointment time to appointment day
            appointmentDay.set(Calendar.HOUR_OF_DAY, appointmentTimeCal.get(Calendar.HOUR_OF_DAY));
            appointmentDay.set(Calendar.MINUTE, appointmentTimeCal.get(Calendar.MINUTE));

            // Check if the current time is within an hour following the appointment time
            Calendar appointmentEndTime = (Calendar) appointmentDay.clone();
            appointmentEndTime.add(Calendar.HOUR_OF_DAY, 1);

            // Check if current time is within the hour following the appointment time
            if (currentTime.compareTo(appointmentDay) >= 0 && currentTime.compareTo(appointmentEndTime) < 0) {
                // Enable start button if current time is within the hour following the appointment time
                startButton.setEnabled(true);

            } else {
                // Disable start button if current time is not within the hour following the appointment time
                startButton.setEnabled(false);

                startButton.setBackgroundResource(R.drawable.grey_round_background);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception
        }

        // Add the row to the table container
        tableContainerchat.addView(rowView);
    }

    public void getsessionvideo(){
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();

        // Create PatientAPI instance
        PatientAPI   patientAPI = retrofit.create(PatientAPI.class);

        // Call API method to fetch appointments
        Call<List<Appointment>> call = patientAPI.getAppointmentsOfVideo();
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    List<Appointment> appointments = response.body();
                    for (Appointment appointment : appointments) {
                        videotable(appointment);
                    }

                    if (appointments.isEmpty()){
                        nosessionvideo.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                // Handle network errors
            }
        });
    }
    private void videotable(Appointment appointment) {
        // If no duplicate row exists, proceed to add the new row
        View rowView = getLayoutInflater().inflate(R.layout.upcommingsessionrow, null);

        TextView usernameTextView = rowView.findViewById(R.id.text_username);
        TextView dayTextView = rowView.findViewById(R.id.day);
        TextView timeTextView = rowView.findViewById(R.id.time);
        Button startButton = rowView.findViewById(R.id.button_start);

        usernameTextView.setText(appointment.getTherapistname());
        dayTextView.setText(appointment.getDay());
        timeTextView.setText(appointment.getTime());

        // Get current date and time
        Calendar currentTime = Calendar.getInstance();

        // Parse appointment date and time
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma", Locale.ENGLISH);
        try {
            Date appointmentDate = sdf.parse(appointment.getDay());
            Calendar appointmentDay = Calendar.getInstance();
            appointmentDay.setTime(appointmentDate);

            // Check if the appointment day has arrived
            if (currentTime.compareTo(appointmentDay) < 0) {
                // Disable start button if the appointment day hasn't arrived yet
                startButton.setEnabled(false);
                startButton.setBackgroundResource(R.drawable.grey_round_background);
                // Add the row to the table container
                tableContainervideo.addView(rowView);
                return; // Exit the method if the appointment day hasn't arrived yet
            }

            // Parse appointment time
            Date appointmentTime = timeFormat.parse(appointment.getTime());
            Calendar appointmentTimeCal = Calendar.getInstance();
            appointmentTimeCal.setTime(appointmentTime);

            // Set appointment time to appointment day
            appointmentDay.set(Calendar.HOUR_OF_DAY, appointmentTimeCal.get(Calendar.HOUR_OF_DAY));
            appointmentDay.set(Calendar.MINUTE, appointmentTimeCal.get(Calendar.MINUTE));

            // Check if the current time is within an hour following the appointment time
            Calendar appointmentEndTime = (Calendar) appointmentDay.clone();
            appointmentEndTime.add(Calendar.HOUR_OF_DAY, 1);

            // Check if current time is within the hour following the appointment time
            if (currentTime.compareTo(appointmentDay) >= 0 && currentTime.compareTo(appointmentEndTime) < 0) {
                // Enable start button if current time is within the hour following the appointment time
                startButton.setEnabled(true);
            } else {
                // Disable start button if current time is not within the hour following the appointment time
                startButton.setEnabled(false);
                startButton.setBackgroundResource(R.drawable.grey_round_background);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception
        }

        // Add the row to the table container
        tableContainervideo.addView(rowView);
    }





    // Method to check if appointment time is in the future
    private boolean isAppointmentInFuture(int appointmentHour, int appointmentMinute, Calendar currentTime) {
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        if (appointmentHour > currentHour || (appointmentHour == currentHour && appointmentMinute > currentMinute)) {
            return true; // Appointment time is in the future
        }
        return false; // Appointment time is in the past or present
    }



}




