package com.example.demo.shipping.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.shipping.ShippingStatus;

public class ShippingBatchResponse {

    private Long id;
    private String bigAreaName;
    private Long bigAreaId;
    private String busPlateNumber;
    private Long busId;
    private String driverName;
    private ShippingStatus status;
    private Integer minimumOrders;
    private Integer currentOrderCount;
    private LocalDateTime createdAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime autoDeliverAt;
    private List<OrderSummary> orders;

    public static class OrderSummary {
        private Long orderId;
        private String orderNumber;
        private String shippingName;
        private String shippingAddress;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

        public String getShippingName() { return shippingName; }
        public void setShippingName(String shippingName) { this.shippingName = shippingName; }

        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBigAreaName() { return bigAreaName; }
    public void setBigAreaName(String bigAreaName) { this.bigAreaName = bigAreaName; }

    public Long getBigAreaId() { return bigAreaId; }
    public void setBigAreaId(Long bigAreaId) { this.bigAreaId = bigAreaId; }

    public String getBusPlateNumber() { return busPlateNumber; }
    public void setBusPlateNumber(String busPlateNumber) { this.busPlateNumber = busPlateNumber; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public ShippingStatus getStatus() { return status; }
    public void setStatus(ShippingStatus status) { this.status = status; }

    public Integer getMinimumOrders() { return minimumOrders; }
    public void setMinimumOrders(Integer minimumOrders) { this.minimumOrders = minimumOrders; }

    public Integer getCurrentOrderCount() { return currentOrderCount; }
    public void setCurrentOrderCount(Integer currentOrderCount) { this.currentOrderCount = currentOrderCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(LocalDateTime dispatchedAt) { this.dispatchedAt = dispatchedAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public LocalDateTime getAutoDeliverAt() { return autoDeliverAt; }
    public void setAutoDeliverAt(LocalDateTime autoDeliverAt) { this.autoDeliverAt = autoDeliverAt; }

    public List<OrderSummary> getOrders() { return orders; }
    public void setOrders(List<OrderSummary> orders) { this.orders = orders; }
}