package com.example.demo.review;

public class ReviewImageResponse {

    private Long id;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;

    // Constructors
    public ReviewImageResponse() {}

    public ReviewImageResponse(ReviewImage image) {
        this.id = image.getId();
        this.imageUrl = image.getImageUrl();
        this.altText = image.getAltText();
        this.displayOrder = image.getDisplayOrder();
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}