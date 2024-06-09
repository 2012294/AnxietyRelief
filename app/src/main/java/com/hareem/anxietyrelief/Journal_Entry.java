package com.hareem.anxietyrelief;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Journal_Entry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry);




        TextView dayTextView = findViewById(R.id.dayTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);


        String currentDay = getCurrentDay();
        String currentDate = getCurrentDate();
        String currentTime = getCurrentTime();

        dayTextView.setText(currentDay);
        dateTextView.setText(currentDate);
        timeTextView.setText(currentTime);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String selectedPrompt = preferences.getString("selectedPrompt", "");

        // Display the selected prompt below the time
        TextView selectedPromptTextView = findViewById(R.id.selectedPromptTextView);
        selectedPromptTextView.setText(selectedPrompt);


        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveJournalEntry();

            }
        });
    }

    private void saveJournalEntry() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patient_id = sharedPreferences.getString("currentPatientId", null);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String selectedPrompt = preferences.getString("selectedPrompt", "");

        String day = getCurrentDay();
        String date = getCurrentDate();
        String time = getCurrentTime();
        String entryText = ((EditText) findViewById(R.id.userEntryEditText)).getText().toString();

        JournalEntryModel journalEntry = new JournalEntryModel();
        journalEntry.setPatient_id(patient_id);
        journalEntry.setPrompt(selectedPrompt);
        journalEntry.setDay(day);
        journalEntry.setDate(date);
        journalEntry.setTime(time);
        journalEntry.setEntryText(entryText);

        JournalEntryAPI apiService = RetrofitClientInstance.getRetrofitInstance().create(JournalEntryAPI.class);
        Call<Void> call = apiService.saveJournalEntry(journalEntry);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Journal_Entry.this, "Journal entry saved successfully", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(Journal_Entry.this, "Failed to save journal entry", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Journal_Entry.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }


}
