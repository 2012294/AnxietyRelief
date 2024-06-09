package com.hareem.anxietyrelief.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hareem.anxietyrelief.Message;
import com.hareem.anxietyrelief.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChatAdapter extends ArrayAdapter<Message> {
    private ArrayList<Message> messages;
    private LayoutInflater inflater;

    public ChatAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
        this.messages = messages;
        inflater = LayoutInflater.from(context);
    }
    public void addMessage(Message message) {
        messages.add(message); // Add the new message to the ArrayList
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.message_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.senderBubbleLayout = view.findViewById(R.id.senderMessageBubble);
            viewHolder.receiverBubbleLayout = view.findViewById(R.id.receiverMessageBubble);
            viewHolder.senderMessageTextView = view.findViewById(R.id.senderMessageTextView);
            viewHolder.receiverMessageTextView = view.findViewById(R.id.receiverMessageTextView);
            viewHolder.senderMessageTimeTextView = view.findViewById(R.id.senderMessageTimeTextView);
            viewHolder.receiverMessageTimeTextView = view.findViewById(R.id.receiverMessageTimeTextView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(message.getTimestamp());

        if (message.isSender()) {
            viewHolder.senderBubbleLayout.setVisibility(View.VISIBLE);
            viewHolder.receiverBubbleLayout.setVisibility(View.GONE);
            viewHolder.senderMessageTextView.setText(message.getText());
            viewHolder.senderMessageTimeTextView.setText(formattedTime);
        } else {
            viewHolder.senderBubbleLayout.setVisibility(View.GONE);
            viewHolder.receiverBubbleLayout.setVisibility(View.VISIBLE);
            viewHolder.receiverMessageTextView.setText(message.getText());
            viewHolder.receiverMessageTimeTextView.setText(formattedTime);
        }

        return view;
    }

    private static class ViewHolder {
        LinearLayout senderBubbleLayout;
        LinearLayout receiverBubbleLayout;
        TextView senderMessageTextView;
        TextView receiverMessageTextView;
        TextView senderMessageTimeTextView;
        TextView receiverMessageTimeTextView;
    }
}
