package com.example.exora.model;

import com.google.gson.annotations.SerializedName;

public class AboutUsModel {
    @SerializedName("id")
    private int id;

    @SerializedName("content")
    private String content;

    public AboutUsModel(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
