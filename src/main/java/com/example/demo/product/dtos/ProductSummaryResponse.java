package com.example.demo.product.dtos;

import java.math.BigDecimal;

import com.example.demo.product.Product;

public class ProductSummaryResponse {

    private Long id;
    private String name;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal discountPercentage;
    private String sku;
    private Integer stockQuantity;
    private boolean inStock;
    private boolean active;
    private boolean featured;
    private Double averageRating;
    private Integer totalReviews;
    private Long categoryId;
    private String categoryName;
    private String primaryImageUrl;
    private Integer viewCount;
    private Integer soldCount;

    // Constructors
    public ProductSummaryResponse() {}

    public ProductSummaryResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.shortDescription = product.getShortDescription();
        this.price = product.getPrice();
        this.discountedPrice = product.getDiscountedPrice();
        this.discountPercentage = product.getDiscountPercentage();
        this.sku = product.getSku();
        this.stockQuantity = product.getStockQuantity();
        this.inStock = product.isInStock();
        this.active = product.isActive();
        this.featured = product.isFeatured();
        this.averageRating = product.getAverageRating();
        this.totalReviews = product.getTotalReviews();
        this.viewCount = product.getViewCount();
        this.soldCount = product.getSoldCount();

        if (product.getCategory() != null) {
            this.categoryId = product.getCategory().getId();
            this.categoryName = product.getCategory().getName();
        }

        // Primary image is set separately via ImageService
        // Call setPrimaryImageUrl() after constructing
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }
}