package com.hareem.anxietyrelief;

import java.io.Serializable;
import java.util.Date;

import java.util.Date;

public class Message implements Serializable {
    private String text;
    private boolean isSender;
     private String sender;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private Date timestamp; // Add timestamp field

    public Message(String text, boolean sender) {
        this.text = text;
        this.isSender = sender;
        this.timestamp = new Date(); // Set timestamp to current time
    }

    public Message(String text, boolean sender, Date timestamp) {
        this.text = text;
        this.isSender = sender;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public boolean isSender() {
        return isSender;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
