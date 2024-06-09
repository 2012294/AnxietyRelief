package com.hareem.anxietyrelief;

import com.google.gson.annotations.SerializedName;

public class Patient {
    private String email;
    private String password;
    private String username;
    private String _id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return _id;
    }

    public void setUid(String uid) {
        this._id = uid;
    }

    public Patient(String email, String password, String username, String uid) {
        this.email = email;
        this.password = password;
        this.username = username;
        this._id = uid;
    }

}

