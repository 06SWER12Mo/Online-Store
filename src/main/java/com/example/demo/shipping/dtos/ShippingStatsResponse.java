package com.example.demo.shipping.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class ShippingStatsResponse {
    
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    
    // Totals
    private Long totalBatches;
    private Long totalOrders;
    private Long totalBatchesDelivered;
    private Long totalOrdersDelivered;
    
    // Averages
    private Double averageOrdersPerBatch;
    private Double averageDeliveryTimeHours;
    private Double averageTimeToDispatchHours;
    
    // Bus utilization
    private Double busUtilizationRate;
    
    // Daily breakdown
    private List<DailyStats> dailyStats;
    
    // Getters and Setters
    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }
    
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }
    
    public Long getTotalBatches() { return totalBatches; }
    public void setTotalBatches(Long totalBatches) { this.totalBatches = totalBatches; }
    
    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
    
    public Long getTotalBatchesDelivered() { return totalBatchesDelivered; }
    public void setTotalBatchesDelivered(Long totalBatchesDelivered) { this.totalBatchesDelivered = totalBatchesDelivered; }
    
    public Long getTotalOrdersDelivered() { return totalOrdersDelivered; }
    public void setTotalOrdersDelivered(Long totalOrdersDelivered) { this.totalOrdersDelivered = totalOrdersDelivered; }
    
    public Double getAverageOrdersPerBatch() { return averageOrdersPerBatch; }
    public void setAverageOrdersPerBatch(Double averageOrdersPerBatch) { this.averageOrdersPerBatch = averageOrdersPerBatch; }
    
    public Double getAverageDeliveryTimeHours() { return averageDeliveryTimeHours; }
    public void setAverageDeliveryTimeHours(Double averageDeliveryTimeHours) { this.averageDeliveryTimeHours = averageDeliveryTimeHours; }
    
    public Double getAverageTimeToDispatchHours() { return averageTimeToDispatchHours; }
    public void setAverageTimeToDispatchHours(Double averageTimeToDispatchHours) { this.averageTimeToDispatchHours = averageTimeToDispatchHours; }
    
    public Double getBusUtilizationRate() { return busUtilizationRate; }
    public void setBusUtilizationRate(Double busUtilizationRate) { this.busUtilizationRate = busUtilizationRate; }
    
    public List<DailyStats> getDailyStats() { return dailyStats; }
    public void setDailyStats(List<DailyStats> dailyStats) { this.dailyStats = dailyStats; }
    
    // Inner class for daily stats
    public static class DailyStats {
        private LocalDateTime date;
        private Long batchesCreated;
        private Long batchesDispatched;
        private Long batchesDelivered;
        private Long ordersProcessed;
        
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        
        public Long getBatchesCreated() { return batchesCreated; }
        public void setBatchesCreated(Long batchesCreated) { this.batchesCreated = batchesCreated; }
        
        public Long getBatchesDispatched() { return batchesDispatched; }
        public void setBatchesDispatched(Long batchesDispatched) { this.batchesDispatched = batchesDispatched; }
        
        public Long getBatchesDelivered() { return batchesDelivered; }
        public void setBatchesDelivered(Long batchesDelivered) { this.batchesDelivered = batchesDelivered; }
        
        public Long getOrdersProcessed() { return ordersProcessed; }
        public void setOrdersProcessed(Long ordersProcessed) { this.ordersProcessed = ordersProcessed; }
    }
}