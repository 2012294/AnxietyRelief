package com.hareem.anxietyrelief;

public class CardData {
    private String cardNumber;



    private String cardname;

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardname() {
        return cardname;
    }

    public String getCvv() {
        return cvv;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    private String cvv;


    private String month;


    private String year;


    public CardData(String cardNumber, String cardname, String cvv, String month, String year) {
        this.cardNumber = cardNumber;
        this.cardname = cardname;
        this.cvv = cvv;
        this.month = month;
        this.year = year;
    }
}
