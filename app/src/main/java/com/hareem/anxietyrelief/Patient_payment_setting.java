package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Patient_payment_setting extends AppCompatActivity implements BottomAddCardEdit.EditListener {
    LinearLayout linearLayout;
    TextView addcard;
    Button save;
    List<CardData> cardList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_payment_setting); // Set the content view first

        addcard = findViewById(R.id.addcards);
        save=findViewById(R.id.savebtn);
        getcards();
        addcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomAddCard bottomSheetFragment = new BottomAddCard();


                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardList.isEmpty()) {
                    Toast.makeText(Patient_payment_setting.this, "No cards to save", Toast.LENGTH_SHORT).show();
                    SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    String patientId = preferences.getString("currentPatientId", "");
                    saveCard(patientId, cardList);
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                   String patientId = preferences.getString("currentPatientId", "");

                // Save the cardList
                saveCard(patientId, cardList);
            }
        });

        linearLayout = findViewById(R.id.patientcardlist1);



    }
    public void updateCardNumber(String cardNumber, CardData cardData) {


        View view = getLayoutInflater().inflate(R.layout.patientcardlist, null);
        linearLayout.addView(view);


        // Find the TextView and ImageView in the inflated view
        TextView cardNumberTextView = view.findViewById(R.id.cardnumbertextview);
        ImageView icon = view.findViewById(R.id.deleteiconarrow);
        Drawable drawable = getResources().getDrawable(R.drawable.baseline_delete_24);

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.pomp_power));

        // Set the tinted drawable as the source for the ImageView
        icon.setImageDrawable(drawable);
        icon.setImageResource(R.drawable.baseline_delete_24);

        // Set the card number to the TextView
        cardNumberTextView.setText("**** **** **** " + cardNumber);

        // Associate the CardData object with the view
        view.setTag(cardData);



        // Set click listener for the icon
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the CardData associated with the clicked view
                CardData clickedCard = (CardData) view.getTag();

                // Remove the clicked CardData from the cardList
                cardList.remove(clickedCard);

                // Remove the view from the layout
                linearLayout.removeView(view);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Determine the position of the clicked view in the linearLayout
                int position = linearLayout.indexOfChild(view);

                CardData cardData = cardList.get(position);

                BottomAddCardEdit bottomSheetFragment = new BottomAddCardEdit(view, cardData, cardData.getCardNumber(), cardData.getCardname(),
                        cardData.getCvv(), cardData.getMonth(), cardData.getYear());

                // Set the edit listener
                bottomSheetFragment.setEditListener(Patient_payment_setting.this);

                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });

        // Add the CardData to the cardList
        cardList.add(cardData);
    }

    @Override
    public void onEditComplete(CardData cardData) {
        // Remove the associated CardData from the list
        cardList.remove(cardData);
    }
    private PatientAPI patientAPI;


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
                    for (CardData cardData : cardList) {
                         String cardNumber=cardData.getCardNumber();
                        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
                       updateCardNumber(lastFourDigits,cardData);
                    }
                } else {
                    // Handle unsuccessful response
                    // Show error message or retry
                }
            }

            @Override
            public void onFailure(Call<List<CardData>> call, Throwable t) {
                // Handle failure
                // Show error message or retry
            }
        });
    }



    public void saveCard(String patientId, List<CardData> cardDataList) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        // Create the CardService instance
        patientAPI = retrofit.create(PatientAPI.class);
        Call<Void> call = patientAPI.saveCard(patientId,cardDataList);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Patient_payment_setting.this, "Cards saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                  //  Toast.makeText(Patient_payment_setting.this, "Failed to save cards", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Patient_payment_setting.this, "Error saving cards: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
