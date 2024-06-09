package com.hareem.anxietyrelief;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;
import static okio.ByteString.decodeBase64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hareem.anxietyrelief.RetrofitClientInstance;
import com.hareem.anxietyrelief.Therapist;
import com.hareem.anxietyrelief.TherapistAPI;

import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewTherapistProfile extends AppCompatActivity {

    private TextView usernameTextView, emailTextView;
    private ImageView profileImageView;
    private TableLayout    selectedfileTable;
    List<byte[]> pdfData = new ArrayList<>();
    private TextView chargeTextView, availabilityTextView, degreesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_therapist_profile);

        // Initialize views
        usernameTextView = findViewById(R.id.textView_username);
        selectedfileTable=findViewById(R.id.selectedfileTable);
        emailTextView = findViewById(R.id.textView_email);
        profileImageView = findViewById(R.id.account_image);
        chargeTextView = findViewById(R.id.textView_charge);
        availabilityTextView = findViewById(R.id.textView_availability);
        degreesTextView = findViewById(R.id.textView_degrees);


        SharedPreferences ViewTherapistsharedPreferences = getSharedPreferences("ViewTherapistPreferences", Context.MODE_PRIVATE);
        String therapistId = ViewTherapistsharedPreferences.getString("ViewProfiletherapist_id", "");


        ObjectId objectId = new ObjectId(therapistId);


        fetchTherapistData(objectId);
        Button bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start BookAppointment activity
                Intent intent = new Intent(ViewTherapistProfile.this, BookAppointment.class);

                startActivity(intent);
            }
        });
    }

    private void fetchTherapistData(ObjectId therapistId) {
        // Create an instance of the TherapistAPI interface
        PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

        // Make a network call to get the therapist's data by ID
        Call<Therapist> call = patientAPI.getTherapist(therapistId);
        call.enqueue(new Callback<Therapist>() {
            @Override
            public void onResponse(Call<Therapist> call, Response<Therapist> response) {
                if (response.isSuccessful()) {
                    // Therapist data fetched successfully
                    Therapist therapist = response.body();
                    // Update UI with therapist data
                    updateUI(therapist);

                } else {
                    // Handle API error
                    Toast.makeText(ViewTherapistProfile.this, "Failed to fetch therapist data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Therapist> call, Throwable t) {
                // Handle network failure
                Toast.makeText(ViewTherapistProfile.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Therapist therapist) {
        // Update UI elements with therapist data
        if (therapist != null) {
            usernameTextView.setText(therapist.getUsername());
            String therapistUsername = usernameTextView.getText().toString();
            SharedPreferences.Editor editor = getSharedPreferences("TherapistUsername", Context.MODE_PRIVATE).edit();
            editor.putString("TherapistUsername", therapistUsername);
            editor.apply();

            emailTextView.setText(therapist.getEmail());
            if (profileImageView != null && therapist.getImage() != null) {
                byte[] decodedImage = Base64.decode(therapist.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                profileImageView.setImageBitmap(bitmap);
            }
            // Display additional information
            chargeTextView.setText(therapist.getCharges());

            // Build availability string
            StringBuilder availabilityStringBuilder = new StringBuilder();
            for (String time : therapist.getAvailableTime()) {
                time = time.replace("Day:", "").trim();
                // Split the time string to get start time and end time
                String[] parts = time.split(", ");
                if (parts.length == 3) {
                    availabilityStringBuilder.append("\n\n");
                    availabilityStringBuilder.append(parts[0]).append(", "); // Add day
                    availabilityStringBuilder.append(parts[1].substring(parts[1].indexOf(":") + 2)).append(" - "); // Add start time
                    availabilityStringBuilder.append(parts[2].substring(parts[2].indexOf(":") + 2)); // Add end time
                }
            }
            availabilityTextView.setText(availabilityStringBuilder.toString());

            // Build degrees string
            StringBuilder degreesStringBuilder = new StringBuilder();
            for (String degree : therapist.getDegree()) {
                degreesStringBuilder.append("\n\n"); // Add two new lines before each degree
                degreesStringBuilder.append(degree);
            }
            degreesTextView.setText(degreesStringBuilder.toString());
            List<PdfFile> pdfFiles = therapist.getPdfFiles();
            if (pdfFiles != null) {
                for (PdfFile pdfFile : pdfFiles) {
                    String originalPdfName = pdfFile.getOriginalPdfName();

                    addfetchedfile(originalPdfName);
                    byte[] byteArray   = decodeBase64(pdfFile.getPdfData()).toByteArray();

                    pdfData.add(byteArray);
                }
            }

        }
    }

    private void addfetchedfile(String availablefile1) {
        View rowView = getLayoutInflater().inflate(R.layout.degree_row_layout, null);

        // Find views in the row layout
        TextView degreeTextView = rowView.findViewById(R.id.degreeTextView);

        ImageView deleteIcon = rowView.findViewById(R.id.deleteIcon);
        deleteIcon.setVisibility(View.GONE);

        degreeTextView.setText(availablefile1);
        degreeTextView.setTextSize(18);




        // Add the row to the table
        TableRow newRow = new TableRow(this);
        newRow.addView(rowView);

        degreeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int index = selectedfileTable.indexOfChild(newRow);
                if (index >= 0 && index < pdfData.size()) {
                   showPdfDialog(pdfData.get(index));
                }
            }
        });
        selectedfileTable.addView(newRow);
    }
    private void showPdfDialog(byte[] pdfData) {
        Uri pdfUri = createTempPdfFile(pdfData);
        if (pdfUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Handle error: no PDF viewer app installed
                Toast.makeText(this, "No PDF viewer app installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle error: unable to create temporary PDF file
            Toast.makeText(this, "Unable to open PDF", Toast.LENGTH_SHORT).show();
        }
    }
    private Uri createTempPdfFile(byte[] pdfData) {
        try {
            File pdfFile = File.createTempFile("temp_pdf", ".pdf", this.getCacheDir());
            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write(pdfData);
            fos.close();
            return FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
