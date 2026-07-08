package com.example.demo.common.exception;

public class OrderCancellationException extends RuntimeException {

    private final Long orderId;
    private final String reason;

    public OrderCancellationException(String message) {
        super(message);
        this.orderId = null;
        this.reason = null;
    }

    public OrderCancellationException(Long orderId, String reason) {
        super(String.format("Order %d cannot be cancelled: %s", orderId, reason));
        this.orderId = orderId;
        this.reason = reason;
    }

    public OrderCancellationException(Long orderId, String reason, Throwable cause) {
        super(String.format("Order %d cannot be cancelled: %s", orderId, reason), cause);
        this.orderId = orderId;
        this.reason = reason;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }
}