package com.hareem.anxietyrelief.Adapter;

import static android.opengl.ETC1.encodeImage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonObject;
import com.hareem.anxietyrelief.Affirmations;
import com.hareem.anxietyrelief.PatientAPI;
import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.RetrofitClientInstance;
import com.hareem.anxietyrelief.ThemeResponse;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter_Affirmation extends RecyclerView.Adapter<Adapter_Affirmation.viewholder> {

    private Context context;

    private ArrayList<Affirmations> affirmations;


    private ThemeResponse themeResponse;


    public Adapter_Affirmation(Context context, ArrayList<Affirmations> affirmations) {
        this.context = context;
        this.affirmations = affirmations;
        FetchTheme();


    }

    public void FetchTheme(){
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String currentPatientId = preferences.getString("currentPatientId", "");
        PatientAPI themeAPI = RetrofitClientInstance.getRetrofitInstance().create(PatientAPI.class);
        Call<ThemeResponse> call = themeAPI.getTheme(currentPatientId);
        call.enqueue(new Callback<ThemeResponse>() {
            @Override
            public void onResponse(Call<ThemeResponse> call, Response<ThemeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    themeResponse = response.body();



                } else {
                    // Handle unsuccessful response
                    Log.e("Theme Retrieval", "Failed to retrieve theme: " + response.message());
                    themeResponse = new ThemeResponse();

                    // Set default values or imitate the response
                    Drawable wallpaperDrawable1 = ContextCompat.getDrawable(context, R.drawable.affirmation_default_background);
                    Bitmap wallpaperBitmap = getBitmapFromDrawable(wallpaperDrawable1);
                    String wallpaperImage = encodeImage(wallpaperBitmap);
                    themeResponse.setWallpaperImage(wallpaperImage);
                    themeResponse.setTextColor(-16777216);
                }

                notifyDataSetChanged(); // Notify adapter of the theme change
            }




            @Override
            public void onFailure(Call<ThemeResponse> call, Throwable t) {
                // Handle network or unexpected errors
                Log.e("Theme Retrieval", "Network request failed: " + t.getMessage());



                notifyDataSetChanged();
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


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.affirmation_row,parent,false);


        return new Adapter_Affirmation.viewholder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {
        final Affirmations currentAffirmation = affirmations.get(position);
        holder.quote.setVisibility(View.GONE);
        holder.favoriteIcon.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE);

        if (themeResponse!=null){
            String wallpaperImage = themeResponse.getWallpaperImage();
            byte[] decodedImage = Base64.decode(wallpaperImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            holder.background.setImageBitmap(bitmap);
            int textColor = themeResponse.getTextColor();
            holder.quote.setTextColor(textColor);
            holder.favoriteIcon.setColorFilter(textColor);
            holder.quote.setText(affirmations.get(position).getQuote());
            holder.quote.setVisibility(View.VISIBLE);
            holder.favoriteIcon.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);

        }else{

        }

        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String currentPatientId = preferences.getString("currentPatientId", "");

        RetrofitClientInstance.getRetrofitInstance()
                .create(PatientAPI.class)
                .getFavoriteStatus(currentPatientId, currentAffirmation.getId())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject responseBody = response.body();

                            // Extract the 'isFavorite' boolean from the response
                            boolean isFavorite = responseBody.getAsJsonPrimitive("isFavorite").getAsBoolean();

                            // Set the heart icon based on the loaded favorite status
                            if (isFavorite) {
                                holder.favoriteIcon.setImageResource(R.drawable.baseline_favorite_24);
                            } else {
                                holder.favoriteIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                            }

                            holder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Toggle the favorite status
                                    boolean newFavoriteStatus = !currentAffirmation.isFavorite();
                                    currentAffirmation.setFavorite(newFavoriteStatus);

                                    // Update UI based on the new favorite status
                                    if (newFavoriteStatus) {
                                        holder.favoriteIcon.setImageResource(R.drawable.baseline_favorite_24);
                                        saveToDatabase(currentPatientId, currentAffirmation.getId(), newFavoriteStatus, currentAffirmation.getQuote());
                                    } else {
                                        holder.favoriteIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                                        deleteFromDatabase(currentPatientId, currentAffirmation.getId());
                                    }
                                }
                            });
                        } else {
                            Log.e("Adapter_Affirmation", "Failed to get favorite status: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        Log.e("Adapter_Affirmation", "Network request failed: " + t.getMessage());
                    }
                });



        holder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavorite = !currentAffirmation.isFavorite();
                currentAffirmation.setFavorite(isFavorite);


                // Update UI based on the favorite status
                if (isFavorite) {
                    holder.favoriteIcon.setImageResource(R.drawable.baseline_favorite_24);
                    saveToDatabase( currentPatientId,currentAffirmation.getId(), isFavorite,currentAffirmation.getQuote());
                } else {
                    holder.favoriteIcon.setImageResource(R.drawable.baseline_favorite_border_24);

                    deleteFromDatabase( currentPatientId,currentAffirmation.getId());



                }




            }
        });
    }



    private void deleteFromDatabase(String currentPatientId, String affirmationId) {
        Affirmations affirmations = new Affirmations();
        affirmations.setPatientid(currentPatientId);
        affirmations.setId(affirmationId);

        RetrofitClientInstance.getRetrofitInstance()
                .create(PatientAPI.class)
                .deleteFavoriteStatus(affirmations)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("Adapter_Affirmation", "Favorite status deleted successfully");
                        } else {
                            Log.e("Adapter_Affirmation", "Failed to delete favorite status: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e("Adapter_Affirmation", "Network request failed: " + t.getMessage());
                    }
                });
    }




    public void saveToDatabase(String patientId, String affirmationId, boolean isFavorite, String quote) {
        RetrofitClientInstance.getRetrofitInstance()
                .create(PatientAPI.class)
                .saveFavoriteStatus(new Affirmations(patientId, affirmationId, isFavorite, quote))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("Adapter_Affirmation", "Favorite status saved successfully");
                        } else {
                            Log.e("Adapter_Affirmation", "Failed to save favorite status: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e("Adapter_Affirmation", "Network request failed: " + t.getMessage());
                    }
                });
    }





    @Override
    public int getItemCount() {
// return   Integer.MAX_VALUE;
        return  affirmations.size();
    }



    public static class viewholder extends RecyclerView.ViewHolder {

        TextView quote;
        ImageView favoriteIcon;
        ImageView background;
        ProgressBar progressBar;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            quote = itemView.findViewById(R.id.quote);
            favoriteIcon = itemView.findViewById(R.id.fav);
            background=itemView.findViewById(R.id.background);
            progressBar=itemView.findViewById(R.id.progressBar);

        }
    }
}