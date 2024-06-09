package com.hareem.anxietyrelief;

import static com.hareem.anxietyrelief.R.id.Arrow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.bson.types.ObjectId;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Patient_change_password extends AppCompatActivity {
    String passwordRegx = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$";
    int password_error_len;
    int confirmPassword_error_len;
    ProgressBar progressBar;
    TextInputLayout confirmPasswordInputLayout;
    TextInputEditText confirmPasswordEditText;
    TextInputLayout  passwordInputLayout;
    TextInputEditText  passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_change_password);
        ImageView Arrow = findViewById(R.id.Arrow);
        confirmPasswordInputLayout = findViewById(R.id.confirmpasswordTextInputLayout);
       confirmPasswordEditText = findViewById(R.id.confirmpasswordEditText);
     passwordInputLayout = findViewById(R.id.newpasswordTextInputLayout);
     passwordEditText = findViewById(R.id.newpasswordEditText);
      progressBar=findViewById(R.id.resetProgressBar);
        Button reset=findViewById(R.id.btnreset);

        Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    passwordInputLayout.setError("New Password is Required");
                    passwordInputLayout.setErrorIconDrawable(null);
                } else if (!password.matches(passwordRegx)) {
                    passwordInputLayout.setError("Password is invalid. It must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.");
                    passwordInputLayout.setErrorIconDrawable(null);
                    password_error_len = password.length();
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    confirmPasswordInputLayout.setError("Confirm Password is Required");
                    confirmPasswordInputLayout.setErrorIconDrawable(null);
                } else if (!confirmPassword.matches(password)) {
                    confirmPasswordInputLayout.setError("Confirm Password not match Password field");
                    confirmPasswordInputLayout.setErrorIconDrawable(null);
                    confirmPassword_error_len = confirmPassword.length();
                }else {

                    resetPassword( password,view);
                }
            }
        });
         OnTypeErrorRemoval();

    }

    private void resetPassword( String password, View view) {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        if (patientId != null) {
            PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

            patientAPI.updatePasswordPatient(new Patient(null, password, null, patientId)).enqueue(new Callback<RegistrationResponse>() {
                @Override
                public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(Patient_change_password.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        if (response.code() == 422) {

                            Toast.makeText(Patient_change_password.this, "New password matches the old password", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(Patient_change_password.this, "Failed to Update password", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                    Toast.makeText(Patient_change_password.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

        }

    private void OnTypeErrorRemoval() {
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                if (s.length() != password_error_len) {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                if (s.length() != confirmPassword_error_len) {
                    confirmPasswordInputLayout.setError(null);
                    confirmPasswordInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
      
    }
}