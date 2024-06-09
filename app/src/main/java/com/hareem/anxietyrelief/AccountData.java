package com.hareem.anxietyrelief;

public class AccountData {
    private String Number;



    private String name;

    public AccountData(String number, String name) {
        Number = number;
        this.name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
