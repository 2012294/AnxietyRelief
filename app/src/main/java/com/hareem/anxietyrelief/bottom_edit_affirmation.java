package com.hareem.anxietyrelief;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;


import android.view.Window;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class bottom_edit_affirmation extends BottomSheetDialogFragment {
    private TextInputLayout quoteTextInputLayout;
    private EditText quoteEditText;
    DatabaseReference affirmationsRef;
    FirebaseAuth mAuth ;

      private  String quotetxt;
      private  String key;

    public bottom_edit_affirmation( String quotetxt, String key) {

        this.quotetxt = quotetxt;
        this.key = key;
    }

    public String getQuotetxt() {
        return quotetxt;
    }

    public void setQuotetxt(String quotetxt) {
        this.quotetxt = quotetxt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_edit_affirmation, container, false);

        Button doneButton = view.findViewById(R.id.btndone);
        quoteTextInputLayout = view.findViewById(R.id.quoteTextInputLayout);
        quoteEditText = view.findViewById(R.id.quoteEditText);
        quoteEditText.setText(getQuotetxt());
        mAuth = FirebaseAuth.getInstance();

        affirmationsRef = FirebaseDatabase.getInstance().getReference("Affirmations");

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quote_ = quoteEditText.getText().toString();
                if (TextUtils.isEmpty(quote_)) {
                    quoteTextInputLayout.setError("please add affirmation");
                    quoteTextInputLayout.setErrorIconDrawable(null);
                }else{

                    SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    String patientId = preferences.getString("currentPatientId", "");

                    updateAffirmationInDB(patientId, quote_, getKey());
                    if (getActivity() instanceof Add_Affirmation_Activity) {
                        ((Add_Affirmation_Activity) getActivity()).updateAffirmations(quote_,patientId,getKey());
                    }
                    dismiss();}
            }
        });
        quoteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing

                quoteTextInputLayout.setError(null);
                quoteTextInputLayout.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onPause() {
        super.onPause();
        adjustSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void adjustSoftInputMode(int mode) {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.softInputMode = mode;
            window.setAttributes(layoutParams);
        }
    }
    private void updateAffirmationInDB(String patientId, String newQuote, String affirmationId) {
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

        // Create a JSONObject containing patientId, affirmationId, and newQuote
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("patientId", patientId);
            requestBodyJson.put("affirmationId", affirmationId);
            requestBodyJson.put("newQuote", newQuote);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a RequestBody from the JSONObject
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString());

        Call<JsonObject> call = patientAPI.updateAffirmation(requestBody);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Handle the response
                if (response.isSuccessful()) {
                    JsonObject result = response.body();
                    if (result != null && result.has("success") && result.get("success").getAsBoolean()) {
                        // Affirmation updated successfully
                        // Handle any UI updates or other actions
                    } else {
                        // Handle unsuccessful response
                        // You might want to show an error message
                    }
                } else {
                    // Handle unsuccessful response
                    // You might want to show an error message
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle failure
                // You might want to show an error message
            }
        });
    }





}



