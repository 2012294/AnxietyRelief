package com.hareem.anxietyrelief;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hareem.anxietyrelief.Appointment;
import com.hareem.anxietyrelief.R;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VideoSession_Patient extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private String sessionTime, TransactionID, userType;
    private ZegoUIKitPrebuiltVideoConferenceFragment videoConferenceFragment;
    private TextView overlayTextView; // Declare the TextView here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_session_patient);

        overlayTextView = findViewById(R.id.overlay_text_view); // Initialize the TextView

        addFragment();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("APPOINTMENT_OBJECT")) {
            Appointment appointment = (Appointment) intent.getSerializableExtra("APPOINTMENT_OBJECT");
            sessionTime = appointment.getTime();
            TransactionID = appointment.getTransactionId();
            userType = intent.getStringExtra("usertype");
        }

        updateCountdown(sessionTime);
    }



    public void addFragment() {
        long appID = 795383913;
        String appSign = "852d06ce7a7b522fa18fe8557c8f7ea34ffca9f66f5f6a563394490d06344fd0";

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("APPOINTMENT_OBJECT")) {
            Appointment appointment = (Appointment) intent.getSerializableExtra("APPOINTMENT_OBJECT");
            String session_id = appointment.get_id();
            String patient_id = appointment.getPatientId();
            String therapist_id = appointment.getTherapistId();
            String patient_name = appointment.getPatientname();
            String therapist_name = appointment.getTherapistname(); // Corrected variable name
            String userType = intent.getStringExtra("usertype");

            if ("patient".equals(userType)) { // Corrected comparison
                String conferenceID = session_id;
                String userID = patient_id;
                String userName = patient_name;
                ZegoUIKitPrebuiltVideoConferenceConfig config = new ZegoUIKitPrebuiltVideoConferenceConfig();
                videoConferenceFragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(appID, appSign, userID, userName, conferenceID, config);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, videoConferenceFragment)
                        .commitNow();

            } else if ("therapist".equals(userType)) { // Corrected comparison
                String conferenceID = session_id;
                String userID = therapist_id;
                String userName = therapist_name;
                ZegoUIKitPrebuiltVideoConferenceConfig config = new ZegoUIKitPrebuiltVideoConferenceConfig();
                videoConferenceFragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(appID, appSign, userID, userName, conferenceID, config);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, videoConferenceFragment)
                        .commitNow();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Cancel the timer if it's running
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // Remove the video conference fragment when the activity is paused
        if (videoConferenceFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(videoConferenceFragment).commitAllowingStateLoss();
            videoConferenceFragment = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release any resources or connections related to the video session
        // This could include cleaning up ZegoVideo SDK resources if necessary
        // Here you might want to also ensure the timer is cancelled if it hasn't already been in onPause()
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-add the video conference fragment when the activity is resumed, if it was removed in onPause()
        if (videoConferenceFragment == null) {
            addFragment();
        }
        // Restart the countdown timer if it wasn't running in onPause()
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }


    private void updateCountdown(String startTime) {
        try {
            // Get current time
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY); // 24-hour format
            int currentMinute = currentTime.get(Calendar.MINUTE);

            // Parse the start time
            int startHour;
            int startMinute = 0; // Assuming minutes are always 0
            boolean isAM;
            if (startTime.endsWith("am")) {
                isAM = true;
            } else {
                isAM = false;
            }
            // Remove "am" or "pm" from the start time
            startTime = startTime.replace("am", "").replace("pm", "").trim();
            if (startTime.equals("12")) {
                // If start hour is 12, adjust it to 0 for 24-hour format
                startHour = 0;
            } else {
                startHour = Integer.parseInt(startTime);
            }

            // Adjust start hour for PM times
            if (!isAM && startHour != 12) {
                startHour += 12;
            }

            // Set current time to the start time
            Calendar startTimeToday = Calendar.getInstance();
            startTimeToday.set(Calendar.HOUR_OF_DAY, startHour);
            startTimeToday.set(Calendar.MINUTE, startMinute);
            startTimeToday.set(Calendar.SECOND, 0);

            // Calculate end time (1 hour after start time)
            Calendar endTime = (Calendar) startTimeToday.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 1);

            // Check if the current time is between the start and end times
            if (currentTime.after(startTimeToday) && currentTime.before(endTime)) {
                // Calculate remaining time until the end time
                long remainingMilliseconds = endTime.getTimeInMillis() - currentTime.getTimeInMillis();

                countDownTimer = new CountDownTimer(remainingMilliseconds, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // Update UI with remaining time
                        long seconds = millisUntilFinished / 1000;
                        long minutes = seconds / 60;
                        seconds = seconds % 60;
                        long hours = minutes / 60;
                        minutes = minutes % 60;
                        String remainingTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                        runOnUiThread(() -> setupOverlayTextView(remainingTime)); // Ensure UI update runs on UI thread
                    }

                    public void onFinish() {
                        runOnUiThread(() -> setupOverlayTextView("Session Ended"));

                        if ("therapist".equals(userType)) {
                            completeTransaction();
                            endCallAndShowDialog();
                        }else{
                            endCallAndShowDialog1();
                        }
                    }
                }.start();
            } else {
                // Handle case where current time is not within the session time range
                runOnUiThread(() -> setupOverlayTextView("Session Not Started"));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void endCallAndShowDialog() {
        // Remove the video conference fragment to end the call
        if (videoConferenceFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(videoConferenceFragment).commitAllowingStateLoss();
            videoConferenceFragment = null;
        }

        // Show a dialog informing the user that the session has ended
        showSessionEndedDialog();
    }

    private void showSessionEndedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Session Ended");
        builder.setMessage("Your session has ended.");
        builder.setCancelable(false); // Prevent dialog from being dismissed by back button

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(VideoSession_Patient.this, PatientNavigationDrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // Prevent dialog from being dismissed by touching outside

        // Show the dialog
        dialog.show();
    }
    private void endCallAndShowDialog1() {
        // Remove the video conference fragment to end the call
        if (videoConferenceFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(videoConferenceFragment).commitAllowingStateLoss();
            videoConferenceFragment = null;
        }

        // Show a dialog informing the user that the session has ended
        showSessionEndedDialog1();
    }

    private void showSessionEndedDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Session Ended");
        builder.setMessage("Your session has ended. Your Payment is released");
        builder.setCancelable(false); // Prevent dialog from being dismissed by back button

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(VideoSession_Patient.this, therapist_navigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // Prevent dialog from being dismissed by touching outside

        // Show the dialog
        dialog.show();
    }

    private void setupOverlayTextView(String text) {
        overlayTextView.setText(text);
        overlayTextView.setVisibility(View.VISIBLE); // Set visibility as needed
    }

    private void completeTransaction() {
        // Create the Retrofit instance and the API service
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("transactionId", TransactionID);

        // Call the completeTransaction method
        Call<ResponseBody> call = therapistAPI.completeTransaction(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle the success response
                    Log.e("completeTransaction", "Transaction status updated successfully");
                } else {
                    Log.e("completeTransaction", "Error updating transaction status");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle the failure
                Log.e("completeTransaction", "Error updating transaction status", t);
            }
        });
    }
}
