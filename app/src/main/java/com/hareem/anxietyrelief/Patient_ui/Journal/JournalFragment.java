package com.hareem.anxietyrelief.Patient_ui.Journal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hareem.anxietyrelief.Adapter.JournalEntryAdapter;
import com.hareem.anxietyrelief.JournalEntryAPI;
import com.hareem.anxietyrelief.JournalEntryModel;
import com.hareem.anxietyrelief.Patient_ui.Affirmation.AffirmationFragment;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.RetrofitClientInstance;
import com.hareem.anxietyrelief.ViewJournalEntry;
import com.hareem.anxietyrelief.databinding.FragmentJournalBinding;
import com.hareem.anxietyrelief.journaling_prompts;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalFragment extends Fragment implements JournalEntryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private JournalEntryAdapter adapter;
    private FragmentJournalBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentJournalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.journalEntriesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        adapter = new JournalEntryAdapter();
        recyclerView.setAdapter(adapter);

        Button newEntryButton = root.findViewById(R.id.newentrybtn);
        newEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), journaling_prompts.class);
                startActivity(intent);
            }
        });

        EditText searchEditText = root.findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = sharedPreferences.getString("currentPatientId", null);

        new GetJournalEntriesTask().execute(patientId);

        // Set the item click listener for deleting entries
        adapter.setOnItemClickListener(this);

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
    public void onItemClick(int position) {
        // Get the clicked entry from the adapter
        JournalEntryModel clickedEntry = adapter.getItem(position);

        // Save the entry ID in SharedPreferences
        saveEntryIdToSharedPreferences(clickedEntry.getId());

        // Start the ViewJournalEntry activity, passing the entry details as extras
        Intent intent = new Intent(requireContext(), ViewJournalEntry.class);
        intent.putExtra("day", clickedEntry.getDay());
        intent.putExtra("date", clickedEntry.getDate());
        intent.putExtra("time", clickedEntry.getTime());
        intent.putExtra("prompt", clickedEntry.getPrompt());
        intent.putExtra("entryText", clickedEntry.getEntryText());
        intent.putExtra("entryId", clickedEntry.getId());
        startActivity(intent);
    }

    private void saveEntryIdToSharedPreferences(String entryId) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("JournalEntryPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentEntryId", entryId);
        editor.apply();
    }

    @Override
    public void onDeleteClick(int position) {
        // Handle the click event when the delete button is clicked for an entry
        showDeleteConfirmationDialog(position);
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        // Get the entry ID from the adapter
                        String entryId = adapter.getItem(position).getId();

                        // Call the API to delete the entry
                        deleteJournalEntry(entryId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button
                        // Do nothing, close the dialog
                    }
                });

        // Create the AlertDialog object and show it
        builder.create().show();
    }

    private class GetJournalEntriesTask extends AsyncTask<String, Void, List<JournalEntryModel>> {

        @Override
        protected List<JournalEntryModel> doInBackground(String... patientIds) {
            JournalEntryAPI apiService = RetrofitClientInstance.getRetrofitInstance().create(JournalEntryAPI.class);
            Call<List<JournalEntryModel>> call = apiService.getJournalEntries(patientIds[0]);

            try {
                Response<List<JournalEntryModel>> response = call.execute();
                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    // Handle error
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<JournalEntryModel> entries) {
            if (entries != null) {
                adapter.setJournalEntryList(entries);
            } else {
                // Handle null or error case
            }
        }
    }

    private void deleteJournalEntry(String entryId) {
        JournalEntryAPI apiService = RetrofitClientInstance.getRetrofitInstance().create(JournalEntryAPI.class);
        Call<Void> call = apiService.deleteJournalEntry(entryId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeEntry(entryId);
                    showToast("Entry deleted successfully");
                } else {
                    showToast("Failed to delete entry");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Failed to delete entry");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onResume() {
        super.onResume();

        // Retrieve the patient ID from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = sharedPreferences.getString("currentPatientId", null);

        // Execute the AsyncTask to fetch journal entries
        new GetJournalEntriesTask().execute(patientId);
    }
}
