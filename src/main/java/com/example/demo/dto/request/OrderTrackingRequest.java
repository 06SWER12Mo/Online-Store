package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public class OrderTrackingRequest {

    @NotBlank
    private String trackingCode;

    // Getters and Setters
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
}