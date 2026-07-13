package com.example.demo.review.dtos;

import java.time.LocalDateTime;

import com.example.demo.review.Review;

public class ReviewResponse {

    private Long id;
    private Integer rating;
    private String comment;
    private boolean approved;
    private String reviewerName;
    private Long userId;
    private Long productId;
    private String productName;
    private LocalDateTime createdAt;

    // Constructors
    public ReviewResponse() {}

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.approved = review.isApproved();
        this.createdAt = review.getCreatedAt();

        if (review.getUser() != null) {
            this.userId = review.getUser().getId();
            this.reviewerName = review.getUser().getFirstName() + " " + review.getUser().getLastName();
        }

        if (review.getProduct() != null) {
            this.productId = review.getProduct().getId();
            this.productName = review.getProduct().getName();
        }
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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}