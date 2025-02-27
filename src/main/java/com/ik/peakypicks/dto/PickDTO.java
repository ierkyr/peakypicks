package com.ik.peakypicks.dto;

import java.util.List;
import java.util.Map;

public class PickDTO {
    private String googleId;         // Google ID for the pick
    private String name;             // Name of the pick
    private String location;         // Location of the pick
    private double rating;           // Rating of the pick
    private int numberOfReviews;     // Number of reviews for the pick
    private List<String> types;      // List of types (e.g., "restaurant", "park")
    private String primaryType;      // Main type of the pick (e.g., "food", "outdoor")
    private List<Double> lowCordinates;
    private List<Double> highCordinates;

    // Getters and setters
    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public List<Double> getLowCordinates() {
        return lowCordinates;
    }
    public List<Double> getHighCordinates() {
        return highCordinates;
    }}
