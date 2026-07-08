package com.example.demo.product;

public class ProductImageResponse {

    private Long id;
    private String imageUrl;
    private String altText;
    private boolean primary;
    private Integer displayOrder;
    private Long imageSize;
    private String imageType;

    // Constructors
    public ProductImageResponse() {}

    public ProductImageResponse(ProductImage image) {
        this.id = image.getId();
        this.imageUrl = image.getImageUrl();
        this.altText = image.getAltText();
        this.primary = image.isPrimary();
        this.displayOrder = image.getDisplayOrder();
        this.imageSize = image.getImageSize();
        this.imageType = image.getImageType();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getImageSize() {
        return imageSize;
    }

    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}