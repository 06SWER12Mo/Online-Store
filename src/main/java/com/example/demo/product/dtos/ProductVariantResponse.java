package com.example.demo.product.dtos;

import java.math.BigDecimal;

import com.example.demo.product.ProductVariant;

public class ProductVariantResponse {

    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private BigDecimal discountedPrice;
    private Integer stockQuantity;
    private boolean inStock;
    private Double weight;
    private String imageUrl;

    // Constructors
    public ProductVariantResponse() {}

    public ProductVariantResponse(ProductVariant variant) {
        this.id = variant.getId();
        this.name = variant.getName();
        this.sku = variant.getSku();
        this.price = variant.getPrice();
        this.compareAtPrice = variant.getCompareAtPrice();
        this.stockQuantity = variant.getStockQuantity();
        this.inStock = variant.isInStock();
        this.weight = variant.getWeight();
        this.imageUrl = variant.getImageUrl();

        if (this.compareAtPrice != null && this.compareAtPrice.compareTo(this.price) > 0) {
            this.discountedPrice = this.compareAtPrice;
        } else {
            this.discountedPrice = this.price;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCompareAtPrice() {
        return compareAtPrice;
    }

    public void setCompareAtPrice(BigDecimal compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}