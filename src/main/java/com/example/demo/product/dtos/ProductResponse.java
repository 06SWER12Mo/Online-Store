package com.example.demo.product.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.image.dtos.ImageResponse;
import com.example.demo.product.Product;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal compareAtPrice;
    private BigDecimal discountedPrice;
    private BigDecimal discountPercentage;
    private String sku;
    private String barcode;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private boolean lowStock;
    private Double weight;
    private Double length;
    private Double width;
    private Double height;
    private boolean active;
    private boolean featured;
    private boolean inStock;
    private boolean digital;
    private Double averageRating;
    private Integer totalReviews;
    private Integer viewCount;
    private Integer soldCount;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private Long categoryId;
    private String categoryName;
    
    // ✅ Images from ImageService
    private List<ImageResponse> images;
    private String primaryImageUrl;
    
    private List<ProductSpecificationResponse> specifications;
    private List<ProductVariantResponse> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ProductResponse() {}

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.shortDescription = product.getShortDescription();
        this.price = product.getPrice();
        this.costPrice = product.getCostPrice();
        this.compareAtPrice = product.getCompareAtPrice();
        this.discountedPrice = product.getDiscountedPrice();
        this.discountPercentage = product.getDiscountPercentage();
        this.sku = product.getSku();
        this.barcode = product.getBarcode();
        this.stockQuantity = product.getStockQuantity();
        this.lowStockThreshold = product.getLowStockThreshold();
        this.lowStock = product.isLowStock();
        this.weight = product.getWeight();
        this.length = product.getLength();
        this.width = product.getWidth();
        this.height = product.getHeight();
        this.active = product.isActive();
        this.featured = product.isFeatured();
        this.inStock = product.isInStock();
        this.digital = product.isDigital();
        this.averageRating = product.getAverageRating();
        this.totalReviews = product.getTotalReviews();
        this.viewCount = product.getViewCount();
        this.soldCount = product.getSoldCount();
        this.metaTitle = product.getMetaTitle();
        this.metaDescription = product.getMetaDescription();
        this.metaKeywords = product.getMetaKeywords();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();

        if (product.getCategory() != null) {
            this.categoryId = product.getCategory().getId();
            this.categoryName = product.getCategory().getName();
        }

        // ✅ Images are set separately via ImageService
        // Call setImages() after constructing

        this.specifications = product.getSpecifications().stream()
                .map(ProductSpecificationResponse::new)
                .toList();

        this.variants = product.getVariants().stream()
                .map(ProductVariantResponse::new)
                .toList();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getCompareAtPrice() { return compareAtPrice; }
    public void setCompareAtPrice(BigDecimal compareAtPrice) { this.compareAtPrice = compareAtPrice; }

    public BigDecimal getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }

    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public boolean isDigital() { return digital; }
    public void setDigital(boolean digital) { this.digital = digital; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }

    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }

    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }

    public String getMetaKeywords() { return metaKeywords; }
    public void setMetaKeywords(String metaKeywords) { this.metaKeywords = metaKeywords; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<ImageResponse> getImages() { return images; }
    public void setImages(List<ImageResponse> images) { this.images = images; }

    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }

    public List<ProductSpecificationResponse> getSpecifications() { return specifications; }
    public void setSpecifications(List<ProductSpecificationResponse> specifications) { this.specifications = specifications; }

    public List<ProductVariantResponse> getVariants() { return variants; }
    public void setVariants(List<ProductVariantResponse> variants) { this.variants = variants; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}