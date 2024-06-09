package com.hareem.anxietyrelief;


import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;


public class EqualBreathingExercise extends AppCompatActivity {

    private TextView countdownTextView;
    private TextView relaxationMessageTextView;
    private CardView stopCardView;
    private CardView replayCardView;
    private MediaPlayer mediaPlayer;
    private MediaPlayer relaxationAudio;
    private MediaPlayer breatheInAudio;
    private MediaPlayer breatheOutAudio;
    private MediaPlayer wellDoneAudio;
    private CountDownTimer countDownTimer;

    private TextView textCycles;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equal_breathing_exercise);

        countdownTextView = findViewById(R.id.countdownTextView);
        relaxationMessageTextView = findViewById(R.id.relaxationMessageTextView);
        stopCardView = findViewById(R.id.stopCardView);
        replayCardView = findViewById(R.id.replayCardView);


        SharedPreferences sharedPreferences = this.getSharedPreferences("EqualBreathingCounter", Context.MODE_PRIVATE);
        int counterValue = sharedPreferences.getInt("EqualBreathingCounterValue", 2);

        SharedPreferences musicPreferences = this.getSharedPreferences("Music", Context.MODE_PRIVATE);
        int music = musicPreferences.getInt("Music", 1);


        if (music == 1) {
            mediaPlayer = MediaPlayer.create(this, R.raw.rain_sound);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());
            mediaPlayer.start();
        } else if (music == 2) {
            mediaPlayer = MediaPlayer.create(this, R.raw.oceanwaves);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());
            mediaPlayer.start();
        }  else if (music == 3 ) {
            mediaPlayer = MediaPlayer.create(this, R.raw.jungle_chorus);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());
            mediaPlayer.start();
        }   else if (music == 4 ) {
            mediaPlayer = MediaPlayer.create(this, R.raw.whispering_falls);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());
            mediaPlayer.start();
        }

        startCountdown(5000, counterValue);

        relaxationAudio = MediaPlayer.create(this, R.raw.relaxation_audio);
        breatheInAudio = MediaPlayer.create(this, R.raw.breathe_in_audio);
        breatheOutAudio = MediaPlayer.create(this, R.raw.breathe_out_audio);
        wellDoneAudio = MediaPlayer.create(this, R.raw.well_done_audio);
        textCycles = findViewById(R.id.TextCycles);

        stopCardView.setOnClickListener(v -> finish());

        replayCardView.setOnClickListener(v -> {
            resetUI();
            startCountdown(5000, counterValue);
        });
    }

    private void resetUI() {
        countdownTextView.setVisibility(View.VISIBLE);
        relaxationMessageTextView.setVisibility(View.GONE);
        replayCardView.setVisibility(View.GONE);

        // Stop and release the mediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, R.raw.rain_sound);
            mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());
            mediaPlayer.start();
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Show the stop button and hide the replay button
        stopCardView.setVisibility(View.VISIBLE);
    }

    private void startCountdown(long millisInFuture, int numBreathingCycles) {
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                if (secondsRemaining > 0) {
                    countdownTextView.setText(String.valueOf(secondsRemaining));
                }
            }

            @Override
            public void onFinish() {
                countdownTextView.setVisibility(View.GONE);
                relaxationMessageTextView.setVisibility(View.VISIBLE);
                relaxationMessageTextView.setText("Relax and get comfortable");
                relaxationAudio.start();

                new Handler().postDelayed(() -> relaxationMessageTextView.setText("Focus on your breathing"), 5000);
                new Handler().postDelayed(() -> startBreathingCycle(1,numBreathingCycles), 15000);
            }
        };

        countDownTimer.start();
    }

    private void startBreathingCycle(int currentCycle, int remainingCycles) {
        textCycles.setVisibility(View.VISIBLE);
        textCycles.setText("Cycle number: " + currentCycle);

        breatheInAudio.start();
        relaxationMessageTextView.setText("Breathe In");
        countdownTextView.setVisibility(View.VISIBLE);

        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                countdownTextView.setText(String.valueOf(secondsRemaining + 1));
            }

            @Override
            public void onFinish() {
                breatheOutAudio.start();
                relaxationMessageTextView.setText("Breathe Out");

                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int secondsRemaining = (int) (millisUntilFinished / 1000);
                        countdownTextView.setText(String.valueOf(secondsRemaining + 1));
                    }

                    @Override
                    public void onFinish() {
                        // Check if there are remaining cycles
                        if (remainingCycles > 1) {
                            // Run the next breathing cycle
                            new Handler().postDelayed(() -> startBreathingCycle(currentCycle + 1, remainingCycles - 1), 0);
                        } else {
                            // Notify the activity that all breathing cycles have finished
                            handleBreathingCyclesCompleted();
                        }
                    }
                }.start();
            }
        }.start();
    }


    private void handleBreathingCyclesCompleted() {
        textCycles.setVisibility(View.GONE);
        countdownTextView.setVisibility(View.GONE);
        relaxationMessageTextView.setText("Well done");


        if (wellDoneAudio != null) {
            wellDoneAudio.start();
        }

        // Hide the stop button and show the replay button
        stopCardView.setVisibility(View.GONE);
        replayCardView.setVisibility(View.VISIBLE);
        replayCardView.setCardBackgroundColor(ContextCompat.getColor(EqualBreathingExercise.this, R.color.mint_blue_green));
        ((ImageView) replayCardView.findViewById(R.id.replayIcon)).setImageResource(R.drawable.baseline_replay_24);
        ((TextView) replayCardView.findViewById(R.id.replayText)).setText("Replay");

        // Stop the rain sound after the last breathing cycle
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (relaxationAudio != null) {
            relaxationAudio.stop();
        }
        if (breatheInAudio != null) {
            breatheInAudio.stop();
        }
        if (breatheOutAudio != null) {
            breatheOutAudio.stop();
        }
        if (wellDoneAudio != null) {
            wellDoneAudio.stop();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        finish();
    }
}
