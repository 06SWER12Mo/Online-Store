package com.example.demo.shipping.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class ShippingDashboardResponse {
    
    // Batch counts by status
    private Long totalBatches;
    private Long collectingOrders;
    private Long readyToDispatch;
    private Long dispatched;
    private Long delivered;
    private Long cancelled;
    
    // Order counts
    private Long totalOrdersInBatches;
    private Long pendingOrders; // Orders not yet assigned to any batch
    
    // Bus stats
    private Long totalBuses;
    private Long availableBuses;
    private Long busyBuses;
    
    // Recent activity (last 10 batches)
    private List<ShippingBatchResponse> recentBatches;
    
    // Urgent: Ready to dispatch but no bus
    private List<ShippingBatchResponse> urgentBatches;
    
    // Timestamps
    private LocalDateTime lastBatchCreated;
    private LocalDateTime lastBatchDispatched;
    private LocalDateTime lastBatchDelivered;
    
    // Getters and Setters
    public Long getTotalBatches() { return totalBatches; }
    public void setTotalBatches(Long totalBatches) { this.totalBatches = totalBatches; }
    
    public Long getCollectingOrders() { return collectingOrders; }
    public void setCollectingOrders(Long collectingOrders) { this.collectingOrders = collectingOrders; }
    
    public Long getReadyToDispatch() { return readyToDispatch; }
    public void setReadyToDispatch(Long readyToDispatch) { this.readyToDispatch = readyToDispatch; }
    
    public Long getDispatched() { return dispatched; }
    public void setDispatched(Long dispatched) { this.dispatched = dispatched; }
    
    public Long getDelivered() { return delivered; }
    public void setDelivered(Long delivered) { this.delivered = delivered; }
    
    public Long getCancelled() { return cancelled; }
    public void setCancelled(Long cancelled) { this.cancelled = cancelled; }
    
    public Long getTotalOrdersInBatches() { return totalOrdersInBatches; }
    public void setTotalOrdersInBatches(Long totalOrdersInBatches) { this.totalOrdersInBatches = totalOrdersInBatches; }
    
    public Long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(Long pendingOrders) { this.pendingOrders = pendingOrders; }
    
    public Long getTotalBuses() { return totalBuses; }
    public void setTotalBuses(Long totalBuses) { this.totalBuses = totalBuses; }
    
    public Long getAvailableBuses() { return availableBuses; }
    public void setAvailableBuses(Long availableBuses) { this.availableBuses = availableBuses; }
    
    public Long getBusyBuses() { return busyBuses; }
    public void setBusyBuses(Long busyBuses) { this.busyBuses = busyBuses; }
    
    public List<ShippingBatchResponse> getRecentBatches() { return recentBatches; }
    public void setRecentBatches(List<ShippingBatchResponse> recentBatches) { this.recentBatches = recentBatches; }
    
    public List<ShippingBatchResponse> getUrgentBatches() { return urgentBatches; }
    public void setUrgentBatches(List<ShippingBatchResponse> urgentBatches) { this.urgentBatches = urgentBatches; }
    
    public LocalDateTime getLastBatchCreated() { return lastBatchCreated; }
    public void setLastBatchCreated(LocalDateTime lastBatchCreated) { this.lastBatchCreated = lastBatchCreated; }
    
    public LocalDateTime getLastBatchDispatched() { return lastBatchDispatched; }
    public void setLastBatchDispatched(LocalDateTime lastBatchDispatched) { this.lastBatchDispatched = lastBatchDispatched; }
    
    public LocalDateTime getLastBatchDelivered() { return lastBatchDelivered; }
    public void setLastBatchDelivered(LocalDateTime lastBatchDelivered) { this.lastBatchDelivered = lastBatchDelivered; }
}