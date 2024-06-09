package com.hareem.anxietyrelief;

public class AnxietyLevel {
    private String anxietyLevel;
    private String DateTime;
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "AnxietyLevel{" +
                "anxietyLevel='" + anxietyLevel + '\'' +


             + '\'' +
                '}';
    }
    public String getLevel() {
        return anxietyLevel;
    }

    public void setLevel(String level) {
        this.anxietyLevel = level;
    }

    public String getDateTime() {
        return DateTime
            ;
    }

    public void setDateTime(String dateTime) {
        this.DateTime
             = dateTime;
    }
}
