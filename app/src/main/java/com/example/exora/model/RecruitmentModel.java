package com.example.exora.model;

public class RecruitmentModel {
    private String clubName;
    private String description;
    private String deadline;

    public RecruitmentModel(String clubName, String description, String deadline) {
        this.clubName = clubName;
        this.description = description;
        this.deadline = deadline;
    }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}