package com.hareem.anxietyrelief;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TherapistRegister extends AppCompatActivity {
    TextView linktextview;
    int password_error_len;
    int confirmPassword_error_len;
    private TherapistAPI therapistAPI;
    String usernameRegx="^[a-zA-Z][a-zA-Z0-9]{5,}$";
    String emailRegx="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    String passwordRegx="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$";
      AppCompatButton RegisterBtn;
    TextInputEditText usernameEditText,emailEditText,passwordEditText,confirmPasswordEditText;
    TextInputLayout usernameInputLayout,emailInputLayout,passwordInputLayout,confirmPasswordInputLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_register);
        passwordInputLayout=findViewById(R.id.passwordTextInputLayoutth);
        passwordEditText=findViewById(R.id.passwordEditTextth);
        confirmPasswordInputLayout=findViewById(R.id.confirmpasswordTextInputLayoutth);
        confirmPasswordEditText=findViewById(R.id.confirmpasswordEditTextth);
        usernameInputLayout=findViewById(R.id.usernameTextInputLayoutth);
        usernameEditText=findViewById(R.id.usernameEditTextth);
        emailInputLayout=findViewById(R.id.emailTextInputLayoutth);
        emailEditText=findViewById(R.id.emailEditTextth);
        RegisterBtn=findViewById(R.id.registerbtnth);
ProgressBar progressBar=findViewById(R.id.registrationProgressBarth);
        therapistAPI = RetrofitClientInstance.getRetrofitInstance().create(TherapistAPI.class);

        linktextview=findViewById(R.id.linkTextView);

        String text="Already have an account? Login";
        SpannableString ss=new SpannableString(text);
        StyleSpan styleSpan=new StyleSpan(Typeface.BOLD);
        ss.setSpan(styleSpan,25,30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linktextview.setText(ss);
        linktextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TherapistRegister.this,TherapistLogin.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){

            Intent i = new Intent(TherapistRegister.this,therapist_navigation.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        OnTypeErrorRemoval();
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
                }
                else if (TextUtils.isEmpty(email)) {

                    emailInputLayout.setError("Email is Required");
                    emailInputLayout.setErrorIconDrawable(null);

                }else if (!email.matches(emailRegx)) {

                    emailInputLayout.setError("Email is invalid.");
                    emailInputLayout.setErrorIconDrawable(null);
                }
                else if (TextUtils.isEmpty(password)) {

                    passwordInputLayout.setError("Password is Required");
                    passwordInputLayout.setErrorIconDrawable(null);



                } else if (!password.matches(passwordRegx)) {

                    passwordInputLayout.setError("Password is invalid. It must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.");
                    passwordInputLayout.setErrorIconDrawable(null);
                    password_error_len=password.length();
                }
                else if (TextUtils.isEmpty(confirmPassword)) {

                    confirmPasswordInputLayout.setError("Confirm Password is Required");
                    confirmPasswordInputLayout.setErrorIconDrawable(null);

                } else if (!confirmPassword.matches(password)) {
                    confirmPasswordInputLayout.setError("Confirm Password not match Password field");
                    confirmPasswordInputLayout.setErrorIconDrawable(null);
                    confirmPassword_error_len=confirmPassword.length();
                }else {
                    therapistAPI.checkUsernameAvailability(Username).enqueue(new Callback<UsernameAvailabilityResponse>() {
                        @Override
                        public void onResponse(Call<UsernameAvailabilityResponse> call, Response<UsernameAvailabilityResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().isAvailable()) {
                                    // Username is available, proceed with registration
                                    // ... (existing registration code)
                                    progressBar.setVisibility(View.VISIBLE);

                                    therapistAPI.registerTherapist(new Therapist(email, password, Username, null)).enqueue(new Callback<RegistrationResponse>() {
                                        @Override
                                        public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(TherapistRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(TherapistRegister.this, TherapistLogin.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Check if the error is due to an existing email
                                                if (response.code() == 400) {
                                                    Toast.makeText(TherapistRegister.this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(TherapistRegister.this, "Failed to register Therapist.", Toast.LENGTH_SHORT).show();
                                                }
                                                progressBar.setVisibility(View.GONE);
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                                            Toast.makeText(TherapistRegister.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                                } else {
                                    // Username is not available, show an error
                                    usernameInputLayout.setError("Username already taken");
                                    usernameInputLayout.setErrorIconDrawable(null);
                                }
                            } else {
                                Toast.makeText(TherapistRegister.this, "Failed to check username availability.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UsernameAvailabilityResponse> call, Throwable t) {
                            Toast.makeText(TherapistRegister.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.d("Network Error1", "Error: " + t.getMessage());
                        }
                    });
                }
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
                if (s.length()!=confirmPassword_error_len) {
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                if (s.length()!=password_error_len) {
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
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

        Intent i = new Intent(TherapistRegister.this, UserOptionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
        super.onBackPressed();
    }











}