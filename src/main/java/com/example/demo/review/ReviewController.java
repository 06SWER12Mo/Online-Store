package com.example.demo.review;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.review.dtos.ReviewRequest;
import com.example.demo.review.dtos.ReviewResponse;
import com.example.demo.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(summary = "Get approved reviews for a product",
            description = "Retrieves all approved reviews for a given product. Publicly accessible, no authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Reviews retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReviewResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<List<ReviewResponse>> getProductReviews(
            @Parameter(description = "ID of the product", required = true) @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    /**
     * Get product average rating
     */
    @GetMapping("/product/{productId}/rating")
    @Operation(summary = "Get product average rating",
            description = "Retrieves the average rating (based on approved reviews) for a given product. Publicly accessible.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Average rating retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Double> getProductAverageRating(
            @Parameter(description = "ID of the product", required = true) @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductAverageRating(productId));
    }

    /**
     * Get product review count
     */
    @GetMapping("/product/{productId}/count")
    @Operation(summary = "Get product review count",
            description = "Retrieves the total number of approved reviews for a given product. Publicly accessible.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Long> getProductReviewCount(
            @Parameter(description = "ID of the product", required = true) @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviewCount(productId));
    }

    // ========== USER ENDPOINTS (Authenticated) ==========

    /**
     * Create a new review
     */
    @PostMapping("/product/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a review", description = "Creates a new review for a product on behalf of the authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Review created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReviewResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User has already reviewed this product")
    })
    public ResponseEntity<ReviewResponse> createReview(
            @Parameter(description = "ID of the product being reviewed", required = true) @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {

        Long userId = getCurrentUserId();
        ReviewResponse response = reviewService.createReview(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get current user's reviews
     */
    @GetMapping("/user/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my reviews", description = "Retrieves all reviews written by the currently authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<ReviewResponse>> getMyReviews() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    /**
     * Check if current user reviewed a product
     */
    @GetMapping("/user/me/product/{productId}/exists")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Check if current user reviewed a product",
            description = "Returns whether the currently authenticated user has already submitted a review for the given product.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Boolean> hasUserReviewedProduct(
            @Parameter(description = "ID of the product", required = true) @PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.hasUserReviewedProduct(userId, productId));
    }

    /**
     * Get current user's review for a product
     */
    @GetMapping("/user/me/product/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my review for a product",
            description = "Retrieves the currently authenticated user's review for the given product, if one exists.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Review retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReviewResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No review found for this product")
    })
    public ResponseEntity<ReviewResponse> getMyReviewForProduct(
            @Parameter(description = "ID of the product", required = true) @PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.getUserReviewForProduct(userId, productId));
    }

    /**
     * Delete current user's own review
     */
    @DeleteMapping("/{reviewId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete my review", description = "Deletes a review owned by the currently authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Review does not belong to the current user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> deleteMyReview(
            @Parameter(description = "ID of the review to delete", required = true) @PathVariable Long reviewId) {
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get pending reviews", description = "Retrieves all reviews awaiting moderation. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pending reviews retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role")
    })
    public ResponseEntity<List<ReviewResponse>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    /**
     * Approve a review (Admin/Manager only)
     */
    @PatchMapping("/admin/{reviewId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Approve a review", description = "Marks a pending review as approved, making it publicly visible. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Review approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> approveReview(
            @Parameter(description = "ID of the review to approve", required = true) @PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
        return ResponseEntity.ok().build();
    }

    /**
     * Reject a review (Admin/Manager only)
     */
    @DeleteMapping("/admin/{reviewId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reject a review", description = "Rejects and removes a pending review. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Review rejected successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> rejectReview(
            @Parameter(description = "ID of the review to reject", required = true) @PathVariable Long reviewId) {
        reviewService.rejectReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin: Get all reviews for a product (including pending)
     */
    @GetMapping("/admin/product/{productId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all reviews for a product (admin)",
            description = "Retrieves all reviews for a product, including pending and rejected ones. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<List<ReviewResponse>> getAllProductReviews(
            @Parameter(description = "ID of the product", required = true) @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getAllProductReviews(productId));
    }

    /**
     * Admin: Delete any review
     */
    @DeleteMapping("/admin/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete any review (admin)", description = "Deletes any review regardless of owner. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> deleteReviewAdmin(
            @Parameter(description = "ID of the review to delete", required = true) @PathVariable Long reviewId) {
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