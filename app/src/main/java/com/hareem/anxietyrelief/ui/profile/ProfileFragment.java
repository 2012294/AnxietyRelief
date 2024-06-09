package com.hareem.anxietyrelief.ui.profile;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.net.Uri;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;


import com.google.android.material.textfield.TextInputLayout;
import com.hareem.anxietyrelief.AccountData;

import com.hareem.anxietyrelief.PatientAPI;
import com.hareem.anxietyrelief.Patient_payment_setting;
import com.hareem.anxietyrelief.PdfFile;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.RetrofitClientInstance;
import com.hareem.anxietyrelief.TherapistAPI;
import com.hareem.anxietyrelief.Therapistprofiledata;
import com.hareem.anxietyrelief.databinding.FragmentProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment  {
    private FragmentProfileBinding binding;

    List<AccountData> accountList = new ArrayList<>();
    private TableLayout selectedItemsTable;
    private TableLayout selectedDegreeTable;

    private TableLayout selectedfileTable;

    private EditText chargesEditText;
    private ImageButton editChargesIcon;
    private ImageView editProfileIcon;
    private Button addButton,upload;
    private static final int PICK_PDF_REQUEST = 1;
    private Button saveButton;
    private Button addaccountButton;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE1 = 3;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    ImageView account_image;
    String newCharges;
    LinearLayout linearLayout1;

    List<byte[]> pdfData = new ArrayList<>();


    Spinner Degreespinner;
    private boolean isEditingCharges = false;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("therapist_username2", null);
        String email = sharedPreferences.getString("therapist_email", null);
        upload=binding.uploadbtn;

        TextInputEditText usernameEditText = binding.UsernamedisplayText;
        TextInputEditText emailEditText = binding.EmaildisplayText;
        account_image=binding.accountImage;

        addaccountButton=binding.addaccountbtn;

        // Place the method outside of the onClick() method


// Your OnClickListener
        addaccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View customView = getLayoutInflater().inflate(R.layout.fragment_add_account, null);
                builder.setView(customView);

                TextInputEditText cardNumberEditText = customView.findViewById(R.id.cardNumberEditText);
                TextInputEditText cardnameEditText = customView.findViewById(R.id.cardnameEditText);
                TextInputLayout cardNumberInputLayout = customView.findViewById(R.id.cardNumberInputLayout);
                TextInputLayout cardnameInputLayout = customView.findViewById(R.id.cardnameInputLayout);

                Button doneButton = customView.findViewById(R.id.btndone);

                // Create the dialog
                AlertDialog dialog = builder.create();

                // Format card number with dashes
                cardNumberEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String text = s.toString().replaceAll("[^\\d]", ""); // Remove non-numeric characters
                        StringBuilder formattedText = new StringBuilder();

                        for (int i = 0; i < text.length(); i++) {
                            formattedText.append(text.charAt(i));
                            if ((i + 1) % 4 == 0 && (i + 1) < text.length()) {
                                formattedText.append("-");
                            }
                        }

                        cardNumberEditText.removeTextChangedListener(this); // Prevent infinite loop
                        cardNumberEditText.setText(formattedText.toString());
                        cardNumberEditText.setSelection(cardNumberEditText.getText().length()); // Set cursor to the end
                        cardNumberEditText.addTextChangedListener(this); // Add TextWatcher back
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                // Set an OnClickListener for the "Done" button
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cardNumber = cardNumberEditText.getText().toString().replaceAll("[^\\d]", ""); // Remove non-numeric characters
                        String cardname = cardnameEditText.getText().toString();

                        if (TextUtils.isEmpty(cardNumber)) {
                            cardNumberInputLayout.setError("Card Number is Required");
                            cardNumberInputLayout.setErrorIconDrawable(null);
                        } else if (TextUtils.isEmpty(cardname)) {
                            cardnameInputLayout.setError("Card Name is Required");
                            cardnameInputLayout.setErrorIconDrawable(null);
                        } else if (cardNumber.length() != 16) {
                            cardNumberInputLayout.setError("Enter full card number");
                            cardNumberInputLayout.setErrorIconDrawable(null);
                        } else {
                            AccountData accountData = new AccountData(cardNumber, cardname);
                            String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
                            onUpdateCardNumber(lastFourDigits, accountData);
                            // Dismiss the dialog
                            dialog.dismiss();
                        }
                    }
                });

                // Call the method to remove error when the user starts typing
                OnTypeErrorRemoval2(cardNumberEditText, cardNumberInputLayout, cardnameEditText, cardnameInputLayout);

                // Show the dialog
                dialog.show();
            }
        });





        Spinner startTimeSpinner = root.findViewById(R.id.startTimeSpinner);
        Spinner endTimeSpinner = root.findViewById(R.id.endTimeSpinner);



        String[] startTimings = getResources().getStringArray(R.array.starttimings);
        String[] endTimings = getResources().getStringArray(R.array.endtimings);



        ArrayAdapter<String> startTimeAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, startTimings);
        startTimeSpinner.setAdapter(startTimeAdapter);


        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int selectedStartTimeIndex = parent.getSelectedItemPosition();


                String[] availableEndTimings = Arrays.copyOfRange(endTimings, selectedStartTimeIndex + 1, endTimings.length);


                ArrayAdapter<String> endTimeAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, availableEndTimings);
                endTimeSpinner.setAdapter(endTimeAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        if (username != null) {

            usernameEditText.setText(username);
        }

        if (email != null) {

            emailEditText.setText(email);
        }


        selectedItemsTable = binding.selectedItemsTable;
        selectedDegreeTable=binding.selecteddegreeTable;
        selectedfileTable=binding.selectedfileTable;
   linearLayout1 = binding.accounttable;
        addButton = binding.submitButton;
        saveButton = binding.saveButton;
        Button addDegreeButton=binding.submitButton1;

        addDegreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRowToTableDegree();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRowToTable();
            }
        });

        Degreespinner = root.findViewById(R.id.degreeSpinner); // Find the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.degrees_array, R.layout.spinner_item_layout);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout); // Set the dropdown view resource
        Degreespinner.setAdapter(adapter);

        chargesEditText = binding.charges;
        editChargesIcon = binding.editChargesIcon;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(chargesEditText.getText().toString())) {
                    newCharges = chargesEditText.getText().toString();
                } else {
                    newCharges = "";
                }
                List<String> therapistAvailability = new ArrayList<>();

                if (selectedItemsTable.getChildCount() != 0) {
                    for (int i = 0; i < selectedItemsTable.getChildCount(); i++) {
                        View rowView = selectedItemsTable.getChildAt(i);
                        if (rowView instanceof TableRow) {
                            TableRow row = (TableRow) rowView;
                            TextView dayTextView = row.findViewById(R.id.dayTextView);
                            TextView fromTimeTextView = row.findViewById(R.id.fromTimeTextView);
                            TextView toTimeTextView = row.findViewById(R.id.toTimeTextView);


                            if (dayTextView != null) {

                                String selectedDay = dayTextView.getText().toString();
                                String selectedStartTime = fromTimeTextView.getText().toString();
                                String selectedEndTime = toTimeTextView.getText().toString();


                                String availability = "Day: " + selectedDay + ", Start Time: " + selectedStartTime + ", End Time: " + selectedEndTime;
                                therapistAvailability.add(availability);
                            }
                        }
                    }
                }
                List<String> therapistDegree = new ArrayList<>();

                if (selectedDegreeTable.getChildCount() != 0) {
                    for (int i = 0; i < selectedDegreeTable.getChildCount(); i++) {
                        View rowView = selectedDegreeTable.getChildAt(i);
                        if (rowView instanceof TableRow) {
                            TableRow row = (TableRow) rowView;
                            TextView Degreetextview = row.findViewById(R.id.degreeTextView);



                            if (Degreetextview != null) {

                                String selectedDegree = Degreetextview.getText().toString();



                                therapistDegree.add(selectedDegree);
                            }
                        }
                    }
                }


                SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                String therapistId = preferences.getString("currentTherapistId", "");
                updateTherapist(therapistId, therapistAvailability, newCharges, therapistDegree);

                List<String> therapistfile = new ArrayList<>();
                if (selectedfileTable.getChildCount() != 0) {
                    for (int i = 0; i < selectedfileTable.getChildCount(); i++) {
                        View rowView = selectedfileTable.getChildAt(i);
                        if (rowView instanceof TableRow) {
                            TableRow row = (TableRow) rowView;
                            TextView filetextview = row.findViewById(R.id.degreeTextView);



                            if (filetextview != null) {

                                String selectedfile = filetextview.getText().toString();



                                therapistfile.add(selectedfile);
                            }
                        }
                    }

                }
                uploadPdfToServer(therapistfile, pdfData);
                Drawable drawable = account_image.getDrawable();


                Bitmap bitmap = getBitmapFromVectorDrawable(drawable);


                Drawable defaultDrawable = getResources().getDrawable(R.drawable.blank_profile_picture_973460);


                Bitmap defaultBitmap = getBitmapFromVectorDrawable(defaultDrawable);


                if (!bitmap.sameAs(defaultBitmap)) {

                    saveImageToDatabase();
                    Log.d("DrawableCheck", "Drawable is not equal to blank_profile_picture_973460");
                } else {
                    deleteImageFromDatabase();

                    Log.d("DrawableCheck", "Drawable is equal to blank_profile_picture_973460");
                }



                // Save the cardList
                saveCard(therapistId, accountList);


            }
            private Bitmap getBitmapFromVectorDrawable(Drawable drawable) {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap; }

        });



        editChargesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEditingCharges) {
                    // Disable the "ADD" button
                    addButton.setEnabled(false);
                    saveButton.setEnabled(false);

                    // Switch to editing mode
                    editChargesIcon.setImageResource(R.drawable.baseline_check_24);
                    chargesEditText.setTextColor(getResources().getColor(android.R.color.black));
                    chargesEditText.setEnabled(true);
                    chargesEditText.setFocusable(true);
                    chargesEditText.setFocusableInTouchMode(true);
                    chargesEditText.requestFocus(); // Request focus to show the cursor
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(chargesEditText, InputMethodManager.SHOW_IMPLICIT); // Show the keyboard
                    isEditingCharges = true;
                } else {
                    // Re-enable the "ADD" button
                    addButton.setEnabled(true);
                    saveButton.setEnabled(true);


                    // Switch back to non-editing mode
                    editChargesIcon.setImageResource(R.drawable.baseline_edit_24);
                    chargesEditText.setEnabled(false);
                    chargesEditText.setFocusable(false);
                    chargesEditText.setFocusableInTouchMode(false);
                    isEditingCharges = false;


                }
            }
        });







        editProfileIcon= root.findViewById(R.id.editProfileIcon);
        editProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Choose Image Source");
                Drawable drawable = account_image.getDrawable();
                Bitmap bitmap = getBitmapFromVectorDrawable(drawable);
                Drawable defaultDrawable = getResources().getDrawable(R.drawable.blank_profile_picture_973460);
                Bitmap defaultBitmap = getBitmapFromVectorDrawable(defaultDrawable);

                String[] options;
                if (!bitmap.sameAs(defaultBitmap)) {
                    options = new String[]{"Gallery", "Camera", "Remove Display Picture"};
                } else {
                    options = new String[]{"Gallery", "Camera"};
                }

                // Create a custom adapter for the dialog options
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.select_dialog_item,
                        android.R.id.text1,
                        options) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = view.findViewById(android.R.id.text1);
                        textView.setTextSize(18);
                        if (textView.getText().toString().equals("Remove Display Picture")) {
                            // Set text color to red for "Remove Display Picture"
                            textView.setTextColor(Color.RED);
                            // Set the bin icon to the left of "Remove Display Picture"
                            Drawable binIcon = getResources().getDrawable(R.drawable.baseline_delete_24_red);
                            textView.setCompoundDrawablesWithIntrinsicBounds(binIcon, null, null, null);
                            textView.setCompoundDrawablePadding(16); // Adjust padding as needed
                        } else {
                            // Set text color to default for other options
                            textView.setTextColor(Color.BLACK);
                            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                        return view;
                    }
                };

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                // Choose from gallery
                                pickImageFromGallery();
                                break;
                            case 1:
                                // Take photo with camera
                                takePhotoWithCamera();
                                break;
                            case 2:
                                // Remove display picture
                                Drawable defaultDrawable = getResources().getDrawable(R.drawable.blank_profile_picture_973460);
                                account_image.setImageDrawable(defaultDrawable);
                                break;
                        }
                    }
                });

                builder.show();
            }

            private Bitmap getBitmapFromVectorDrawable(Drawable drawable) {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }
        });

        getTherapistData();

        return root;
    }
  TherapistAPI therapistAPI;
    private void saveCard(String therapistId, List<AccountData> accountList) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        therapistAPI = retrofit.create(TherapistAPI.class);
        Call<Void> call = therapistAPI.saveCard(therapistId, accountList);
        call.enqueue(new Callback<Void>() {@Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                Log.d("YourTag", "Accounts saved successfully");
                Toast.makeText(requireContext(), "Accounts saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("YourTag", "Failed to save accounts. HTTP response code: " + response.code());
                try {
                    String errorMessage = response.errorBody().string();
                    Log.d("YourTag", "Error message from server: " + errorMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(requireContext(), "Failed to save accounts", Toast.LENGTH_SHORT).show();
            }
        }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Log.d("YourTag", "Error saving accounts: " + t.getMessage());
                Toast.makeText(requireContext(), "Error saving accounts: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }




    private void updateTherapist(String therapistId, List<String> therapistAvailability, String newCharges, List<String> selecteddegree) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);

        // Create a map to hold the data to be sent in the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("newCharges", newCharges);
        requestBody.put("availableTime", therapistAvailability);
        requestBody.put("selectedDegree", selecteddegree);

        Call<ResponseBody> call = therapistAPI.updateTherapistData(therapistId, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    getTherapistDataonsave();
                    //  Toast.makeText(requireContext(), "Therapist data updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful response
                    //       Toast.makeText(requireContext(), "Failed to update therapist data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failure
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Handle PDF selection
            Uri pdfUri = data.getData();
            handlePDF(pdfUri);
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Handle image selection from gallery
            Uri imageUri = data.getData();


            // Set the selected image in the ImageView

            account_image.setImageURI(imageUri);


            // Convert the image to a byte array

        } else if (requestCode == REQUEST_IMAGE_CAPTURE1 && resultCode == Activity.RESULT_OK && data != null) {
            // Handle image capture from camera
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            // Set the photo in the ImageView
            account_image.setImageBitmap(imageBitmap);

            // Convert the image to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            Base64.encodeToString(byteArray, Base64.DEFAULT);

            // Save the image byte array to the database

        }
    }

    private void saveImageToDatabase() {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String therapistId = preferences.getString("currentTherapistId", "");
        Drawable Image = account_image.getDrawable();
        Bitmap ImageBitmap = getBitmapFromDrawable(Image);
        String profileImage = encodeImage(ImageBitmap);

        // Create the request body map
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("TherapistId", therapistId);
        requestBody.put("Image", profileImage);

        // Make the Retrofit call to save the image
        Call<ResponseBody> call = therapistAPI.saveProfilePic(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Image saved successfully
                    //  Toast.makeText(requireContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
                    getTherapistDataonsave();
                } else {
                    // Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failure
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteImageFromDatabase() {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String therapistId = preferences.getString("currentTherapistId", "");

        // Create the request body map
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("TherapistId", therapistId);

        // Make the Retrofit call to delete the image
        Call<ResponseBody> call = therapistAPI.deleteProfilePic(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Image deleted successfully
                    getTherapistDataonsave();
                    // Toast.makeText(requireContext(), "Profile picture deleted successfully", Toast.LENGTH_SHORT).show();
                    // Here you can update your UI or perform any other actions after deletion
                } else {
                    //  Toast.makeText(requireContext(), "Failed to delete profile picture", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failure
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress the image with a lower quality setting
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }



    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void takePhotoWithCamera() {
        // Check if the CAMERA permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is granted, start the camera activity
            startCameraActivity();
        }
    }

    // Method to start the camera activity
    private void startCameraActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Check if the CAMERA permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start the camera activity
                startCameraActivity();
            } else {
                // Permission is denied, show a message or take appropriate action
                Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void handlePDF(Uri pdfUri) {

        String pdfFileName=getFileNameFromUri(pdfUri);


        tablepdf(pdfFileName,pdfUri);
        byte[] pdfData = convertPdfToByteArray(pdfUri);




    }

    private void tablepdf(String pdfFileName, Uri pdfUri) {
        if (isDuplicateRow1(pdfFileName)) {
            Toast.makeText(requireContext(), "Duplicate entry", Toast.LENGTH_SHORT).show();
            return;
        }

        View rowView = getLayoutInflater().inflate(R.layout.degree_row_layout, null);
        TextView degreeTextView = rowView.findViewById(R.id.degreeTextView);
        ImageView deleteIcon = rowView.findViewById(R.id.deleteIcon);
        degreeTextView.setText(pdfFileName);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the parent row from the table
                TableRow parentRow = (TableRow) rowView.getParent();
                if (parentRow != null) {
                    selectedfileTable.removeView(parentRow);
                    int index = selectedfileTable.indexOfChild(parentRow);
                    if (index >= 0 && index < pdfData.size()) {
                        pdfData.remove(index);
                    }
                }


                deleteIcon.setOnClickListener(null);
            }
        });

        TableRow newRow = new TableRow(getContext());
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
        pdfData.add(convertPdfToByteArray(pdfUri));
    }







    private boolean isDuplicateRow1(String selectedDegree) {
        for (int i = 0; i < selectedDegreeTable.getChildCount(); i++) {
            View rowView = selectedDegreeTable.getChildAt(i);
            if (rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                TextView degreeTextView = row.findViewById(R.id.degreeTextView);

                // Null check for degreeTextView
                if (degreeTextView != null) {
                    String existingDegree = degreeTextView.getText().toString();
                    if (existingDegree.equals(selectedDegree)) {
                        return true; // Duplicate row found
                    }
                }
            }
        }
        return false;
    }




    private byte[] convertPdfToByteArray(Uri pdfUri) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(pdfUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            byteArray = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }




    private void uploadPdfToServer(List<String> pdfFileNames, List<byte[]> pdfDataList) {
        // Create a list to hold MultipartBody.Part instances for each PDF file

        if (pdfFileNames.isEmpty() || pdfDataList.isEmpty()) {
            // No files to upload, delete existing files from the database and server
            deletePdfFilesFromServer();
            return;
        }
        List<MultipartBody.Part> pdfParts = new ArrayList<>();

        // Iterate through each PDF file
        for (int i = 0; i < pdfFileNames.size(); i++) {
            // Get the file name and file data for the current PDF
            String pdfFileName = pdfFileNames.get(i);
            byte[] pdfData = pdfDataList.get(i);

            // Create a RequestBody instance for the PDF file data
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), pdfData);

            // Create a MultipartBody.Part instance for the PDF file
            MultipartBody.Part pdfPart = MultipartBody.Part.createFormData("pdf", pdfFileName, requestBody);

            // Add the PDF part to the list
            pdfParts.add(pdfPart);
        }

        // Obtain therapistId from preferences
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String therapistId = preferences.getString("currentTherapistId", "");

        // Create Retrofit instance
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();

        // Create an instance of your Retrofit API interface
        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);

        // Call the endpoint to upload the PDF files
        Call<ResponseBody> call = therapistAPI.uploadMultiplePdfs(therapistId, pdfParts);

        // Execute the network request asynchronously
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    //   Toast.makeText(requireContext(), "PDFs uploaded successfully", Toast.LENGTH_SHORT).show();
                    getTherapistDataonsave();
                } else {
                    // Handle unsuccessful response
                    //   Toast.makeText(requireContext(), "Failed to upload PDFs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failure
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deletePdfFilesFromServer() {
        // Obtain therapistId from preferences
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String therapistId = preferences.getString("currentTherapistId", "");

        // Create Retrofit instance
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();

        // Create an instance of your Retrofit API interface
        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);

        // Call the endpoint to delete the PDF files
        Call<ResponseBody> call = therapistAPI.deletePdfFiles(therapistId);

        // Execute the network request asynchronously
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    //   Toast.makeText(requireContext(), "PDF files deleted successfully", Toast.LENGTH_SHORT).show();
                    getTherapistDataonsave();
                } else {
                    // Handle unsuccessful response
                    //  Toast.makeText(requireContext(), "Failed to delete PDF files", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failure
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void addRowToTable() {
        Spinner daySpinner = binding.daySpinner;
        Spinner startTimeSpinner = binding.startTimeSpinner;
        Spinner endTimeSpinner = binding.endTimeSpinner;

        String selectedDay = daySpinner.getSelectedItem() != null ? daySpinner.getSelectedItem().toString() : "";
        String selectedStartTime = startTimeSpinner.getSelectedItem() != null ? startTimeSpinner.getSelectedItem().toString() : "";
        String selectedEndTime = endTimeSpinner.getSelectedItem() != null ? endTimeSpinner.getSelectedItem().toString() : "";


        // Check if the selected day and time combination already exists in any row
        if (isDuplicateRow(selectedDay, selectedStartTime, selectedEndTime)) {
            // If duplicate row exists, show a message or perform desired action
            Toast.makeText(requireContext(), "Duplicate entry", Toast.LENGTH_SHORT).show();
            return;
        }

        // If no duplicate row exists, proceed to add the new row
        View rowView = getLayoutInflater().inflate(R.layout.row_layout, null);
        TextView dayTextView = rowView.findViewById(R.id.dayTextView);
        TextView fromTimeTextView = rowView.findViewById(R.id.fromTimeTextView);
        TextView toTimeTextView = rowView.findViewById(R.id.toTimeTextView);
        ImageView deleteIcon = rowView.findViewById(R.id.deleteIcon);

        dayTextView.setText(selectedDay);
        fromTimeTextView.setText(selectedStartTime);
        toTimeTextView.setText(selectedEndTime);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the parent row from the table
                if (rowView != null) {
                    TableRow parentRow = (TableRow) rowView.getParent();
                    if (parentRow != null) {
                        selectedItemsTable.removeView(parentRow);
                    }
                }

                deleteIcon.setOnClickListener(null);

            }
        });

        TableRow newRow = new TableRow(getContext());
        newRow.addView(rowView);

        selectedItemsTable.addView(newRow);
    }

    private boolean isDuplicateRow(String selectedDay, String selectedStartTime, String selectedEndTime) {
        for (int i = 0; i < selectedItemsTable.getChildCount(); i++) {
            View rowView = selectedItemsTable.getChildAt(i);
            if (rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                TextView dayTextView = row.findViewById(R.id.dayTextView);
                TextView fromTimeTextView = row.findViewById(R.id.fromTimeTextView);
                TextView toTimeTextView = row.findViewById(R.id.toTimeTextView);

                String existingDay = dayTextView.getText().toString();
                String existingStartTime = fromTimeTextView.getText().toString();
                String existingEndTime = toTimeTextView.getText().toString();

                // Check if the current row has the same day and time combination as the selected one
                if (existingDay.equals(selectedDay) && existingStartTime.equals(selectedStartTime)
                        && existingEndTime.equals(selectedEndTime)) {
                    return true; // Duplicate row found
                }
            }
        }
        return false;
    }

    private byte[] decodeBase64(String base64String) {
        return Base64.decode(base64String, Base64.DEFAULT);
    }
    private void addRowToTableDegree() {
        Spinner degreeSpinner = binding.degreeSpinner;


        String selectedDegree = degreeSpinner.getSelectedItem().toString();



        if (isDuplicateRow(selectedDegree)) {

            Toast.makeText(requireContext(), "Duplicate entry", Toast.LENGTH_SHORT).show();
            return;
        }

        // If no duplicate row exists, proceed to add the new row
        View rowView = getLayoutInflater().inflate(R.layout.degree_row_layout, null);
        TextView degreeTextView = rowView.findViewById(R.id.degreeTextView);

        ImageView deleteIcon = rowView.findViewById(R.id.deleteIcon);

        degreeTextView.setText(selectedDegree);


        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the parent row from the table
                if (rowView != null) {
                    TableRow parentRow = (TableRow) rowView.getParent();
                    if (parentRow != null) {
                        selectedDegreeTable.removeView(parentRow);
                    }
                }
                // Remove the click listener to prevent accessing views from the deleted row
                deleteIcon.setOnClickListener(null);
            }
        });

        TableRow newRow = new TableRow(getContext());
        newRow.addView(rowView);

        selectedDegreeTable.addView(newRow);
    }

    private boolean isDuplicateRow(String selectedDegree) {
        for (int i = 0; i < selectedDegreeTable.getChildCount(); i++) {
            View rowView = selectedDegreeTable.getChildAt(i);
            if (rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                TextView degreeTextView = row.findViewById(R.id.degreeTextView);

                // Null check for degreeTextView
                if (degreeTextView != null) {
                    String existingDegree = degreeTextView.getText().toString();
                    if (existingDegree.equals(selectedDegree)) {
                        return true; // Duplicate row found
                    }
                }
            }
        }
        return false;
    }
    private void OnTypeErrorRemoval2(TextInputEditText cardNumberEditText, TextInputLayout cardNumberInputLayout, TextInputEditText cardnameEditText, TextInputLayout cardnameInputLayout) {
        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                cardNumberInputLayout.setError(null);
                cardNumberInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cardnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the user starts typing
                cardnameInputLayout.setError(null);
                cardnameInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void getTherapistData() {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        if (retrofit == null) {
            // Handle null Retrofit instance
            return;
        }

        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);
        if (getActivity() == null || getContext() == null) {
            // Fragment not attached to an activity, cannot proceed
            return;
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String therapistId = preferences.getString("currentTherapistId", "");

        Call<Therapistprofiledata> call = therapistAPI.getTherapistData(therapistId);
        call.enqueue(new Callback<Therapistprofiledata>() {
            @Override
            public void onResponse(Call<Therapistprofiledata> call, Response<Therapistprofiledata> response) {
                if (response.isSuccessful()) {
                    Therapistprofiledata therapist = response.body();
                    if (therapist != null) {
                        // Populate UI fields with therapist data
                        populateTherapistData(therapist);
                        int nonNullFieldCount = therapist.countNonNullFields();
                        ProgressBar horizontalProgressBar= binding.horizontalProgressBar;
                        horizontalProgressBar.setProgress(nonNullFieldCount);
                    }
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(requireContext(), "Failed to retrieve therapist data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Therapistprofiledata> call, Throwable t) {
                // Handle network failure
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void populateTherapistData(Therapistprofiledata therapist) {
        if (therapist != null) {
            if (chargesEditText != null && therapist.getCharges() != null) {
                chargesEditText.setText(therapist.getCharges());
            }



//            if (pdfNameTextView != null && therapist.getOriginalPdfName() != null) {
//                pdfNameTextView.setText(therapist.getOriginalPdfName());
//            }

            if (account_image != null && therapist.getImage() != null) {
                byte[] decodedImage = Base64.decode(therapist.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                account_image.setImageBitmap(bitmap);
            }

            List<String> availableTimes = therapist.getAvailableTime();
            if (availableTimes != null) {
                for (String availableTime : availableTimes) {
                    addRowToTable(availableTime);
                }
            }
            List<String> availableDegree = therapist.getDegree();
            if (availableDegree != null) {
                for (String availableDegree1 : availableDegree) {
                    addfetcheddegree(availableDegree1);
                }
            }

            List<PdfFile> pdfFiles = therapist.getPdfFiles();
            if (pdfFiles != null) {
                for (PdfFile pdfFile : pdfFiles) {
                    String originalPdfName = pdfFile.getOriginalPdfName();

                    addfetchedfile(originalPdfName);
                    byte[] byteArray   =decodeBase64(pdfFile.getPdfData());

                    pdfData.add(byteArray);
                }
            }
            List<AccountData> accountData = therapist.getAccountList();
            if (accountData != null) {
                for (AccountData accountData1 : accountData) {
                    String lastFourDigits = accountData1.getNumber().substring(accountData1.getNumber().length() - 4);
                    onUpdateCardNumber(lastFourDigits, accountData1);
                }
            }
        }
    }



    private void addfetchedfile(String availablefile1) {
        View rowView = getLayoutInflater().inflate(R.layout.degree_row_layout, null);

        // Find views in the row layout
        TextView degreeTextView = rowView.findViewById(R.id.degreeTextView);

        ImageView deleteIcon = rowView.findViewById(R.id.deleteIcon);


        degreeTextView.setText(availablefile1);


        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the parent row from the table
                ViewGroup parent = (ViewGroup) rowView.getParent();
                parent.removeView(rowView);
            }
        });

        // Add the row to the table
        TableRow newRow = new TableRow(getContext());
        newRow.addView(rowView);

        degreeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "No PDF viewer app installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle error: unable to create temporary PDF file
            Toast.makeText(requireContext(), "Unable to open PDF", Toast.LENGTH_SHORT).show();
        }
    }



    private Uri createTempPdfFile(byte[] pdfData) {
        try {
            File pdfFile = File.createTempFile("temp_pdf", ".pdf", requireContext().getCacheDir());
            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write(pdfData);
            fos.close();
            return FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }










    private void addfetcheddegree(String availableDegree1) {
        View rowView = getLayoutInflater().inflate(R.layout.degree_row_layout, null);

        // Find views in the row layout
        TextView degreeTextView = rowView.findViewById(R.id.degreeTextView);

        ImageView deleteIcon = rowView.findViewById(R.id.deleteIcon);


        degreeTextView.setText(availableDegree1);


        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the parent row from the table
                if (rowView != null) {
                    TableRow parentRow = (TableRow) rowView.getParent();
                    if (parentRow != null) {
                        selectedDegreeTable.removeView(parentRow);
                    }
                }

                deleteIcon.setOnClickListener(null);
            } });

        // Add the row to the table
        TableRow newRow = new TableRow(getContext());
        newRow.addView(rowView);
        selectedDegreeTable.addView(newRow);
    }


    private void addRowToTable(String availableTime) {
        // Inflate the row layout
        View rowView = getLayoutInflater().inflate(R.layout.row_layout, null);

        // Find views in the row layout
        TextView dayTextView = rowView.findViewById(R.id.dayTextView);
        TextView fromTimeTextView = rowView.findViewById(R.id.fromTimeTextView);
        TextView toTimeTextView = rowView.findViewById(R.id.toTimeTextView);
        ImageView deleteIcon = rowView.findViewById(R.id.deleteIcon);

        // Extract day, start time, and end time from the availableTime string
        String[] parts = availableTime.split(", ");
        String day = parts[0].substring(parts[0].indexOf(": ") + 2);
        String startTime = parts[1].substring(parts[1].indexOf(": ") + 2);
        String endTime = parts[2].substring(parts[2].indexOf(": ") + 2);

        // Set the values in the row views
        dayTextView.setText(day);
        fromTimeTextView.setText(startTime);
        toTimeTextView.setText(endTime);

        // Set click listener for delete icon
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the parent row from the table
                if (rowView != null) {
                    TableRow parentRow = (TableRow) rowView.getParent();
                    if (parentRow != null) {
                        selectedItemsTable.removeView(parentRow);
                    }
                }

                deleteIcon.setOnClickListener(null);
            }

        });

        // Add the row to the table
        TableRow newRow = new TableRow(getContext());
        newRow.addView(rowView);
        selectedItemsTable.addView(newRow);
    }
    private void getTherapistDataonsave() {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        if (retrofit == null) {
            // Handle null Retrofit instance
            return;
        }

        TherapistAPI therapistAPI = retrofit.create(TherapistAPI.class);
        if (getActivity() == null || getContext() == null) {
            // Fragment not attached to an activity, cannot proceed
            return;
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String therapistId = preferences.getString("currentTherapistId", "");

        Call<Therapistprofiledata> call = therapistAPI.getTherapistData(therapistId);
        call.enqueue(new Callback<Therapistprofiledata>() {
            @Override
            public void onResponse(Call<Therapistprofiledata> call, Response<Therapistprofiledata> response) {
                if (response.isSuccessful()) {
                    Therapistprofiledata therapist = response.body();
                    if (therapist != null) {
                        // Populate UI fields with therapist data

                        int nonNullFieldCount = therapist.countNonNullFields();
                        ProgressBar horizontalProgressBar= binding.horizontalProgressBar;
                        horizontalProgressBar.setProgress(nonNullFieldCount);
                    }
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(requireContext(), "Failed to retrieve therapist data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Therapistprofiledata> call, Throwable t) {
                // Handle network failure
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



//    public void updateCardNumber(String cardNumber, CardData cardData) {
//
//
//        View view = getLayoutInflater().inflate(R.layout.account_row_layout, null);
//        linearLayout1.addView(view);
//
//
//        // Find the TextView and ImageView in the inflated view
//        TextView cardNumberTextView = view.findViewById(R.id.cardnumbertextview);
//        ImageView icon = view.findViewById(R.id.deleteiconarrow);
//        Drawable drawable = getResources().getDrawable(R.drawable.baseline_delete_24);
//
//        drawable = DrawableCompat.wrap(drawable);
//        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.pomp_power));
//
//        // Set the tinted drawable as the source for the ImageView
//        icon.setImageDrawable(drawable);
//        icon.setImageResource(R.drawable.baseline_delete_24);
//
//        // Set the card number to the TextView
//        cardNumberTextView.setText("**** **** **** " + cardNumber);
//
//        // Associate the CardData object with the view
//        view.setTag(cardData);
//
//
//
//        // Set click listener for the icon
//        icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get the CardData associated with the clicked view
//                CardData clickedCard = (CardData) view.getTag();
//
//                // Remove the clicked CardData from the cardList
//                cardList.remove(clickedCard);
//
//                // Remove the view from the layout
//                linearLayout.removeView(view);
//            }
//        });
//
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Determine the position of the clicked view in the linearLayout
//                int position = linearLayout.indexOfChild(view);
//
//                CardData cardData = cardList.get(position);
//
//                BottomAddCardEdit bottomSheetFragment = new BottomAddCardEdit(view, cardData, cardData.getCardNumber(), cardData.getCardname(),
//                        cardData.getCvv(), cardData.getMonth(), cardData.getYear());
//
//                // Set the edit listener
//                bottomSheetFragment.setEditListener(Patient_payment_setting.this);
//
//                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
//
//            }
//        });
//
//        // Add the CardData to the cardList
//        cardList.add(cardData);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    public void onUpdateCardNumber(String lastFourDigits, AccountData accountData) {
        // Check if there is already a row added
        if(linearLayout1.getChildCount() > 0) {
            // If a row is already added, show a toast message and return
            Toast.makeText(getContext(), "Cant add multiple accounts", Toast.LENGTH_SHORT).show();
            return;
        }

        // If no row is added, proceed to add the row
        View view = getLayoutInflater().inflate(R.layout.account_row_layout, null);
        linearLayout1.addView(view);

        TextView cardNumberTextView = view.findViewById(R.id.accountnum);
        TextView cardNameTextView = view.findViewById(R.id.accountname);
        ImageView icon = view.findViewById(R.id.deleteIcon);
        Drawable drawable = getResources().getDrawable(R.drawable.baseline_delete_24);

        drawable = DrawableCompat.wrap(drawable);

        // Set the tinted drawable as the source for the ImageView
        icon.setImageDrawable(drawable);
        icon.setImageResource(R.drawable.baseline_delete_24);

        // Set the card number to the TextView
        cardNumberTextView.setText("**** **** **** " + lastFourDigits);
        cardNameTextView.setText(accountData.getName());

        // Associate the CardData object with the view
        view.setTag(accountData);

        // Set click listener for the icon
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the CardData associated with the clicked view
                AccountData clickedCard = (AccountData) view.getTag();

                // Remove the clicked CardData from the cardList
                accountList.remove(clickedCard);

                // Remove the view from the layout
                linearLayout1.removeView(view);
            }
        });

        accountList.add(accountData);
    }
}
