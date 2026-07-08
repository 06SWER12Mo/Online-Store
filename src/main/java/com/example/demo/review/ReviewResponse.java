package com.example.demo.review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewResponse {

    private Long id;
    private Integer rating;
    private String comment;
    private boolean verifiedPurchase;
    private boolean approved;
    private boolean featured;
    private Integer helpfulCount;
    private Integer unhelpfulCount;
    private String reviewerName;
    private Long userId;
    private String userFullName;
    private String userProfilePicture;
    private Long productId;
    private String productName;
    private String productPrimaryImage;
    private List<ReviewImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ReviewResponse() {}

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.verifiedPurchase = review.isVerifiedPurchase();
        this.approved = review.isApproved();
        this.featured = review.isFeatured();
        this.helpfulCount = review.getHelpfulCount();
        this.unhelpfulCount = review.getUnhelpfulCount();
        this.reviewerName = review.getReviewerName();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();

        if (review.getUser() != null) {
            this.userId = review.getUser().getId();
            this.userFullName = review.getUser().getFirstName() + " " + review.getUser().getLastName();
            this.userProfilePicture = review.getUser().getProfilePictureUrl();
        }

        if (review.getProduct() != null) {
            this.productId = review.getProduct().getId();
            this.productName = review.getProduct().getName();
            // Get primary product image
            review.getProduct().getImages().stream()
                    .filter(img -> img.isPrimary())
                    .findFirst()
                    .ifPresent(img -> this.productPrimaryImage = img.getImageUrl());
            if (this.productPrimaryImage == null && !review.getProduct().getImages().isEmpty()) {
                this.productPrimaryImage = review.getProduct().getImages().get(0).getImageUrl();
            }
        }

        this.images = review.getImages().stream()
                .map(ReviewImageResponse::new)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public Integer getUnhelpfulCount() {
        return unhelpfulCount;
    }

    public void setUnhelpfulCount(Integer unhelpfulCount) {
        this.unhelpfulCount = unhelpfulCount;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrimaryImage() {
        return productPrimaryImage;
    }

    public void setProductPrimaryImage(String productPrimaryImage) {
        this.productPrimaryImage = productPrimaryImage;
    }

    public List<ReviewImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ReviewImageResponse> images) {
        this.images = images;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}