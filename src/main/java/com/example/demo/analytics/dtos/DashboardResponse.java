package com.example.demo.analytics.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardResponse {

    // Summary metrics
    private Long totalOrders;
    private Long totalProducts;
    private Long totalCustomers;
    private Long totalCategories;
    private Long totalReviews;

    // Revenue metrics
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal thisWeekRevenue;
    private BigDecimal thisMonthRevenue;
    private BigDecimal averageOrderValue;

    // Order metrics
    private Long pendingOrders;
    private Long processingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;

    // Stock metrics
    private Long lowStockProducts;
    private Long outOfStockProducts;
    private Long totalStockQuantity;

    // Growth metrics
    private Double revenueGrowthPercentage;
    private Double orderGrowthPercentage;
    private Double customerGrowthPercentage;

    // Recent data
    private List<SalesByDayResponse> recentSales;
    private List<TopProductResponse> topSellingProducts;
    private List<TopCategoryResponse> topCategories;

    // Time
    private LocalDateTime lastUpdated;

    // Constructors
    public DashboardResponse() {}

    // Getters and Setters
    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(Long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public BigDecimal getThisWeekRevenue() {
        return thisWeekRevenue;
    }

    public void setThisWeekRevenue(BigDecimal thisWeekRevenue) {
        this.thisWeekRevenue = thisWeekRevenue;
    }

    public BigDecimal getThisMonthRevenue() {
        return thisMonthRevenue;
    }

    public void setThisMonthRevenue(BigDecimal thisMonthRevenue) {
        this.thisMonthRevenue = thisMonthRevenue;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public Long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public Long getProcessingOrders() {
        return processingOrders;
    }

    public void setProcessingOrders(Long processingOrders) {
        this.processingOrders = processingOrders;
    }

    public Long getShippedOrders() {
        return shippedOrders;
    }

    public void setShippedOrders(Long shippedOrders) {
        this.shippedOrders = shippedOrders;
    }

    public Long getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setDeliveredOrders(Long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }

    public Long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public Long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(Long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public Long getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public void setOutOfStockProducts(Long outOfStockProducts) {
        this.outOfStockProducts = outOfStockProducts;
    }

    public Long getTotalStockQuantity() {
        return totalStockQuantity;
    }

    public void setTotalStockQuantity(Long totalStockQuantity) {
        this.totalStockQuantity = totalStockQuantity;
    }

    public Double getRevenueGrowthPercentage() {
        return revenueGrowthPercentage;
    }

    public void setRevenueGrowthPercentage(Double revenueGrowthPercentage) {
        this.revenueGrowthPercentage = revenueGrowthPercentage;
    }

    public Double getOrderGrowthPercentage() {
        return orderGrowthPercentage;
    }

    public void setOrderGrowthPercentage(Double orderGrowthPercentage) {
        this.orderGrowthPercentage = orderGrowthPercentage;
    }

    public Double getCustomerGrowthPercentage() {
        return customerGrowthPercentage;
    }

    public void setCustomerGrowthPercentage(Double customerGrowthPercentage) {
        this.customerGrowthPercentage = customerGrowthPercentage;
    }

    public List<SalesByDayResponse> getRecentSales() {
        return recentSales;
    }

    public void setRecentSales(List<SalesByDayResponse> recentSales) {
        this.recentSales = recentSales;
    }

    public List<TopProductResponse> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<TopProductResponse> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }

    public List<TopCategoryResponse> getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(List<TopCategoryResponse> topCategories) {
        this.topCategories = topCategories;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Inner classes
    public static class SalesByDayResponse {
        private String date;
        private BigDecimal revenue;
        private Long orderCount;

        public SalesByDayResponse() {}

        public SalesByDayResponse(String date, BigDecimal revenue, Long orderCount) {
            this.date = date;
            this.revenue = revenue;
            this.orderCount = orderCount;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
    }

    public static class TopProductResponse {
        private Long productId;
        private String productName;
        private String productSku;
        private Long totalSold;
        private BigDecimal revenue;
        private String imageUrl;

        public TopProductResponse() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }

        public Long getTotalSold() { return totalSold; }
        public void setTotalSold(Long totalSold) { this.totalSold = totalSold; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class TopCategoryResponse {
        private Long categoryId;
        private String categoryName;
        private Long productCount;
        private BigDecimal revenue;

        public TopCategoryResponse() {}

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public Long getProductCount() { return productCount; }
        public void setProductCount(Long productCount) { this.productCount = productCount; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }
}