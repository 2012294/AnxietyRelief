package com.hareem.anxietyrelief;

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
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.bson.types.ObjectId;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TherapistLogin extends AppCompatActivity {

    AppCompatButton btnLogin;
    String emailRegx = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    int password_error_len;
    Boolean move=false;

    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    TherapistAPI apiService;
    TextInputEditText emailEditText, passwordEditText;
    TextInputLayout emailInputLayout, passwordInputLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_login);
        emailInputLayout = findViewById(R.id.emailTextInputLayoutth);
        emailEditText = findViewById(R.id.emailEditTextth);
        passwordInputLayout = findViewById(R.id.passwordTextInputLayoutth);
        passwordEditText = findViewById(R.id.passwordEditTextth);
        progressBar=findViewById(R.id.loginProgressBarth);
        btnLogin = findViewById(R.id.loginbuttonth);
        TextView linktextview = findViewById(R.id.linkTextView);
        String text = "Do not have an account? Signup";
        SpannableString ss = new SpannableString(text);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(styleSpan, 24, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linktextview.setText(ss);
        sharedPreferences=getSharedPreferences("mypref",MODE_PRIVATE);
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TherapistAPI.class);
        linktextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TherapistLogin.this,TherapistRegister.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    emailInputLayout.setError("Email is Required");
                    emailInputLayout.setErrorIconDrawable(null);
                } else if (!email.matches(emailRegx)) {
                    emailInputLayout.setError("Email not formatted correctly");
                    emailInputLayout.setErrorIconDrawable(null);
                } else if (TextUtils.isEmpty(password)) {
                    passwordInputLayout.setError("Password is Required");
                    passwordInputLayout.setErrorIconDrawable(null);
                } else {
                    login(email, password,view);
                }
            }
        });

        OnTypeErrorRemoval();
    }
    private void login(final String email, String password, View view) {
        Log.d("Login", "Login button clicked");
        progressBar.setVisibility(View.VISIBLE);






        Call<LoginResponse> call = apiService.loginTherapists(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse != null) {
                        // Handle successful login
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("therapist_email", email);
                        editor.apply();

                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                        editor1.putBoolean("therapist_login",true);
                        editor1.apply();

                        Log.d("LoginResponse", "UID: " + loginResponse.get_id());
                        ObjectId objectId = new ObjectId(loginResponse.get_id());
                        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor3 = preferences.edit();
                        editor3.putString("currentTherapistId", String.valueOf(objectId));
                        editor3.apply();
                        getUsernameAndSaveToSharedPreferences(objectId);
                    } else {
                        // Handle null response
                        Log.d("LoginResponse", "Unexpected null response");
                        Toast.makeText(TherapistLogin.this, "Unexpected response. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d("LoginResponse", "Unsuccessful response. Code: " + response.code());

                    if (response.code() == 401) {
                        // Unauthorized - incorrect password or email not registered
                        try {
                            // Parse the error body and check the specific error message
                            String errorBody = response.errorBody().string();

                            if (errorBody.contains("Incorrect password")) {
                                passwordInputLayout.setError("Incorrect password");
                                passwordInputLayout.setErrorIconDrawable(null);
                            } else if (errorBody.contains("Email not registered")) {
                                emailInputLayout.setError("Email does not exist");
                                emailInputLayout.setErrorIconDrawable(null);
                            } else {
                                // Other error, display a generic message
                                Toast.makeText(TherapistLogin.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Handle error parsing error body
                            Toast.makeText(TherapistLogin.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Other HTTP status code, display a generic message
                        Toast.makeText(TherapistLogin.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d("LoginResponse", "Network error", t);
                Toast.makeText(TherapistLogin.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
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

        Intent i = new Intent(TherapistLogin.this, UserOptionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
    public void getUsernameAndSaveToSharedPreferences(ObjectId userId) {


        Call<Therapist> call = apiService.getTherapist(userId);

        call.enqueue(new Callback<Therapist>() {
            @Override
            public void onResponse(Call<Therapist> call, Response<Therapist> response) {
                if (response.isSuccessful()) {
                    Therapist therapist = response.body();

                    if (therapist != null) {
                        // Get the username associated with the patient's data
                        String username = therapist.getUsername();

                        // Save username to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("therapist_username2", username);
                        editor.apply();

                        // Run UI operations on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Start the new activity, update UI, etc.
                                Intent i = new Intent(TherapistLogin.this, therapist_navigation.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        });

                        Toast.makeText(TherapistLogin.this, "Login successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TherapistLogin.this, "User not found.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.d("ResponseError1", "Unsuccessful response. Code: " + response.code());
                    try {
                        Log.d("ResponseBody1", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(TherapistLogin.this, "Failed to get user. Please try again.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Therapist> call, Throwable t) {
                Log.d("NetworkError1", "Failed to get user", t);
                Toast.makeText(TherapistLogin.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
