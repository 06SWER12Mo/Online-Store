package com.example.demo.order.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.order.OrderStatus;
import com.example.demo.order.TrackingStatus;

public class TrackingResponse {

    private String orderNumber;
    private String guestName;
    private String shippingName;
    private String shippingAddress;
    private BigDecimal totalPrice;
    private OrderStatus currentStatus;
    private String trackingCode;
    private LocalDateTime createdAt;
    private List<TrackingEvent> trackingHistory;

    // Nested DTO for tracking events
    public static class TrackingEvent {
        private TrackingStatus status;
        private String description;
        private LocalDateTime timestamp;

        // Getters and Setters
        public TrackingStatus getStatus() { return status; }
        public void setStatus(TrackingStatus status) { this.status = status; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    // Getters and Setters
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getShippingName() { return shippingName; }
    public void setShippingName(String shippingName) { this.shippingName = shippingName; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public OrderStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(OrderStatus currentStatus) { this.currentStatus = currentStatus; }

    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<TrackingEvent> getTrackingHistory() { return trackingHistory; }
    public void setTrackingHistory(List<TrackingEvent> trackingHistory) { this.trackingHistory = trackingHistory; }
}