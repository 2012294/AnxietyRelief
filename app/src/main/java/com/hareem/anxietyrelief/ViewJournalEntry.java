package com.hareem.anxietyrelief;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hareem.anxietyrelief.Patient_ui.Journal.JournalFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewJournalEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journal_entry);

        // Retrieve the passed values from the intent
        Intent intent = getIntent();
        String day = intent.getStringExtra("day");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String prompt = intent.getStringExtra("prompt");
        String entryText = intent.getStringExtra("entryText");

        // Update your UI elements with the retrieved values
        TextView dayText = findViewById(R.id.dayText);
        TextView dateText = findViewById(R.id.dateText);
        TextView timeText = findViewById(R.id.timeText);
        TextView selectedPromptTextView = findViewById(R.id.selectedPromptTextView);
        EditText userEntryEditText = findViewById(R.id.JournalEntrydispaly);

        dayText.setText(day);
        dateText.setText(date);
        timeText.setText(time);
        selectedPromptTextView.setText(prompt);
        userEntryEditText.setText(entryText);

        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateJournalEntry();
            }
        });
    }

    private void updateJournalEntry() {
        // Retrieve the edited entry text
        EditText userEntryEditText = findViewById(R.id.JournalEntrydispaly);
        String updatedEntryText = userEntryEditText.getText().toString();

        // Retrieve the entry ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("JournalEntryPreferences", Context.MODE_PRIVATE);
        String entryId = sharedPreferences.getString("currentEntryId", null);

        // Create an updated entry model
        JournalEntryModel updatedEntry = new JournalEntryModel();
        updatedEntry.setEntryText(updatedEntryText);

        // Call the API to update the entry
        JournalEntryAPI apiService = RetrofitClientInstance.getRetrofitInstance().create(JournalEntryAPI.class);
        Call<Void> call = apiService.updateJournalEntry(entryId, updatedEntry);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    showToast("Entry updated successfully");
                    finish();

                } else {
                    showToast("Failed to update entry");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Failed to update entry");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
