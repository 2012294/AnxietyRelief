package com.hareem.anxietyrelief;

import static okio.ByteString.decodeBase64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hareem.anxietyrelief.Adapter.AvailabilityAdapter;
import com.hareem.anxietyrelief.Adapter.CalendarAdapter;

import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAppointment extends AppCompatActivity {

    private CardView videoSessionCardView, chatSessionCardView;
    private TextView textViewVideoSession, textViewChatSession, availabilityTextView;

    TextInputLayout SessionTypeInputLayout, DateInputLayout, TimeSlotInputLayout;

    private boolean isSessionTypeSelected = false;

    private boolean isCalendarSelected = false;

    private boolean isTimeSlotSelected = false;

    public void setIsCalendarSelected(boolean value) {
        isCalendarSelected = value;
    }

    public void setIsTimeSlotSelected(boolean value) {
        isTimeSlotSelected = value;
    }
    public void setDateInputLayout(String error) {
        DateInputLayout.setError(error);
    }

    public void setTimeSlotInputLayout(String error) {
        TimeSlotInputLayout.setError(error);
    }


    Button next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        SharedPreferences ViewTherapistsharedPreferences = getSharedPreferences("ViewTherapistPreferences", Context.MODE_PRIVATE);
        String therapistId = ViewTherapistsharedPreferences.getString("ViewProfiletherapist_id", "");


        ObjectId objectId = new ObjectId(therapistId);
        fetchTherapistData(objectId);

        next= findViewById(R.id.bookAppointmentButton);

        SessionTypeInputLayout=findViewById(R.id.SessionTypeInputLayout);
        DateInputLayout=findViewById(R.id.CalendarInputLayout);
        TimeSlotInputLayout=findViewById(R.id.TimeSlotInputLayout);




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSessionTypeSelected) { // Check if a session type is not selected
                    SessionTypeInputLayout.setError("Please select a session type");
                    SessionTypeInputLayout.setErrorIconDrawable(null);
                }else if (!isCalendarSelected) { // Check if a session type is not selected
                    DateInputLayout.setError("Please select a day");
                    DateInputLayout.setErrorIconDrawable(null);
                }else if (!isTimeSlotSelected) { // Check if a session type is not selected
                    TimeSlotInputLayout.setError("Please select a time slot");
                    TimeSlotInputLayout.setErrorIconDrawable(null);
                } else {
                    SessionTypeInputLayout.setError("");
                    DateInputLayout.setError("");
                    TimeSlotInputLayout.setError("");
                    // Proceed to payment activity
                    Intent i = new Intent(BookAppointment.this, PaymentActivity.class);
                    startActivity(i);
                }
            }
        });


        videoSessionCardView = findViewById(R.id.VideoSessionCardView);
        chatSessionCardView = findViewById(R.id.ChatSessionCardView);
        textViewVideoSession = findViewById(R.id.textView_videosession);
        textViewChatSession = findViewById(R.id.textView_chatsession);
        TableLayout availabilityTableLayout = findViewById(R.id.availabilityTableLayout);





        // Populate therapist username
        SharedPreferences sharedPreferences = getSharedPreferences("TherapistUsername", Context.MODE_PRIVATE);
        String therapistUsername = sharedPreferences.getString("TherapistUsername", "");
        TextView therapistUsernameTextView = findViewById(R.id.textView_therapist_username);
        therapistUsernameTextView.setText("Book an Appointment with " + therapistUsername);

        videoSessionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCardView(videoSessionCardView, textViewVideoSession);
                String VideoSession = "Video Session";
                SharedPreferences.Editor editor = getSharedPreferences("SessionType", Context.MODE_PRIVATE).edit();
                editor.putString("SessionType", VideoSession);
                editor.apply();
                isSessionTypeSelected = true;
                SessionTypeInputLayout.setError("");




            }
        });

        chatSessionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCardView(chatSessionCardView, textViewChatSession);
                String ChatSession = "Chat Session";
                SharedPreferences.Editor editor = getSharedPreferences("SessionType", Context.MODE_PRIVATE).edit();
                editor.putString("SessionType", ChatSession);
                editor.apply();
                isSessionTypeSelected = true;
                SessionTypeInputLayout.setError("");

            }
        });


    }

    private void selectCardView(CardView cardView, TextView textView) {
        // Reset the background of all CardViews and set text color to default
        videoSessionCardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        chatSessionCardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        textViewVideoSession.setTextColor(getResources().getColor(R.color.verdigris));
        textViewChatSession.setTextColor(getResources().getColor(R.color.verdigris));


        // Set background color for the selected CardView and change text color
        cardView.setCardBackgroundColor(getResources().getColor(R.color.verdigris));
        textView.setTextColor(getResources().getColor(android.R.color.white));

    }

    private void fetchTherapistData(ObjectId therapistId) {
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);
        Call<Therapist> call = patientAPI.getTherapist(therapistId);
        call.enqueue(new Callback<Therapist>() {
            @Override
            public void onResponse(Call<Therapist> call, Response<Therapist> response) {
                if (response.isSuccessful()) {
                    Therapist therapist = response.body();
                    // Update UI with therapist data
                    if (therapist != null) {
                        List<String> availableTime = therapist.getAvailableTime();
                        if (availableTime != null && !availableTime.isEmpty()) {
                            // Extract days from availability slots
                            List<String> daysList = extractDaysFromAvailability(availableTime);

                            ;

                            // Set the list of days as text for TextView
//                            TextView therapistUsernameTextView = findViewById(R.id.textView_therapist_username);
//                            therapistUsernameTextView.setText("Available Days: " + TextUtils.join("", daysList));


                            TableLayout availabilityTableLayout = findViewById(R.id.availabilityTableLayout);
                            CalendarAdapter calendaradapter = new CalendarAdapter(BookAppointment.this,daysList,availabilityTableLayout,BookAppointment.this);
                            GridView calendarGridView = findViewById(R.id.calendarGridView);
                            calendarGridView.setAdapter(calendaradapter);

                            TextView monthTextView = findViewById(R.id.monthTextView);
                            String selectedDay = calendaradapter.getSelectedDay();
                            monthTextView.setText(calendaradapter.getCurrentMonth());

                            // Create and set adapter for availability grid view
                             calendaradapter.setAvailableTime(availableTime);










                        } else {
                            Toast.makeText(BookAppointment.this, "No availability information available", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(BookAppointment.this, "Failed to fetch therapist data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Therapist> call, Throwable t) {
                Toast.makeText(BookAppointment.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private List<String> extractDaysFromAvailability(List<String> availableTime) {
        List<String> daysList = new ArrayList<>();
        // Loop through each availability slot
        for (String availability : availableTime) {
            // Split the availability slot by comma
            String[] parts = availability.split(", ");
            // Loop through the parts to find the one containing the day
            for (String part : parts) {
                if (part.startsWith("Day:")) {
                    // Extract the day and add it to the list
                    String day = part.substring(part.indexOf(":") + 2); // Extract day value after ": "
                    daysList.add(day.trim()); // Trim to remove extra spaces
                    break; // Move to the next availability slot
                }
            }
        }
        return daysList;
    }}





