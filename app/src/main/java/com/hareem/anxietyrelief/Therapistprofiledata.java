package com.hareem.anxietyrelief;

import java.util.List;

public class Therapistprofiledata {
    public List<PdfFile> pdfFiles;
    public List<AccountData> accountList;

    public List<AccountData> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<AccountData> accountList) {
        this.accountList = accountList;
    }

    public List<PdfFile> getPdfFiles() {
        return pdfFiles;
    }

    public void setPdfFiles(List<PdfFile> pdfFiles) {
        this.pdfFiles = pdfFiles;
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

    public int countNonNullFields() {
        int count = 0;
        if (pdfFiles != null && !pdfFiles.isEmpty()) {


                        count++;




        }
        if (availableTime != null && !availableTime.isEmpty()) {
            count++;
        }
        if (accountList != null && !accountList.isEmpty()) {
            count++;
        }
        if (charges != null && !charges.isEmpty()) {
            count++;
        }
        if (Image != null && !Image.isEmpty()) {
            count++;
        }

        if (degree != null && !degree.isEmpty()) {
            count++;
        }
        return count;
    }

}
