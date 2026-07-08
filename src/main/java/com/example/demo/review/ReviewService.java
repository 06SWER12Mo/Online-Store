package com.example.demo.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    // CRUD operations
    ReviewResponse createReview(Long userId, Long productId, ReviewRequest request);
    ReviewResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request);
    void deleteReview(Long userId, Long reviewId);
    void deleteReviewAdmin(Long reviewId);
    ReviewResponse getReviewById(Long reviewId);
    Page<ReviewResponse> getAllReviews(Pageable pageable);
    
    // Product reviews
    Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable);
    List<ReviewResponse> getProductReviewsWithImages(Long productId);
    Page<ReviewResponse> getProductReviewsByRating(Long productId, Integer rating, Pageable pageable);
    List<ReviewResponse> getProductReviewsByMinRating(Long productId, Integer minRating);
    List<ReviewResponse> getRecentProductReviews(Long productId);
    List<ReviewResponse> getMostHelpfulProductReviews(Long productId);
    List<ReviewResponse> searchProductReviews(Long productId, String keyword);
    
    // User reviews
    Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable);
    boolean hasUserReviewedProduct(Long userId, Long productId);
    ReviewResponse getUserReviewForProduct(Long userId, Long productId);
    
    // Admin operations
    Page<ReviewResponse> getPendingReviews(Pageable pageable);
    void approveReview(Long reviewId);
    void rejectReview(Long reviewId);
    void toggleFeaturedReview(Long reviewId);
    List<ReviewResponse> getFeaturedReviews();
    
    // Helpful votes
    void markReviewHelpful(Long userId, Long reviewId);
    void markReviewUnhelpful(Long userId, Long reviewId);
    void removeVote(Long userId, Long reviewId);
    
    // Reports
    void reportReview(Long userId, Long reviewId, String reason, String description);
    void resolveReport(Long reportId, String resolvedBy);
    Page<ReviewReport> getUnresolvedReports(Pageable pageable);
    
    // Statistics
    double getProductAverageRating(Long productId);
    long getProductTotalReviews(Long productId);
    List<Object[]> getProductRatingDistribution(Long productId);
    long countByProductIdAndRating(Long productId, Integer rating);
}