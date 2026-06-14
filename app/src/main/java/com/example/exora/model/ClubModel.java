package com.example.exora.model;

import com.google.gson.annotations.SerializedName;

public class ClubModel {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("description")
    private String description;

    public ClubModel(int id, String name, String category, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public ClubModel(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
