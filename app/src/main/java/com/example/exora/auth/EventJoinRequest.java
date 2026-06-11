package com.example.exora.auth;

public class EventJoinRequest {
    private int eventId;

    public EventJoinRequest(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
}