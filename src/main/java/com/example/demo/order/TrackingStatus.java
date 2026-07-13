package com.example.demo.order;

public enum TrackingStatus {
    PENDING_PAYMENT,    // Order placed, waiting for payment
    PAID,               // Payment confirmed
    READY_FOR_SHIPPING, // Ready to be picked up by shipping
    ASSIGNED_TO_BATCH,  // Added to shipping batch
    SHIPPED,            // On the road
    DELIVERED,          // Delivered to customer
    CANCELLED           // Cancelled
}