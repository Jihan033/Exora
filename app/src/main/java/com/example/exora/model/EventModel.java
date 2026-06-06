package com.example.exora.model;

public class EventModel {
    private int id;
    private String name;
    private String date;
    private String time;
    private String location;
    private String description;
    private String status; // Registration Open, Ongoing, Upcoming

    public EventModel(int id, String name, String date, String time, String location, String description, String status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.status = status;
    }

    // Constructor for new events
    public EventModel(String name, String date, String time, String location, String description, String status) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}
