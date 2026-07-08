package com.example.demo.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SalesReportResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalItemsSold;
    private BigDecimal averageOrderValue;
    private BigDecimal averageItemsPerOrder;
    private BigDecimal totalTax;
    private BigDecimal totalShipping;
    private BigDecimal totalDiscount;
    private BigDecimal totalRefunds;
    private BigDecimal netRevenue;
    private List<DailySalesSummary> dailySummary;
    private List<HourlySalesSummary> hourlySummary;

    // Constructors
    public SalesReportResponse() {}

    // Getters and Setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalItemsSold() {
        return totalItemsSold;
    }

    public void setTotalItemsSold(Long totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public BigDecimal getAverageItemsPerOrder() {
        return averageItemsPerOrder;
    }

    public void setAverageItemsPerOrder(BigDecimal averageItemsPerOrder) {
        this.averageItemsPerOrder = averageItemsPerOrder;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalShipping() {
        return totalShipping;
    }

    public void setTotalShipping(BigDecimal totalShipping) {
        this.totalShipping = totalShipping;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(BigDecimal totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public BigDecimal getNetRevenue() {
        return netRevenue;
    }

    public void setNetRevenue(BigDecimal netRevenue) {
        this.netRevenue = netRevenue;
    }

    public List<DailySalesSummary> getDailySummary() {
        return dailySummary;
    }

    public void setDailySummary(List<DailySalesSummary> dailySummary) {
        this.dailySummary = dailySummary;
    }

    public List<HourlySalesSummary> getHourlySummary() {
        return hourlySummary;
    }

    public void setHourlySummary(List<HourlySalesSummary> hourlySummary) {
        this.hourlySummary = hourlySummary;
    }

    // Inner classes
    public static class DailySalesSummary {
        private LocalDate date;
        private BigDecimal revenue;
        private Long orderCount;
        private Long itemsSold;

        public DailySalesSummary() {}

        public DailySalesSummary(LocalDate date, BigDecimal revenue, Long orderCount, Long itemsSold) {
            this.date = date;
            this.revenue = revenue;
            this.orderCount = orderCount;
            this.itemsSold = itemsSold;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }

        public Long getItemsSold() { return itemsSold; }
        public void setItemsSold(Long itemsSold) { this.itemsSold = itemsSold; }
    }

    public static class HourlySalesSummary {
        private Integer hour;
        private BigDecimal revenue;
        private Long orderCount;

        public HourlySalesSummary() {}

        public HourlySalesSummary(Integer hour, BigDecimal revenue, Long orderCount) {
            this.hour = hour;
            this.revenue = revenue;
            this.orderCount = orderCount;
        }

        public Integer getHour() { return hour; }
        public void setHour(Integer hour) { this.hour = hour; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
    }
}