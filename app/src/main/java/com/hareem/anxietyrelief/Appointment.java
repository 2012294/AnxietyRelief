package com.hareem.anxietyrelief;
import java.io.Serializable;
public class Appointment implements Serializable {
    private String therapistId;
    private String patientId;
    private String patientname;
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    private String therapistname;

    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public String getTherapistname() {
        return therapistname;
    }

    public void setTherapistname(String therapistname) {
        this.therapistname = therapistname;
    }

    private String SessionType;
    private String day;
    private String time;
    private String transactionId;

    // Constructors, getters, and setters
    public Appointment() {
    }

    public Appointment(String day, String time) {
        this.day = day;
        this.time = time;
    }

    public String getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(String therapistId) {
        this.therapistId = therapistId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getSessionType() {
        return SessionType;
    }

    public void setSessionType(String sessionType) {
        this.SessionType = sessionType;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
