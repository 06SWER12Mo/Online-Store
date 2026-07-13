package com.example.demo.review;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.review.dtos.ReviewRequest;
import com.example.demo.review.dtos.ReviewResponse;
import com.example.demo.security.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Endpoints for managing product reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ========== PUBLIC ENDPOINTS ==========

    /**
     * Get all approved reviews for a product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    /**
     * Get product average rating
     */
    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductAverageRating(productId));
    }

    /**
     * Get product review count
     */
    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> getProductReviewCount(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviewCount(productId));
    }

    // ========== USER ENDPOINTS (Authenticated) ==========

    /**
     * Create a new review
     */
    @PostMapping("/product/{productId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {

        Long userId = getCurrentUserId();
        ReviewResponse response = reviewService.createReview(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get current user's reviews
     */
    @GetMapping("/user/me")
    public ResponseEntity<List<ReviewResponse>> getMyReviews() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    /**
     * Check if current user reviewed a product
     */
    @GetMapping("/user/me/product/{productId}/exists")
    public ResponseEntity<Boolean> hasUserReviewedProduct(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.hasUserReviewedProduct(userId, productId));
    }

    /**
     * Get current user's review for a product
     */
    @GetMapping("/user/me/product/{productId}")
    public ResponseEntity<ReviewResponse> getMyReviewForProduct(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.getUserReviewForProduct(userId, productId));
    }

    /**
     * Delete current user's own review
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteMyReview(@PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        reviewService.deleteUserReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    // ========== ADMIN ENDPOINTS ==========

    /**
     * Get all pending reviews (Admin/Manager only)
     */
    @GetMapping("/admin/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ReviewResponse>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    /**
     * Approve a review (Admin/Manager only)
     */
    @PatchMapping("/admin/{reviewId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> approveReview(@PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
        return ResponseEntity.ok().build();
    }

    /**
     * Reject a review (Admin/Manager only)
     */
    @DeleteMapping("/admin/{reviewId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> rejectReview(@PathVariable Long reviewId) {
        reviewService.rejectReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin: Get all reviews for a product (including pending)
     */
    @GetMapping("/admin/product/{productId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ReviewResponse>> getAllProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getAllProductReviews(productId));
    }

    /**
     * Admin: Delete any review
     */
    @DeleteMapping("/admin/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteReviewAdmin(@PathVariable Long reviewId) {
        reviewService.deleteReviewAdmin(reviewId);
        return ResponseEntity.noContent().build();
    }

    // ========== HELPER METHODS ==========

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }

        throw new RuntimeException("User not authenticated");
    }
}