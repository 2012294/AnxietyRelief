package com.hareem.anxietyrelief;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreathingExerciseMusic extends AppCompatActivity {

    private ImageView toggleIcon1;
    private ImageView toggleIcon2;
    private ImageView toggleIcon3;
    private ImageView toggleIcon4;
    private MediaPlayer mediaPlayer;

    private boolean isPlaying = false;
    private int selectedMusic = 1;
    private Handler handler = new Handler();
    private Runnable stopMediaPlayerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing_exercise_music);

        toggleIcon1 = findViewById(R.id.toggleIcon1);
        toggleIcon2 = findViewById(R.id.toggleIcon2);
        toggleIcon3 = findViewById(R.id.toggleIcon3);
        toggleIcon4 = findViewById(R.id.toggleIcon4);


        int music = getIntent().getIntExtra("music", 1);
        setToggleIcon(music);

        Button save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMusicValue(selectedMusic);
                SharedPreferences.Editor editor4 = BreathingExerciseMusic.this.getSharedPreferences("Music", Context.MODE_PRIVATE).edit();
                editor4.putInt("Music", selectedMusic);
                editor4.apply();
                finish();
            }
        });

        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        stopMediaPlayerRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    isPlaying = false;
                }
            }
        };

        CardView RainfallCardview = findViewById(R.id.rainfallcardview);
        RainfallCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic(R.raw.rain_sound);
                setToggleIcon(1);
                selectedMusic = 1;
            }
        });

        CardView OceanCardview = findViewById(R.id.OceanCardview);
        OceanCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic(R.raw.oceanwaves);
                setToggleIcon(2);
                selectedMusic = 2;
            }
        });

        CardView RainforestBirdsCardview = findViewById(R.id.RainforestBirsdsCardview);
        RainforestBirdsCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic(R.raw.jungle_chorus);
                setToggleIcon(3);
                selectedMusic = 3;
            }
        });
        CardView WaterfallCardview = findViewById(R.id.waterfallCardview);
        WaterfallCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic(R.raw.whispering_falls);
                setToggleIcon(4);
                selectedMusic = 4;
            }
        });
    }
    private void setToggleIcon(int music) {
        switch (music) {
            case 1:
                toggleIcon1.setImageResource(R.drawable.baseline_circle_24);
                toggleIcon2.setImageResource(R.drawable.outline_circle_24);
                toggleIcon3.setImageResource(R.drawable.outline_circle_24);
                toggleIcon4.setImageResource(R.drawable.outline_circle_24);
                break;
            case 2:
                toggleIcon1.setImageResource(R.drawable.outline_circle_24);
                toggleIcon2.setImageResource(R.drawable.baseline_circle_24);
                toggleIcon3.setImageResource(R.drawable.outline_circle_24);
                toggleIcon4.setImageResource(R.drawable.outline_circle_24);
                break;
            case 3:
                toggleIcon1.setImageResource(R.drawable.outline_circle_24);
                toggleIcon2.setImageResource(R.drawable.outline_circle_24);
                toggleIcon3.setImageResource(R.drawable.baseline_circle_24);
                toggleIcon4.setImageResource(R.drawable.outline_circle_24);
                break;
            case 4:
                toggleIcon1.setImageResource(R.drawable.outline_circle_24);
                toggleIcon2.setImageResource(R.drawable.outline_circle_24);
                toggleIcon3.setImageResource(R.drawable.outline_circle_24);
                toggleIcon4.setImageResource(R.drawable.baseline_circle_24);
                break;
        }
    }

    private void playMusic(int musicResource) {
        // Cancel any existing playback
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Start playing the new audio
        mediaPlayer = MediaPlayer.create(this, musicResource);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.release();
            mediaPlayer = null;
        });

        // Ensure audio starts from the beginning
        mediaPlayer.seekTo(0);

        // Start playback
        mediaPlayer.start();

        // Cancel any existing timer
        handler.removeCallbacksAndMessages(null);

        // Schedule stopping after 10 seconds
        handler.postDelayed(() -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }, 10000); // 10 seconds delay
    }



    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(stopMediaPlayerRunnable);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updateMusicValue(int newMusicValue) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String patientId = preferences.getString("currentPatientId", "");

        BreathingExercisesAPI breathingExercisesAPI = RetrofitClientInstance.getRetrofitInstance().create(BreathingExercisesAPI.class);

        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("music", newMusicValue);

        Call<Void> call = breathingExercisesAPI.updateMusicValue(patientId, requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful update
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
