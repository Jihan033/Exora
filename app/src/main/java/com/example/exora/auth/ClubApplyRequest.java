package com.example.exora.auth;

public class ClubApplyRequest {
    private String clubName;

    public ClubApplyRequest(String clubName) {
        this.clubName = clubName;
    }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }
}