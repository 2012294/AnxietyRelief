package com.hareem.anxietyrelief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Theme_Affirmation_Activity extends AppCompatActivity {
    LinearLayout default1;
    private static final String TAG = "ThemeActivity1";
    ImageView wallpaper;
    LinearLayout textcolor;
    LinearLayout background;
    CardView colortab;
    TextView quote;
    View activebackground,activetextcolor,activedefault;
    private static final String PREF_THEME_DATA = "theme_data";
    private static final String KEY_WALLPAPER = "wallpaper";
    private static final String KEY_TEXT_COLOR = "text_color";


    private static final int REQUEST_IMAGE_PICKER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_affirmation);
        default1=findViewById(R.id.default1);
        wallpaper=findViewById(R.id.wallpaper);
        textcolor=findViewById(R.id.textcolor);
        background=findViewById(R.id.background);
        colortab=findViewById(R.id.colortab);
        quote=findViewById(R.id.quote);
        activebackground=findViewById(R.id.activebackground);
        activetextcolor=findViewById(R.id.activetextcolor);
        activedefault=findViewById(R.id.activedefault);
        Button cancel=findViewById(R.id.cancel);
        Button save=findViewById(R.id.save);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fetchThemeInfo();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                String patientId = preferences.getString("currentPatientId", "");

                int textColor = quote.getCurrentTextColor(); // Get text color as int

                // Check if the wallpaper is set and is a BitmapDrawable
                Drawable wallpaperDrawable = wallpaper.getDrawable();

                if (wallpaperDrawable != null) {
                    Bitmap wallpaperBitmap = getBitmapFromDrawable(wallpaperDrawable);
                    String wallpaperImage = encodeImage(wallpaperBitmap);

                    // Create a JSON object to send to the server
                    JsonObject themeObject = new JsonObject();
                    themeObject.addProperty("patientId", patientId);
                    themeObject.addProperty("wallpaperImage", wallpaperImage);
                    themeObject.addProperty("textColor", textColor);

                    // Use RetrofitClientInstance to create the PatientAPI service
                    PatientAPI patientAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);

                    // Make a POST request to save the theme on the server
                    Call<Void> call = patientAPI.saveTheme(themeObject);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                SharedPreferences themePreferences = getSharedPreferences(PREF_THEME_DATA, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = themePreferences.edit();
                                editor.putString(KEY_WALLPAPER, wallpaperImage);
                                editor.putInt(KEY_TEXT_COLOR, textColor);
                                editor.apply();

                                Toast.makeText(Theme_Affirmation_Activity.this, "Theme saved successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(Theme_Affirmation_Activity.this, "no changes", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("ThemeActivity2", "Error saving theme", t);
                            Toast.makeText(Theme_Affirmation_Activity.this, "Failed to save theme", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }


            // Method to convert Drawable to Bitmap
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

            // Method to convert Bitmap to Base64
            private String encodeImage(Bitmap bitmap) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                // Compress the image with a lower quality setting
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
        });



        default1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quote.setTextColor(ContextCompat.getColor(Theme_Affirmation_Activity.this, R.color.black));
                wallpaper.setImageResource(R.drawable.affirmation_default_background);
                activedefault.setVisibility(View.VISIBLE);
                activetextcolor.setVisibility(View.GONE);
                activebackground.setVisibility(View.GONE);
                colortab.setVisibility(View.GONE);
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activedefault.setVisibility(View.GONE);
                activetextcolor.setVisibility(View.GONE);
                activebackground.setVisibility(View.VISIBLE);
                colortab.setVisibility(View.GONE);
                dispatchPickImageIntent();

            }
        });

        textcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activedefault.setVisibility(View.GONE);
                activetextcolor.setVisibility(View.VISIBLE);
                activebackground.setVisibility(View.GONE);
                colortab.setVisibility(View.VISIBLE);


            }
        });
        setupCircularImageViewColors();
    }
    private void setCircularImageViewColor(ImageView imageView, int colorResourceId) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(ContextCompat.getColor(this, colorResourceId));
        imageView.setBackground(shapeDrawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                quote.setTextColor(ContextCompat.getColor(Theme_Affirmation_Activity.this, colorResourceId));
            }
        });
    }
    private void setupCircularImageViewColors() {
        ImageView colorRed = findViewById(R.id.red);
        setCircularImageViewColor(colorRed, R.color.red);
        ImageView colorGreen = findViewById(R.id.green);
        setCircularImageViewColor(colorGreen, R.color.green);
        ImageView colorBlue = findViewById(R.id.blue);
        setCircularImageViewColor(colorBlue, R.color.blue);
        ImageView colorYellow = findViewById(R.id.yellow);
        setCircularImageViewColor(colorYellow, R.color.yellow);
        ImageView colorBlack = findViewById(R.id.black);
        setCircularImageViewColor(colorBlack, R.color.black);
        ImageView colorWhite = findViewById(R.id.white);
        setCircularImageViewColor(colorWhite, R.color.white);
        ImageView colorLightPink = findViewById(R.id.lightpink);
        setCircularImageViewColor(colorLightPink, R.color.light_pink);
        ImageView colorMintGreen = findViewById(R.id.mintgreen);
        setCircularImageViewColor(colorMintGreen, R.color.mint_green1);
        ImageView colorSkyBlue = findViewById(R.id.skyblue);
        setCircularImageViewColor(colorSkyBlue, R.color.sky_blue);
        ImageView colorLavender = findViewById(R.id.lavender);
        setCircularImageViewColor(colorLavender, R.color.lavender);
        ImageView colorPeach = findViewById(R.id.peach);
        setCircularImageViewColor(colorPeach, R.color.peach);
        ImageView colorGray = findViewById(R.id.grey);
        setCircularImageViewColor(colorGray, R.color.gray);
        ImageView colorBeige = findViewById(R.id.beige);
        setCircularImageViewColor(colorBeige, R.color.beige);
        ImageView colorTaupe = findViewById(R.id.taupe);
        setCircularImageViewColor(colorTaupe, R.color.taupe);
        ImageView colorIvory = findViewById(R.id.ivory);
        setCircularImageViewColor(colorIvory, R.color.ivory);
        ImageView colorCharcoal = findViewById(R.id.charcoal);
        setCircularImageViewColor(colorCharcoal, R.color.charcoal);
        ImageView colorOrange = findViewById(R.id.orange);
        setCircularImageViewColor(colorOrange, R.color.orange);
        ImageView colorTurquoise = findViewById(R.id.turquoise);
        setCircularImageViewColor(colorTurquoise, R.color.turquoise);
        ImageView colorMagenta = findViewById(R.id.magenta);
        setCircularImageViewColor(colorMagenta, R.color.magenta);
        ImageView colorLimeGreen = findViewById(R.id.limegreen);
        setCircularImageViewColor(colorLimeGreen, R.color.lime_green);
        ImageView colorCyan = findViewById(R.id.cyan);
        setCircularImageViewColor(colorCyan, R.color.cyan);



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICKER) {
                Uri selectedImageUri = data.getData();
                wallpaper.setImageURI(selectedImageUri);
            }
        }

    }

    void dispatchPickImageIntent() {

        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICKER);

    }






    private void fetchThemeInfo() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String currentPatientId = preferences.getString("currentPatientId", "");
        SharedPreferences themePreferences = getSharedPreferences(PREF_THEME_DATA, Context.MODE_PRIVATE);
        String cachedWallpaper = themePreferences.getString(KEY_WALLPAPER, "");
        int cachedTextColor = themePreferences.getInt(KEY_TEXT_COLOR, 0);
        if (!cachedWallpaper.isEmpty() && cachedTextColor != 0) {
            // Use cached data
            byte[] decodedImage = Base64.decode(cachedWallpaper, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            wallpaper.setImageBitmap(bitmap);
            quote.setTextColor(cachedTextColor);
            return;
        }

        // If no cached data, make the network call
        PatientAPI themeAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);
        Call<ThemeResponse> call = themeAPI.getTheme(currentPatientId);

        call.enqueue(new Callback<ThemeResponse>() {
            @Override
            public void onResponse(Call<ThemeResponse> call, Response<ThemeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle the successful response
                    ThemeResponse themeResponse = response.body();

                    // Access the wallpaper image and text color from themeResponse
                    String wallpaperImage = themeResponse.getWallpaperImage();
                    int textColor = themeResponse.getTextColor();



                    // Set the wallpaper image and text color
                    byte[] decodedImage = Base64.decode(wallpaperImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                    wallpaper.setImageBitmap(bitmap);
                    quote.setTextColor(textColor);
                    SharedPreferences themePreferences = getSharedPreferences(PREF_THEME_DATA, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = themePreferences.edit();
                    editor.putString(KEY_WALLPAPER, wallpaperImage);
                    editor.putInt(KEY_TEXT_COLOR, textColor);
                    editor.apply();

                    // Notify the adapter that data has changed
                } else {
                    // Handle unsuccessful response (e.g., theme not found or other server error)
                    Log.e("Theme Retrieval", "Failed to retrieve theme: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ThemeResponse> call, Throwable t) {
                // Handle network or unexpected errors
                Log.e("Theme Retrieval", "Network request failed: " + t.getMessage());
            }
        });
    }




}



