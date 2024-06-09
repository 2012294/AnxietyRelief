package com.hareem.anxietyrelief;

import java.io.Serializable;
import java.util.List;

public class RecordedChatMessage  implements Serializable {
    private String _id;
    private String roomId;
    private String PatientName;
    private String SessionDate;
    private String SessionTime;
    private String TherapistID;
    private List<Message> messages; // Change to List<Message>



    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }

    public String getSessionDate() {
        return SessionDate;
    }

    public void setSessionDate(String sessionDate) {
        SessionDate = sessionDate;
    }

    public String getSessionTime() {
        return SessionTime;
    }

    public void setSessionTime(String sessionTime) {
        SessionTime = sessionTime;
    }

    public String getTherapistID() {
        return TherapistID;
    }

    public void setTherapistID(String therapistID) {
        TherapistID = therapistID;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
