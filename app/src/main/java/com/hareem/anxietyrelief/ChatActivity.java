package com.hareem.anxietyrelief;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.hareem.anxietyrelief.Adapter.ChatAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;



import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.TimeZone;


public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;

    TextView username,Status;
    private WebSocketClient mWebSocketClient;
    String SessionTime,PatientName, SessionDate,appointmentid;


    String patientID, therapistID;
    private ArrayList<Message> messages;
    private ChatAdapter adapter;
    private ListView chatListView;
    TextView timer;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        messageEditText = findViewById(R.id.messageEditText);
        ImageView sendButton = findViewById(R.id.sendbutton);
        username = findViewById(R.id.nameTextView);
        chatListView = findViewById(R.id.chatListView);
        Status=findViewById(R.id.onlineStatus);
        timer=findViewById(R.id.timerTextView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("APPOINTMENT_OBJECT")) {
            Appointment appointment = (Appointment) intent.getSerializableExtra("APPOINTMENT_OBJECT");
            patientID = appointment.getPatientId();
            therapistID = appointment.getTherapistId();
            SessionTime=appointment.getTime();
            appointmentid=appointment.get_id();
            Log.d("tinee", "onCreate: "+SessionTime);



            PatientName=appointment.getPatientname();
            SessionDate=appointment.getDay();
        }
        if (intent != null && intent.hasExtra("username")) {
            username.setText(intent.getStringExtra("username"));
        }


        messages = new ArrayList<>();

        // Initialize adapter with empty list
        adapter = new ChatAdapter(this, messages);
        chatListView.setAdapter(adapter);

        connectWebSocket(therapistID, patientID);
        updateCountdown(SessionTime);


        // Handle send button click event
        findViewById(R.id.sendbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(therapistID,messageText);}
            }
        });
    }
    private void updateCountdown(String startTime) {
        try {
            // Get current time
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY); // 24-hour format
            int currentMinute = currentTime.get(Calendar.MINUTE);

            // Parse the start time
            int startHour;
            int startMinute = 0; // Assuming minutes are always 0
            boolean isAM;
            if (startTime.endsWith("am")) {
                isAM = true;
            } else {
                isAM = false;
            }
            // Remove "am" or "pm" from the start time
            startTime = startTime.replace("am", "").replace("pm", "").trim();
            if (startTime.equals("12")) {
                // If start hour is 12, adjust it to 0 for 24-hour format
                startHour = 0;
            } else {
                startHour = Integer.parseInt(startTime);
            }

            // Adjust start hour for PM times
            if (!isAM && startHour != 12) {
                startHour += 12;
            }

            // Set current time to the start time
            Calendar startTimeToday = Calendar.getInstance();
            startTimeToday.set(Calendar.HOUR_OF_DAY, startHour);
            startTimeToday.set(Calendar.MINUTE, startMinute);
            startTimeToday.set(Calendar.SECOND, 0);

            // Calculate end time (1 hour after start time)
            Calendar endTime = (Calendar) startTimeToday.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 1);

            // Check if the current time is between the start and end times
            if (currentTime.after(startTimeToday) && currentTime.before(endTime)) {
                // Calculate remaining time until the end time
                long remainingMilliseconds = endTime.getTimeInMillis() - currentTime.getTimeInMillis();

                countDownTimer=  new CountDownTimer(remainingMilliseconds, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // Update UI with remaining time
                        long seconds = millisUntilFinished / 1000;
                        long minutes = seconds / 60;
                        seconds = seconds % 60;
                        long hours = minutes / 60;
                        minutes = minutes % 60;
                        String remainingTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                        timer.setText(remainingTime);
                    }

                    public void onFinish() {
                        // Handle finish
                        timer.setText("Session Ended");
                        showSessionEndedDialog();

                    }
                }.start();
            } else {
                // Session has ended


            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private void showSessionEndedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Session Ended");
        builder.setMessage("Your session has ended.");
        builder.setCancelable(false); // Prevent dialog from being dismissed by back button

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ChatActivity.this, PatientNavigationDrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // Prevent dialog from being dismissed by touching outside

        // Show the dialog
        dialog.show();
    }




    private void connectWebSocket(String therapistId, String patientId) {
        URI uri;
        try {
            // Pass therapistId and patientId as query parameters in the WebSocket connection URL
            uri = new URI("ws://192.168.100.250:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override

            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("WebSocket", "Opened");
                registerUser("patient", patientID, therapistID); // Pass patientID as roomId
            }


            @Override
            public void onMessage(String s) {
                Log.i("WebSocket", "Received message: " + s);
                // Handle incoming message
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String event = jsonObject.getString("event");

                    if (event.equals("therapist_online")) {
                        // Therapist is online, show a notification
                        runOnUiThread(() ->Status.setVisibility(View.VISIBLE));
                    } else if (event.equals("therapist_offline")) {
                        // Therapist is offline, show a notification
                        runOnUiThread(() ->Status.setVisibility(View.GONE));
                    }else{
                        String text = jsonObject.getString("text");
                        String sender = jsonObject.getString("sender");
                        // Create a new Message object from the received data
                        Message message = new Message(text, sender.equals("patient"));
                        // Add the message to the list
                        messages.add(message);
                        // Notify the adapter that the data set has changed
                        runOnUiThread(() -> adapter.notifyDataSetChanged()); }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("WebSocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.e("WebSocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // Modify sendMessage method in ChatActivity.java
    private void sendMessage(String roomId, String messageText) {
        // Check if room ID and message text are not empty
        if (!roomId.isEmpty() && !messageText.isEmpty()) {
            // Create a JSON object with message data including room ID
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("event", "message"); // Add the event field
                jsonObject.put("roomId", appointmentid);
                jsonObject.put("text", messageText);

                jsonObject.put("sender", "patient");
                jsonObject.put("PatientName", PatientName);
                jsonObject.put("SessionDate", SessionDate);
                jsonObject.put("SessionTime", SessionTime);
                jsonObject.put("TherapistID", therapistID);// Assuming the user sending the message is the sender
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            // Send the JSON object as a string to the WebSocket server
            if (mWebSocketClient != null) {
                mWebSocketClient.send(jsonObject.toString());
            } else {
                Toast.makeText(this, "WebSocket is not connected", Toast.LENGTH_SHORT).show();
            }
            Message message = new Message(messageText, true);

            // Add the message to the list
            messages.add(message);

            // Notify the adapter that the data set has changed
            adapter.notifyDataSetChanged();
            // Clear the message input field
            messageEditText.getText().clear();

            // Optionally, scroll to the bottom of the ListView to show the latest message
            chatListView.smoothScrollToPosition(adapter.getCount() - 1);
        }
    }

    private void registerUser(String role, String userId, String roomId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event", "register");
            jsonObject.put("role", role);
            jsonObject.put("userId", userId);
            jsonObject.put("roomId", appointmentid); // Include roomId in the registration message
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mWebSocketClient != null) {
            mWebSocketClient.send(jsonObject.toString());
        } else {
            Toast.makeText(this, "WebSocket is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectWebSocket(therapistID, patientID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            messages.clear();


        }
    }

    // Method to get current local time
    private String getCurrentLocalTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
