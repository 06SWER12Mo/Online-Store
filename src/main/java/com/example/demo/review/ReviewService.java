package com.example.demo.review;

import java.util.List;

import com.example.demo.review.dtos.ReviewRequest;
import com.example.demo.review.dtos.ReviewResponse;

/**
 * Review Service Interface
 * Defines all review-related business operations
 */
public interface ReviewService {

    // ========== USER OPERATIONS ==========

    /**
     * Create a new review (pending approval)
     */
    ReviewResponse createReview(Long userId, Long productId, ReviewRequest request);

    /**
     * Get all approved reviews for a product (public)
     */
    List<ReviewResponse> getProductReviews(Long productId);

    /**
     * Get product average rating (public)
     */
    double getProductAverageRating(Long productId);

    /**
     * Get total review count for product (public)
     */
    long getProductReviewCount(Long productId);

    /**
     * Get user's own reviews
     */
    List<ReviewResponse> getUserReviews(Long userId);

    /**
     * Check if user has reviewed a product
     */
    boolean hasUserReviewedProduct(Long userId, Long productId);

    /**
     * Get user's review for a specific product
     */
    ReviewResponse getUserReviewForProduct(Long userId, Long productId);

    /**
     * Delete user's own review
     */
    void deleteUserReview(Long userId, Long reviewId);

    // ========== ADMIN OPERATIONS ==========

    /**
     * Get all pending reviews (not approved)
     */
    List<ReviewResponse> getPendingReviews();

    /**
     * Approve a review
     */
    void approveReview(Long reviewId);

    /**
     * Reject a review (delete it)
     */
    void rejectReview(Long reviewId);

    /**
     * Admin: Delete any review
     */
    void deleteReviewAdmin(Long reviewId);

    /**
     * Admin: Get all reviews for a product (including pending)
     */
    List<ReviewResponse> getAllProductReviews(Long productId);
}