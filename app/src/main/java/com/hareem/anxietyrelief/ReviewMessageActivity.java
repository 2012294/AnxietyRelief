package com.hareem.anxietyrelief;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hareem.anxietyrelief.Adapter.ChatAdapter;

import org.java_websocket.client.WebSocketClient;

import java.util.ArrayList;

public class ReviewMessageActivity extends AppCompatActivity {


    TextView username,Status;

    String SessionTime,PatientName, SessionDate;


    String patientID, therapistID;
    private ArrayList<Message> messages;
    private ChatAdapter adapter;
    ArrayList<Message> Message1;
    private ListView chatListView;
    TextView timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_message);


        username = findViewById(R.id.nameTextView);
        chatListView = findViewById(R.id.chatListView);
        Status=findViewById(R.id.onlineStatus);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("MSG_OBJECT")) {
            RecordedChatMessage msg = (RecordedChatMessage) intent.getSerializableExtra("MSG_OBJECT");
             Message1= (ArrayList<Message>) msg.getMessages();
        }
        if (intent != null && intent.hasExtra("username")) {
            username.setText(intent.getStringExtra("username"));
        }


        messages = new ArrayList<>();

        // Initialize adapter with empty list
        adapter = new ChatAdapter(this, messages);
        chatListView.setAdapter(adapter);
        boolean isSender=false;
        if (Message1 != null) {
            for (Message message : Message1) {

                if (message.getSender().equals("therapist")) {
                    isSender = true;
                }else{
                    isSender=false;
                }
              message = new Message(message.getText(), isSender,message.getTimestamp());
                messages.add(message);
            }
            adapter.notifyDataSetChanged(); // Notify the adapter of the data change
        }

    }
}