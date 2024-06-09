package com.hareem.anxietyrelief;

import java.util.Collections;
import java.util.List;

public class Therapist {
    private String email;
    private String password;

    public List<PdfFile> pdfFiles;

    public List<PdfFile> getPdfFiles() {
        return pdfFiles;
    }

    public void setPdfFiles(List<PdfFile> pdfFiles) {
        this.pdfFiles = pdfFiles;
    }

    private String username;
    private String _id;
    private Boolean active;



    private String originalPdfName;

    private List<String> availableTime;
    private String charges;
    private List<String> degree;


    private String Image;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Therapist(String email, String password, String username, String uid) {
        this.email = email;
        this.password = password;
        this.username = username;
        this._id = uid;

    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

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

    public String getOriginalPdfName() {
        return originalPdfName;
    }

    public void setOriginalPdfName(String originalPdfName) {
        this.originalPdfName = originalPdfName;
    }

    public List<String> getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(List<String> availableTime) {
        this.availableTime = availableTime;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public List<String> getDegree() {
        return degree;
    }

    public void setDegree(List<String> degree) {
        this.degree = degree;
    }

    public void setUid(String uid) {
        this._id = uid;
    }
}