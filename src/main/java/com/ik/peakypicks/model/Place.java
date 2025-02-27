package com.ik.peakypicks.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Place {
    private String googleId;
    private String displayName;
    private String formattedAddress;
    private double rating;
    private int userRatingCount;
    private List<String> types;
    private String primaryType;
    private List<Double> lowCordinates;
    private List<Double> highCordinates;
    public String getGoogleId() {
        return googleId;
    }

    public double getScore() {
        return score;
    }

    private double score;

    public Place(String googleId, String displayName, String formattedAddress,
                 double rating, int userRatingCount, List<String> types,
                 String primaryType, List<Double> lowCordinates, List<Double> highCordinates) {
        this.googleId = googleId;
        this.displayName = displayName != null ? displayName : "Unknown";
        this.formattedAddress = formattedAddress != null ? formattedAddress : "No address available";
        this.rating = rating != 0 ? rating : 0.0;  // Default to 0.0 if not available
        this.userRatingCount = userRatingCount > 0 ? userRatingCount : 0;
        this.types = types != null ? types : new ArrayList<>();
        this.primaryType = primaryType != null ? primaryType : "N/A";
        this.lowCordinates = lowCordinates != null ? lowCordinates : null;
        this.highCordinates = highCordinates != null ? highCordinates : null;

    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }
    public double getRating() {
        return rating;
    }

    public int getUserRatingCount() {
        return userRatingCount;
    }

    public List<String> getTypes() {
        return types;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public List<Double> getLowCordinates() {
        return lowCordinates;
    }
    public List<Double> getHighCordinates() {
        return highCordinates;
    }

    public void calculatePlaceScore(int maxReviewCount, double ratingWeight, double reviewWeight) {
        this.score = (this.rating * ratingWeight) + ((this.userRatingCount * reviewWeight)/maxReviewCount);
    }
}
