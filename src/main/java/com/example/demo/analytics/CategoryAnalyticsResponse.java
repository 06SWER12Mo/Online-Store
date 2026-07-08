package com.example.demo.analytics;

import java.math.BigDecimal;

public class CategoryAnalyticsResponse {

    private Long categoryId;
    private String categoryName;
    private Long parentCategoryId;
    private String parentCategoryName;
    private Long productCount;
    private Long activeProductCount;
    private Long totalSold;
    private BigDecimal totalRevenue;
    private Double averagePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer subCategoryCount;
    private Double revenuePercentage;
    private BigDecimal categoryGrowth;

    // Constructors
    public CategoryAnalyticsResponse() {}

    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public Long getActiveProductCount() {
        return activeProductCount;
    }

    public void setActiveProductCount(Long activeProductCount) {
        this.activeProductCount = activeProductCount;
    }

    public Long getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Long totalSold) {
        this.totalSold = totalSold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getSubCategoryCount() {
        return subCategoryCount;
    }

    public void setSubCategoryCount(Integer subCategoryCount) {
        this.subCategoryCount = subCategoryCount;
    }

    public Double getRevenuePercentage() {
        return revenuePercentage;
    }

    public void setRevenuePercentage(Double revenuePercentage) {
        this.revenuePercentage = revenuePercentage;
    }

    public BigDecimal getCategoryGrowth() {
        return categoryGrowth;
    }

    public void setCategoryGrowth(BigDecimal categoryGrowth) {
        this.categoryGrowth = categoryGrowth;
    }
}