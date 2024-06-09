package com.hareem.anxietyrelief;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String selectedAffirmation = AffirmationManager.getNewAffirmation(context);
        Intent splashIntent = new Intent(context, SplashActivity.class);
        splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, splashIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        Log.d("Notification1", "Notification triggered.");
        // Build the notification with the selected affirmation message
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "affirmation_channel")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Daily Affirmation")
                .setContentText(selectedAffirmation)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        SharedPreferences sharedPreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        Boolean loginPatient = sharedPreferences.getBoolean("patient_login", false);

        if (loginPatient) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling ActivityCompat#requestPermissions
                return;
            }
            notificationManager.notify(1, builder.build());
        }
    }
}
