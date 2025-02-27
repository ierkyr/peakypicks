package com.ik.peakypicks.security;

public class JwtResponse {
    private String message;
    private String token;

    // Constructor, getters, setters
    public JwtResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}