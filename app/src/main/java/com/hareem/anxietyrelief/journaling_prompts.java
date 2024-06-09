// journaling_prompts.java
package com.hareem.anxietyrelief;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class journaling_prompts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JournalingPromptAdapter adapter;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journaling_prompts);

        recyclerView = findViewById(R.id.promptsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JournalingPromptAdapter();
        recyclerView.setAdapter(adapter);

        // Get the reference to the search EditText
        searchEditText = findViewById(R.id.searchEditText);

        // Set a TextWatcher to listen for text changes
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call the filter method when text changes
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });

        // Execute the AsyncTask to make the API call
        new GetAvailablePromptsTask().execute();
    }

    private class GetAvailablePromptsTask extends AsyncTask<Void, Void, List<JournalingPrompt>> {

        @Override
        protected List<JournalingPrompt> doInBackground(Void... voids) {
            // Fetch available prompts for the patient
            PromptsAPI apiService = RetrofitClientInstance.getRetrofitInstance().create(PromptsAPI.class);

            SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
            String patient_id = sharedPreferences.getString("currentPatientId", null);

            Call<List<JournalingPrompt>> call = apiService.getAvailablePrompts(patient_id);

            try {
                Response<List<JournalingPrompt>> response = call.execute();
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
        protected void onPostExecute(List<JournalingPrompt> prompts) {
            // Update UI with the list of prompts
            if (prompts != null) {
                // Ensure UI updates are performed on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setPromptList(prompts);
                    }
                });
            } else {
                // Handle null or error case
            }
        }
    }

    class JournalingPromptAdapter extends RecyclerView.Adapter<JournalingPromptAdapter.PromptViewHolder> {

        private List<JournalingPrompt> promptList;
        private List<JournalingPrompt> originalPromptList;

        public void setPromptList(List<JournalingPrompt> promptList) {
            this.promptList = promptList;
            this.originalPromptList = new ArrayList<>(promptList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PromptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prompt, parent, false);
            return new PromptViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PromptViewHolder holder, int position) {
            JournalingPrompt prompt = promptList.get(position);
            holder.promptText.setText(prompt.getPrompt());

            // Add click listener to CardView
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences preferences = view.getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("selectedPrompt", prompt.getPrompt());
                    editor.apply();

                    // Handle card view click, e.g., navigate to Journal_Entry activity
                    Intent intent = new Intent(view.getContext(), Journal_Entry.class);
                    // You can also pass data to the Journal_Entry activity if needed
                    // intent.putExtra("promptText", prompt.getPrompt());
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return promptList != null ? promptList.size() : 0;
        }

        public class PromptViewHolder extends RecyclerView.ViewHolder {
            TextView promptText;

            public PromptViewHolder(@NonNull View itemView) {
                super(itemView);
                promptText = itemView.findViewById(R.id.promptText);
            }
        }

        public void filter(String query) {
            promptList.clear();
            if (query.isEmpty()) {
                promptList.addAll(originalPromptList);
            } else {
                query = query.toLowerCase();
                for (JournalingPrompt prompt : originalPromptList) {
                    if (prompt.getPrompt().toLowerCase().contains(query)) {
                        promptList.add(prompt);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Execute the AsyncTask to make the API call when the activity is resumed
        new GetAvailablePromptsTask().execute();
    }
}
