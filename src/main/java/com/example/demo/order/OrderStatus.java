package com.example.demo.order;

public enum OrderStatus {
    PENDING_PAYMENT,    // Order placed, waiting for payment
    PAID,               // Payment confirmed
    READY_FOR_SHIPPING, // Ready to be picked up by shipping
    ASSIGNED_TO_BATCH,  // Added to shipping batch
    SHIPPED,            // On the road
    DELIVERED,          // Delivered to customer
    CANCELLED;          // Cancelled

    /**
     * Check if order can be cancelled
     */
    public boolean canBeCancelled() {
        return this != SHIPPED && this != DELIVERED && this != CANCELLED;
    }

    /**
     * Check if order is in shipping process
     */
    public boolean isInShipping() {
        return this == ASSIGNED_TO_BATCH || this == SHIPPED;
    }

    /**
     * Check if order is complete
     */
    public boolean isComplete() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Check if order is active (not complete or cancelled)
     */
    public boolean isActive() {
        return !isComplete();
    }

    /**
     * Check if order is ready to be added to shipping batch
     */
    public boolean isReadyForShipping() {
        return this == READY_FOR_SHIPPING;
    }
}