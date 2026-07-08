package com.example.demo.review;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ========== Public Endpoints ==========

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewResponse> reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductAverageRating(productId));
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> getProductTotalReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductTotalReviews(productId));
    }

    @GetMapping("/product/{productId}/distribution")
    public ResponseEntity<List<Object[]>> getProductRatingDistribution(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductRatingDistribution(productId));
    }

    @GetMapping("/product/{productId}/recent")
    public ResponseEntity<List<ReviewResponse>> getRecentProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getRecentProductReviews(productId));
    }

    @GetMapping("/product/{productId}/helpful")
    public ResponseEntity<List<ReviewResponse>> getMostHelpfulProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getMostHelpfulProductReviews(productId));
    }

    @GetMapping("/product/{productId}/search")
    public ResponseEntity<List<ReviewResponse>> searchProductReviews(
            @PathVariable Long productId,
            @RequestParam String keyword) {
        return ResponseEntity.ok(reviewService.searchProductReviews(productId, keyword));
    }

    @GetMapping("/product/{productId}/with-images")
    public ResponseEntity<List<ReviewResponse>> getProductReviewsWithImages(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviewsWithImages(productId));
    }

    @GetMapping("/product/{productId}/rating/{rating}")
    public ResponseEntity<Page<ReviewResponse>> getProductReviewsByRating(
            @PathVariable Long productId,
            @PathVariable Integer rating,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviewsByRating(productId, rating, pageable));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ReviewResponse>> getFeaturedReviews() {
        return ResponseEntity.ok(reviewService.getFeaturedReviews());
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    // ========== Authenticated User Endpoints ==========

    @PostMapping("/product/{productId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {
        Long userId = getCurrentUserId();
        ReviewResponse response = reviewService.createReview(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        Long userId = getCurrentUserId();
        ReviewResponse response = reviewService.updateReview(userId, reviewId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/me")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.getUserReviews(userId, pageable));
    }

    @GetMapping("/user/me/product/{productId}")
    public ResponseEntity<ReviewResponse> getMyReviewForProduct(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.getUserReviewForProduct(userId, productId));
    }

    @GetMapping("/user/me/product/{productId}/exists")
    public ResponseEntity<Boolean> hasUserReviewedProduct(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.hasUserReviewedProduct(userId, productId));
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> markReviewHelpful(@PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        reviewService.markReviewHelpful(userId, reviewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reviewId}/unhelpful")
    public ResponseEntity<Void> markReviewUnhelpful(@PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        reviewService.markReviewUnhelpful(userId, reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}/vote")
    public ResponseEntity<Void> removeVote(@PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        reviewService.removeVote(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reviewId}/report")
    public ResponseEntity<Void> reportReview(
            @PathVariable Long reviewId,
            @RequestParam String reason,
            @RequestParam(required = false) String description) {
        Long userId = getCurrentUserId();
        reviewService.reportReview(userId, reviewId, reason, description);
        return ResponseEntity.ok().build();
    }

    // ========== Admin Endpoints ==========

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<ReviewResponse>> getPendingReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getPendingReviews(pageable));
    }

    @PatchMapping("/admin/{reviewId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> approveReview(@PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/{reviewId}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> rejectReview(@PathVariable Long reviewId) {
        reviewService.rejectReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/{reviewId}/toggle-featured")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> toggleFeaturedReview(@PathVariable Long reviewId) {
        reviewService.toggleFeaturedReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReviewAdmin(@PathVariable Long reviewId) {
        reviewService.deleteReviewAdmin(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/reports")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<ReviewReport>> getUnresolvedReports(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getUnresolvedReports(pageable));
    }

    @PatchMapping("/admin/reports/{reportId}/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> resolveReport(@PathVariable Long reportId) {
        String resolvedBy = getCurrentUsername();
        reviewService.resolveReport(reportId, resolvedBy);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<ReviewResponse>> getAllReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllReviews(pageable));
    }

    // ========== Helper Methods ==========

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.example.demo.user.User) {
            com.example.demo.user.User user = (com.example.demo.user.User) authentication.getPrincipal();
            return user.getId();
        }
        throw new RuntimeException("User not authenticated");
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return "SYSTEM";
    }
}