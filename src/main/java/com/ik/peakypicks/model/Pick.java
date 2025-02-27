package com.ik.peakypicks.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
public class Pick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private Long id;

    private String googleId;
    private String name;
    private String location;
    private double rating;
    private int numberOfReviews;
    private List<String> types;
    private String primaryType;
    private List<Double> lowCordinates;
    private List<Double> highCordinates;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructor for saved picks (with user)
    public Pick(String googleId, String name, String location, double rating, int numberOfReviews, List<String> types, String primaryType, List<Double> lowCordinates, List<Double> highCordinates, User user) {
        this.googleId = googleId;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.numberOfReviews = numberOfReviews;
        this.types = types;
        this.primaryType = primaryType;
        this.lowCordinates = lowCordinates;
        this.highCordinates = highCordinates;
        this.user = user;  // Set user for saved pick
    }

    // Constructor for non-authenticated users (without user)
    public Pick(String googleId, String name, String location, double rating, int numberOfReviews, List<String> types, String primaryType, List<Double> lowCordinates, List<Double> highCordinates) {
        this.googleId = googleId;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.numberOfReviews = numberOfReviews;
        this.types = types;
        this.primaryType = primaryType;
        this.lowCordinates = lowCordinates;
        this.highCordinates = highCordinates;
    }

    public Pick() {
    }

    public void setUser(Optional<User> user) {
        if (user.isPresent()) {
            this.user = user.get();  // Set user if present
        } else {
            throw new IllegalArgumentException("User must be present");
        }
    }

    // Getters
    public String getGoogleId() {
        return googleId;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public double getRating() {
        return rating;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
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

}