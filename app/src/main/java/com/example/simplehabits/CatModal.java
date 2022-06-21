package com.example.simplehabits;

public class CatModal {
    // variables for our catname,
    // description, tracks and duration, id.
    private String catName;
    private String catCurrency;
    private String catSentiment;
    private String timestamp;
    private int catID;

    // creating getter and setter methods
    public String getCatName() {
        return catName;
    }

    public void setCourseName(String courseName) {
        this.catName = courseName;
    }

    public String getCatCurrency() {
        return catCurrency;
    }

    public void setCatCurrency(String courseDuration) {
        this.catCurrency = courseDuration;
    }

    public String getCatSentiment() {
        return catSentiment;
    }

    public void setCatSentiment(String courseTracks) {
        this.catSentiment = courseTracks;
    }

    public int getId() {
        return catID;
    }

    public void setId(int id) {
        this.catID = id;
    }

    public String getTimestamp(){
        return this.timestamp;
    }

    // Constructor creates a modal for timestamps
    public CatModal(String catName, String catCurrency, String catSentiment) {
        this.catName = catName;
        this.catCurrency = catCurrency;
        this.catSentiment = catSentiment;
    }

    // Constructor creates a modal for habits
    public CatModal(String habitName, String habitCurrency, String habitSentiment, String habitTimestap) {
        this.catName = habitName;
        this.catCurrency = habitCurrency;
        this.catSentiment = habitSentiment;
        this.timestamp = habitTimestap;
    }
}
