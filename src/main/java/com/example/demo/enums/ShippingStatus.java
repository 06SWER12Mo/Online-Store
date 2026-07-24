package com.example.demo.enums;

public enum ShippingStatus {
    COLLECTING_ORDERS,   // Waiting for orders
    READY_TO_DISPATCH,   // Minimum orders reached, ready for bus
    DISPATCHED,          // On the road
    DELIVERED,           // Delivered
    CANCELLED            // Cancelled
}