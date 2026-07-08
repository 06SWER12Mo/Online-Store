package com.example.demo.order;

import jakarta.validation.constraints.NotBlank;

public class OrderTrackingRequest {

    @NotBlank
    private String trackingCode;

    // Getters and Setters
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
}