package com.example.demo.shipping;

public enum ShippingStatus {
    COLLECTING_ORDERS,   // Waiting for orders
    READY_TO_DISPATCH,   // Minimum orders reached, ready for bus
    DISPATCHED,          // On the road
    DELIVERED,           // Delivered
    CANCELLED            // Cancelled
}