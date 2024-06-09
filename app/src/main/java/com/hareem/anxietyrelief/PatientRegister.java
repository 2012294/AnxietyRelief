package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientRegister extends AppCompatActivity {

    TextView linktextview;
    int password_error_len;
    int confirmPassword_error_len;
    String usernameRegx = "^[a-zA-Z][a-zA-Z0-9]{5,}$";
    String emailRegx = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    String passwordRegx = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$";
    private PatientAPI patientAPI;
    TextInputEditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    TextInputLayout usernameInputLayout, emailInputLayout, passwordInputLayout, confirmPasswordInputLayout;
    AppCompatButton RegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        linktextview = findViewById(R.id.linkTextView);
        passwordInputLayout = findViewById(R.id.passwordTextInputLayout);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordInputLayout = findViewById(R.id.confirmpasswordTextInputLayout);
        confirmPasswordEditText = findViewById(R.id.confirmpasswordEditText);
        usernameInputLayout = findViewById(R.id.usernameTextInputLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailInputLayout = findViewById(R.id.emailTextInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        RegisterBtn = findViewById(R.id.registerbtn1);
        ProgressBar progressBar = findViewById(R.id.registrationProgressBar);
        patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

        String text = "Already have an account? Login";
        SpannableString ss = new SpannableString(text);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(styleSpan, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linktextview.setText(ss);
        linktextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PatientRegister.this, PatientLogin.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        OnTypeErrorRemoval();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            Intent i = new Intent(PatientRegister.this, PatientNavigationDrawerActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(Username)) {
                    usernameInputLayout.setError("Username is Required");
                    usernameInputLayout.setErrorIconDrawable(null);
                } else if (!Username.matches(usernameRegx)) {
                    usernameInputLayout.setError("Username is invalid. It should start with a letter and have at least 6 characters.");
                    usernameInputLayout.setErrorIconDrawable(null);
                } else if (TextUtils.isEmpty(email)) {
                    emailInputLayout.setError("Email is Required");
                    emailInputLayout.setErrorIconDrawable(null);
                } else if (!email.matches(emailRegx)) {
                    emailInputLayout.setError("Email is invalid.");
                    emailInputLayout.setErrorIconDrawable(null);
                } else if (TextUtils.isEmpty(password)) {
                    passwordInputLayout.setError("Password is Required");
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
                } else {
                    patientAPI.checkUsernameAvailability(Username).enqueue(new Callback<UsernameAvailabilityResponse>() {
                        @Override
                        public void onResponse(Call<UsernameAvailabilityResponse> call, Response<UsernameAvailabilityResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().isAvailable()) {
                                    // Username is available, proceed with registration
                                    // ... (existing registration code)
                                    progressBar.setVisibility(View.VISIBLE);

                                    patientAPI.registerPatient(new Patient(email, password, Username, null)).enqueue(new Callback<RegistrationResponse>() {
                                        @Override
                                        public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                                            if (response.isSuccessful()) {
                                                String patientId = response.body().get_id();
                                                insertBreathingExercises(patientId);
                                                Toast.makeText(PatientRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(PatientRegister.this, PatientLogin.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Check if the error is due to an existing email
                                                if (response.code() == 400) {
                                                    Toast.makeText(PatientRegister.this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(PatientRegister.this, "Failed to register patient.", Toast.LENGTH_SHORT).show();
                                                }
                                                progressBar.setVisibility(View.GONE);
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                                            Toast.makeText(PatientRegister.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                                } else {
                                    // Username is not available, show an error
                                    usernameInputLayout.setError("Username already taken");
                                    usernameInputLayout.setErrorIconDrawable(null);
                                }
                            } else {
                                Toast.makeText(PatientRegister.this, "Failed to check username availability.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UsernameAvailabilityResponse> call, Throwable t) {
                            Toast.makeText(PatientRegister.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.d("Network Error1", "Error: " + t.getMessage());
                        }
                    });
                }
            }
        });
    }


    private void insertBreathingExercises(String patient_id) {
        int equalbreathing = 2;
        int boxbreathing = 2;
        int fourseveneightbreathing = 2;
        int trianlgebreathing = 2;
        int music = 1;


        BreathingExercisesModel breathingExercisesModel = new BreathingExercisesModel();

        breathingExercisesModel.setPatient_id(patient_id);
        breathingExercisesModel.setEqualBreathing(equalbreathing);
        breathingExercisesModel.setBoxBreathing(boxbreathing);
        breathingExercisesModel.setFourseveneightBreathing(fourseveneightbreathing);
        breathingExercisesModel.setTrianglebreathing(trianlgebreathing);
        breathingExercisesModel.setMusic(music);


        BreathingExercisesAPI apiService = RetrofitClientInstance.getRetrofitInstance().create(BreathingExercisesAPI.class);
        Call<Void> call = apiService.insertBreathingExercises(breathingExercisesModel);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    finish();

                } else {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void OnTypeErrorRemoval() {
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
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != password_error_len) {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setErrorEnabled(false);
                }
            }



            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameInputLayout.setError(null);
                usernameInputLayout.setErrorEnabled(false);
            }



            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                emailInputLayout.setError(null);
                emailInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onBackPressed() {
        Intent i = new Intent(PatientRegister.this, UserOptionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}


