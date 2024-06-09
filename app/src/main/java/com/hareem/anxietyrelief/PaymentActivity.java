package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentActivity extends AppCompatActivity {
    private PatientAPI patientAPI;
    LinearLayout linearLayout;
    LinearLayout savedCardSection;
    TextView totalCharges;
    Button paynowbtn;
    TextInputEditText cardNumberEditText,cardnameEditText,monthEditText,yearEditText,cvvEditText;
    String Therapist_account_number;
    String Total_charges;
    TextInputLayout cardNumberInputLayout,cardnameInputLayout,monthInputLayout,yearInputLayout,cvvInputLayout;
    private RadioButton selectedRadioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        cardNumberEditText=findViewById(R.id.cardNumberEditText);
        cardNumberInputLayout=findViewById(R.id.cardNumberInputLayout);
        cardnameInputLayout=findViewById(R.id.cardnameInputLayout);
        cardnameEditText=findViewById(R.id.cardnameEditText);
        paynowbtn=findViewById(R.id.btnpay);
        monthInputLayout=findViewById(R.id.monthInputLayout);
        monthEditText=findViewById(R.id.monthEditText);
        yearEditText=findViewById(R.id.yearEditText);
        yearInputLayout=findViewById(R.id.yearInputLayout);
        cvvEditText=findViewById(R.id.cvvEditText);
        cvvInputLayout=findViewById(R.id.cvvInputLayout);
        linearLayout = findViewById(R.id.savedcardslist);
        savedCardSection=findViewById(R.id.savedcardsection);
        totalCharges=findViewById(R.id.totalcharges);
        OnTypeErrorRemoval();
        getcards();
        selectedRadioButton=null;
        ImageView backarrow=findViewById(R.id.back);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        SharedPreferences ViewTherapistsharedPreferences = getSharedPreferences("ViewTherapistPreferences", Context.MODE_PRIVATE);
        String therapistId = ViewTherapistsharedPreferences.getString("ViewProfiletherapist_id", "");


        getTherapistData(therapistId );
        paynowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumber = cardNumberEditText.getText().toString();
                String cardname=cardnameEditText.getText().toString();
                String cvv=cvvEditText.getText().toString();
                String month=monthEditText.getText().toString();
                String year=yearEditText.getText().toString();
                if (TextUtils.isEmpty(cardNumber)) {
                    cardNumberInputLayout.setError("Card Number is Required");
                    cardNumberInputLayout.setErrorIconDrawable(null);
                }  else if (TextUtils.isEmpty(month)) {
                    monthInputLayout.setError("Card Number is Required");
                    monthInputLayout.setErrorIconDrawable(null);
                } else if (TextUtils.isEmpty(year)) {
                    yearInputLayout.setError("Card Number is Required");
                    yearInputLayout.setErrorIconDrawable(null);
                } else if (TextUtils.isEmpty(cardname)) {
                    cardnameInputLayout.setError("Card Number is Required");
                    cardnameInputLayout.setErrorIconDrawable(null);

                } else if (TextUtils.isEmpty(cvv)) {
                    cvvInputLayout.setError("Card Number is Required");
                    cvvInputLayout.setErrorIconDrawable(null);
                }else if (cardNumber.length()!=19) {
                    cardNumberInputLayout.setError("enter full card number");
                    cardNumberInputLayout.setErrorIconDrawable(null);
                }else if (month.length()!=2) {
                    monthInputLayout.setError("enter month in XX format");
                    monthInputLayout.setErrorIconDrawable(null);
                }else if (year.length()!=4) {
                    yearInputLayout.setError("enter year in XXXX format");
                    yearInputLayout.setErrorIconDrawable(null);
                }else if (cvv.length()!=3) {
                    cvvInputLayout.setError("enter full cvv");
                    cvvInputLayout.setErrorIconDrawable(null);
                }else if (!isValidMonth(month)) {
                    monthInputLayout.setError("Invalid month");
                    monthInputLayout.setErrorIconDrawable(null);
                } else if (!isValidYear(year)) {
                    yearInputLayout.setError("invalid year");
                    yearInputLayout.setErrorIconDrawable(null);
                } else {
                    String patient_cardNumber = cardNumberEditText.getText().toString().replaceAll("[^0-9]", "");

                    TransationtoDB(patientId,therapistId,Therapist_account_number,patient_cardNumber,Total_charges,"pending");
                }

            }
        });
    }
    private void TransationtoDB(String patientId, String therapistId, String therapistAccountNumber, String patientAccountNumber, String charges, String status) {
        try {
            // Create a JSON object with the required fields
            JSONObject requestBody = new JSONObject();
            requestBody.put("therapistId", therapistId);
            requestBody.put("patientId", patientId);
            requestBody.put("therapistAccountNumber", therapistAccountNumber);
            requestBody.put("patientAccountNumber", patientAccountNumber);
            requestBody.put("charges", charges);
            requestBody.put("status", status);


            // Create a RequestBody using the JSON object
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

            // Make an HTTP POST request using Retrofit
            Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
            PatientAPI patientAPI = retrofit.create(PatientAPI.class);

            Call<Void> call = patientAPI.saveTransaction(body);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String transactionId = response.headers().get("Transaction-Id");


                        // save session to db with transactionId here!!!
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                        String patient_id = sharedPreferences.getString("currentPatientId", null);

                        SharedPreferences ViewTherapistsharedPreferences = getSharedPreferences("ViewTherapistPreferences", Context.MODE_PRIVATE);
                        String therapistId = ViewTherapistsharedPreferences.getString("ViewProfiletherapist_id", "");

                        SharedPreferences SessionTypesharedPreferences = getSharedPreferences("SessionType", Context.MODE_PRIVATE);
                        String SessionType= SessionTypesharedPreferences.getString("SessionType", "");

                        SharedPreferences TimeSlotsharedPreferences = getSharedPreferences("Time Slot", Context.MODE_PRIVATE);
                        String TimeSlot= TimeSlotsharedPreferences.getString("Time Slot", "");

                        SharedPreferences DayandDatesharedPreferences = getSharedPreferences("SelectedDay&Date", Context.MODE_PRIVATE);
                        String DayandDate= DayandDatesharedPreferences.getString("SelectedDay&Date", "");

                        AppointmentsDB(patient_id,therapistId,SessionType,DayandDate,TimeSlot,transactionId);



                    } else {

                        Toast.makeText(PaymentActivity.this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Handle failure
                    Toast.makeText(PaymentActivity.this, "Failed to save transaction: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void AppointmentsDB(String patientId, String therapistId, String SessionType, String day, String time, String transactionId) {
        try {
            // Create a JSON object with the required fields
            SharedPreferences sharedPreferences = getSharedPreferences("TherapistUsername", Context.MODE_PRIVATE);
            String therapistUsername = sharedPreferences.getString("TherapistUsername", "");
            SharedPreferences sharedPreferences1 = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
            String username = sharedPreferences1.getString("patient_username2", null);
            JSONObject requestBody = new JSONObject();
            requestBody.put("therapistId", therapistId);

            requestBody.put("patientId", patientId);
            requestBody.put("SessionType", SessionType);
            requestBody.put("day", day);
            requestBody.put("time", time);
            requestBody.put("transactionId", transactionId);
            requestBody.put("therapistname",therapistUsername );
            requestBody.put("patientname", username);




            // Create a RequestBody using the JSON object
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

            // Make an HTTP POST request using Retrofit
            Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
            PatientAPI patientAPI = retrofit.create(PatientAPI.class);

            Call<Void> call = patientAPI.saveAppointments(body);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(PaymentActivity.this, SucessfullyBooked.class);
                        startActivity(intent);

                        // Finish the PaymentActivity to prevent navigating back to it

                        finish();
                    } else {

                        Toast.makeText(PaymentActivity.this, "Appointment Saved", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Handle failure
                    Toast.makeText(PaymentActivity.this, "Failed to save appointment: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void updateCardNumber(String cardNumber, CardData cardData) {
        View view = getLayoutInflater().inflate(R.layout.saved_cards, null);
        linearLayout.addView(view);

        RadioButton cardNumberRadioButton = view.findViewById(R.id.radio_button);
        cardNumberRadioButton.setText("**** **** **** " + cardNumber);

        view.setTag(cardData);
        cardNumberRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioButton = (RadioButton) v;
                // Uncheck previously selected radio button if any
                if (selectedRadioButton != null && selectedRadioButton != radioButton) {
                    selectedRadioButton.setChecked(false);
                }
                // Set the current radio button as selected
                radioButton.setChecked(true);
                // Update the selected radio button reference
                selectedRadioButton = radioButton;
                showSelectedCardData(cardData);
            }
        });

    }
    private void showSelectedCardData(CardData cardData) {

        cardNumberEditText.setText(cardData.getCardNumber());
        cardnameEditText.setText(cardData.getCardname());
        cvvEditText.setText(cardData.getCvv());
        monthEditText.setText(cardData.getMonth());
        yearEditText.setText(cardData.getYear());
    }


    public void  getcards(){
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();

        patientAPI = retrofit.create(PatientAPI.class);

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        Call<List<CardData>> call = patientAPI.getCards(patientId);
        call.enqueue(new Callback<List<CardData>>() {
            @Override
            public void onResponse(Call<List<CardData>> call, Response<List<CardData>> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    List<CardData> cardList = response.body();
                    if (cardList.isEmpty()){
                        savedCardSection.setVisibility(View.GONE);
                    }else {
                        savedCardSection.setVisibility(View.VISIBLE);
                    }
                    for (CardData cardData : cardList) {
                        String cardNumber=cardData.getCardNumber();
                        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
                        updateCardNumber(lastFourDigits,cardData);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<CardData>> call, Throwable t) {
                // Handle failure
                // Show error message or retry
            }
        });
    }
    private boolean isValidMonth(String month) {
        try {
            int monthValue = Integer.parseInt(month);
            return monthValue >= 1 && monthValue <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean isValidYear(String year) {
        try {
            int yearValue = Integer.parseInt(year);
            Calendar calendar = Calendar.getInstance();

            int currentYear =  calendar.get(Calendar.YEAR);;
            return yearValue >= currentYear;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void OnTypeErrorRemoval() {
        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                cardNumberInputLayout.setError(null);
                cardNumberInputLayout.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cardnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                cardnameInputLayout.setError(null);
                cardnameInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        monthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                monthInputLayout.setError(null);
                monthInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        yearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                yearInputLayout.setError(null);
                yearInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        cvvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                cvvInputLayout.setError(null);
                cvvInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getTherapistData(String therapistId) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        if (retrofit == null) {
            // Handle null Retrofit instance
            return;
        }

        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);


        Call<Therapistprofiledata> call = therapistAPI.getTherapistData(therapistId);
        call.enqueue(new Callback<Therapistprofiledata>() {
            @Override
            public void onResponse(Call<Therapistprofiledata> call, Response<Therapistprofiledata> response) {
                if (response.isSuccessful()) {
                    Therapistprofiledata therapist = response.body();
                    if (therapist != null) {
                        // Populate UI fields with therapist data
                        populateTherapistData(therapist);

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<Therapistprofiledata> call, Throwable t) {

            }
        });
    }

    private void populateTherapistData(Therapistprofiledata therapist) {
        if (!therapist.getCharges().isEmpty()){
            Total_charges=therapist.getCharges();
            totalCharges.setText("Total: "+therapist.getCharges()+" Rs");
        }
        List<AccountData> accountList = therapist.getAccountList();
        if (accountList != null && !accountList.isEmpty()) {
            AccountData firstAccount = accountList.get(0);
            Therapist_account_number = firstAccount.getNumber();
        }
    }
}