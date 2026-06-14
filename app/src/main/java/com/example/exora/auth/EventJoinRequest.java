package com.example.exora.auth;

public class EventJoinRequest {
    private int eventId;
    private String userEmail;
    private String studentName;
    private String studentId;

    public EventJoinRequest(int eventId, String userEmail, String studentName, String studentId) {
        this.eventId = eventId;
        this.userEmail = userEmail;
        this.studentName = studentName;
        this.studentId = studentId;
    }

    public int getEventId() { return eventId; }
    public String getUserEmail() { return userEmail; }
    public String getStudentName() { return studentName; }
    public String getStudentId() { return studentId; }
}
