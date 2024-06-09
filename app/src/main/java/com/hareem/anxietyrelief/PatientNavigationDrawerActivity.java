package com.hareem.anxietyrelief;
import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import com.hareem.anxietyrelief.databinding.ActivityPatientNavigationDrawerBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class PatientNavigationDrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPatientNavigationDrawerBinding binding;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPatientNavigationDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarPatientNavigationDrawer.toolbar);
        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

     // requestNotificationPermission();

        createNotificationChannel();
        scheduleDailyNotification();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_affirmation, R.id.nav_journal, R.id.nav_anxiety_level,R.id.nav_exercise,R.id.nav_find_therapist)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient_navigation_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Add a destination changed listener to update the Toolbar title
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Update the toolbar title based on the destination ID
            switch (destination.getId()) {
                case R.id.nav_home:
                    getSupportActionBar().setTitle("");
                    break;
                case R.id.nav_journal:
                    getSupportActionBar().setTitle("");
                    break;
                case R.id.nav_anxiety_level:
                    getSupportActionBar().setTitle("");
                    break;
                case R.id.nav_affirmation:
                    getSupportActionBar().setTitle("");
                    break;
                case R.id.nav_exercise:
                    getSupportActionBar().setTitle("");
                    break;
                case R.id.nav_find_therapist:
                    getSupportActionBar().setTitle("");
                    break;
                default:
                    // Handle other cases or set a default title
                    getSupportActionBar().setTitle("");
                    break;
            }
        });

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("patient_login", false);
            editor1.apply();
            SharedPreferences themePreferences = getSharedPreferences("theme_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor themeEditor = themePreferences.edit();
            themeEditor.clear();
            themeEditor.apply();

            Intent i = new Intent(PatientNavigationDrawerActivity.this, PatientLogin.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(i);
            finish();
            return true;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                navController.navigate(R.id.nav_home);
            } else if (id == R.id.nav_journal) {
                navController.navigate(R.id.nav_journal);
            } else if (id == R.id.nav_anxiety_level) {
                navController.navigate(R.id.nav_anxiety_level);
            } else if (id == R.id.nav_affirmation) {
                navController.navigate(R.id.nav_affirmation);
            }else if (id == R.id.nav_exercise) {
                navController.navigate(R.id.nav_exercise);
            }else if (id == R.id.nav_find_therapist) {
                navController.navigate(R.id.nav_find_therapist);
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_navigation_drawer, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PatientSettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient_navigation_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Affirmation Channel";
            String description = "Daily Affirmations";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("affirmation_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("Notification1", "Notification channel created.");
        }
    }



    private void scheduleDailyNotification() {
        SharedPreferences prefs = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE);
        boolean isNotificationScheduled = prefs.getBoolean("is_notification_scheduled", false);

        // If notification is already scheduled, return
        if (isNotificationScheduled) {
            Log.d("Notification1", "return");
            return;
        }

        // Create a calendar instance in the Pakistan timezone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Karachi"));
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the hour and minute for the notification
        calendar.set(Calendar.HOUR_OF_DAY, 1); // Set the hour for the notification (3 PM in 24-hour format)
        calendar.set(Calendar.MINUTE, 10);      // Set the minute for the notification
        calendar.set(Calendar.SECOND, 0);       // Set seconds to 0 to avoid delays

        // If the set time is before the current time, add one day to ensure it schedules for the next occurrence
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Log.d("Notification1", "Scheduled for the next day.");
        }

        // Create an Intent for the BroadcastReceiver
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set up AlarmManager to trigger the BroadcastReceiver at the specified time
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        // Update flag to indicate that notification is scheduled
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_notification_scheduled", true);
        editor.apply();

        // Log after setting the alarm
        Log.d("Notification1", "Alarm set for daily notification at 3:39 PM.");
    }




}



