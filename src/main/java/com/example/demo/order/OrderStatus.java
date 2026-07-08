package com.example.demo.order;

public enum OrderStatus {
    PendingPayment,
    Paid,
    ReadyForShipping,
    AssignedToBatch,
    Shipped,
    Delivered,
    Cancelled
}