package com.hareem.anxietyrelief.ui.home;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hareem.anxietyrelief.Appointment;
import com.hareem.anxietyrelief.Chat_therapist_activity;
import com.hareem.anxietyrelief.PatientAPI;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.RetrofitClientInstance;
import com.hareem.anxietyrelief.TherapistAPI;
import com.hareem.anxietyrelief.databinding.FragmentHomeBinding;

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

    private FragmentHomeBinding binding;
    TableLayout tableContainerchat,tableContainervideo;
    private TextView nosessionchat,nosessionvideo;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tableContainerchat=root.findViewById(R.id.chatsessiontable);

        tableContainervideo=root.findViewById(R.id.videosessiontable);
        nosessionchat=root.findViewById(R.id.nosessionchat);
        nosessionvideo=root.findViewById(R.id.nosessionvideo);
        getchatsession();
        getsessionvideo();








        return root;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void getchatsession(){
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();

        // Create PatientAPI instance
        PatientAPI patientAPI = retrofit.create(PatientAPI.class);

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
        View rowView = getLayoutInflater().inflate(R.layout.upcommingtherapistrow, null);

        TextView usernameTextView = rowView.findViewById(R.id.text_username);
        TextView dayTextView = rowView.findViewById(R.id.day);
        TextView timeTextView = rowView.findViewById(R.id.time);
        Button startButton = rowView.findViewById(R.id.button_start);
        Button cancelButton = rowView.findViewById(R.id.button_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure you want to cancel the session?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ViewGroup parentView = (ViewGroup) rowView.getParent();
                                if (parentView != null) {
                                    parentView.removeView(rowView);
                                }
                                cancelButton.setOnClickListener(null);
                                String sessionID = appointment.get_id();
                                deleteAppointment(sessionID);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked No button
                                // Dismiss the dialog
                                dialog.dismiss();
                            }
                        });
                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), Chat_therapist_activity.class);
                i.putExtra("APPOINTMENT_OBJECT", appointment);
                i.putExtra("username", usernameTextView.getText());
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        usernameTextView.setText(appointment.getPatientname());
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
                cancelButton.setEnabled(false);
                cancelButton.setBackgroundResource(R.drawable.grey_round_background);
            } else {
                // Disable start button if current time is not within the hour following the appointment time
               startButton.setEnabled(false);
               cancelButton.setEnabled(true);
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
        View rowView = getLayoutInflater().inflate(R.layout.upcommingtherapistrow, null);

        TextView usernameTextView = rowView.findViewById(R.id.text_username);
        TextView dayTextView = rowView.findViewById(R.id.day);
        TextView timeTextView = rowView.findViewById(R.id.time);
        Button startButton = rowView.findViewById(R.id.button_start);
        Button cancelButton=rowView.findViewById(R.id.button_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure you want to cancel the session?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ViewGroup parentView = (ViewGroup) rowView.getParent();
                                if (parentView != null) {
                                    parentView.removeView(rowView);
                                }

                                cancelButton.setOnClickListener(null);
                                String sessionID= appointment.get_id();
                                deleteAppointment(sessionID);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked No button
                                // Dismiss the dialog
                                dialog.dismiss();
                            }
                        });
                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });




        usernameTextView.setText(appointment.getPatientname());
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
                cancelButton.setEnabled(false);
                cancelButton.setBackgroundResource(R.drawable.grey_round_background);
            } else {
                // Disable start button if current time is not within the hour following the appointment time
                startButton.setEnabled(false);
                cancelButton.setEnabled(true);
                startButton.setBackgroundResource(R.drawable.grey_round_background);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing exception
        }

        // Add the row to the table container
        tableContainervideo.addView(rowView);
    }


    public void deleteAppointment(String appointmentId) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();

        // Create TherapistAPI instance
        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);

        // Make DELETE request
        Call<Void> call = therapistAPI.deleteAppointment(appointmentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful deletion
                    // For example, show a toast message
                    Toast.makeText(requireContext(), "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful deletion
                    // For example, show an error message
                    Toast.makeText(requireContext(), "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                // For example, show an error message
                Toast.makeText(requireContext(), "Failed to delete appointment: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    } }