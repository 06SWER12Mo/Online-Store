package com.example.demo.review;

import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewHelpfulRepository reviewHelpfulRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ReviewImageRepository reviewImageRepository,
                             ReviewHelpfulRepository reviewHelpfulRepository,
                             ReviewReportRepository reviewReportRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.reviewHelpfulRepository = reviewHelpfulRepository;
        this.reviewReportRepository = reviewReportRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.reviewMapper = reviewMapper;
    }

    // ========== CRUD Operations ==========

    @Override
    public ReviewResponse createReview(Long userId, Long productId, ReviewRequest request) {
        // Check if user already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("You have already reviewed this product");
        }

        Review review = reviewMapper.toEntity(request, userId, productId);
        Review savedReview = reviewRepository.save(review);

        // Handle images
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                ReviewImage image = reviewMapper.toImageEntity(imageUrl, savedReview);
                reviewImageRepository.save(image);
            }
        }

        // Update product rating
        updateProductRating(productId);

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request) {
        Review review = findReviewById(reviewId);
        
        // Verify user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this review");
        }

        reviewMapper.updateEntity(review, request);
        Review updatedReview = reviewRepository.save(review);

        // Update images if provided
        if (request.getImageUrls() != null) {
            // Delete existing images
            reviewImageRepository.deleteByReviewId(reviewId);
            
            // Add new images
            for (String imageUrl : request.getImageUrls()) {
                ReviewImage image = reviewMapper.toImageEntity(imageUrl, updatedReview);
                reviewImageRepository.save(image);
            }
        }

        // Update product rating
        updateProductRating(review.getProduct().getId());

        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    public void deleteReview(Long userId, Long reviewId) {
        Review review = findReviewById(reviewId);
        
        // Verify user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this review");
        }

        reviewRepository.softDelete(reviewId);
        updateProductRating(review.getProduct().getId());
    }

    @Override
    public void deleteReviewAdmin(Long reviewId) {
        Review review = findReviewById(reviewId);
        reviewRepository.softDelete(reviewId);
        updateProductRating(review.getProduct().getId());
    }

    @Override
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = findReviewById(reviewId);
        return reviewMapper.toResponse(review);
    }

    @Override
    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        return reviewRepository.findByApprovedTrue(pageable)
                .map(reviewMapper::toResponse);
    }

    // ========== Product Reviews ==========

    @Override
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndApprovedTrue(productId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Override
    public List<ReviewResponse> getProductReviewsWithImages(Long productId) {
        return reviewRepository.findReviewsWithImagesByProductId(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Page<ReviewResponse> getProductReviewsByRating(Long productId, Integer rating, Pageable pageable) {
        return reviewRepository.findByProductIdAndRating(productId, rating, pageable)
                .map(reviewMapper::toResponse);
    }

    @Override
    public List<ReviewResponse> getProductReviewsByMinRating(Long productId, Integer minRating) {
        return reviewRepository.findByProductIdAndMinRating(productId, minRating)
                .stream()
                .map(reviewMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getRecentProductReviews(Long productId) {
        return reviewRepository.findRecentReviewsByProductId(productId, Pageable.ofSize(10))
                .stream()
                .map(reviewMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getMostHelpfulProductReviews(Long productId) {
        return reviewRepository.findMostHelpfulReviews(productId, Pageable.ofSize(10))
                .stream()
                .map(reviewMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<ReviewResponse> searchProductReviews(Long productId, String keyword) {
        return reviewRepository.searchReviewsByProductId(productId, keyword)
                .stream()
                .map(reviewMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    // ========== User Reviews ==========

    @Override
    public Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Override
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public ReviewResponse getUserReviewForProduct(Long userId, Long productId) {
        Review review = reviewRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return reviewMapper.toResponse(review);
    }

    // ========== Admin Operations ==========

    @Override
    public Page<ReviewResponse> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByApprovedFalse(pageable)
                .map(reviewMapper::toResponse);
    }

    @Override
    public void approveReview(Long reviewId) {
        reviewRepository.updateApprovalStatus(reviewId, true);
        Review review = findReviewById(reviewId);
        updateProductRating(review.getProduct().getId());
    }

    @Override
    public void rejectReview(Long reviewId) {
        reviewRepository.updateApprovalStatus(reviewId, false);
    }

    @Override
    public void toggleFeaturedReview(Long reviewId) {
        Review review = findReviewById(reviewId);
        review.setFeatured(!review.isFeatured());
        reviewRepository.save(review);
    }

    @Override
    public List<ReviewResponse> getFeaturedReviews() {
        return reviewRepository.findByApprovedTrueAndFeaturedTrue()
                .stream()
                .map(reviewMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    // ========== Helpful Votes ==========

    @Override
    public void markReviewHelpful(Long userId, Long reviewId) {
        Review review = findReviewById(reviewId);
        User user = findUserById(userId);

        // Check if user already voted
        reviewHelpfulRepository.findByReviewIdAndUserId(reviewId, userId)
                .ifPresent(vote -> {
                    throw new RuntimeException("You have already voted on this review");
                });

        ReviewHelpful vote = new ReviewHelpful(true, review, user);
        reviewHelpfulRepository.save(vote);
        
        // Update counts
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    @Override
    public void markReviewUnhelpful(Long userId, Long reviewId) {
        Review review = findReviewById(reviewId);
        User user = findUserById(userId);

        // Check if user already voted
        reviewHelpfulRepository.findByReviewIdAndUserId(reviewId, userId)
                .ifPresent(vote -> {
                    throw new RuntimeException("You have already voted on this review");
                });

        ReviewHelpful vote = new ReviewHelpful(false, review, user);
        reviewHelpfulRepository.save(vote);
        
        // Update counts
        review.setUnhelpfulCount(review.getUnhelpfulCount() + 1);
        reviewRepository.save(review);
    }

    @Override
    public void removeVote(Long userId, Long reviewId) {
        reviewHelpfulRepository.deleteByReviewIdAndUserId(reviewId, userId);
    }

    // ========== Reports ==========

    @Override
    public void reportReview(Long userId, Long reviewId, String reason, String description) {
        Review review = findReviewById(reviewId);
        User user = findUserById(userId);

        // Check if user already reported this review
        if (reviewReportRepository.existsByReviewIdAndReportedBy(reviewId, userId)) {
            throw new RuntimeException("You have already reported this review");
        }

        ReviewReport report = new ReviewReport(reason, review, user);
        report.setDescription(description);
        reviewReportRepository.save(report);
    }

    @Override
    public void resolveReport(Long reportId, String resolvedBy) {
        reviewReportRepository.resolveReport(reportId, resolvedBy);
    }

    @Override
    public Page<ReviewReport> getUnresolvedReports(Pageable pageable) {
        return reviewReportRepository.findByResolvedFalse(pageable);
    }

    // ========== Statistics ==========

    @Override
    public double getProductAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }

    @Override
    public long getProductTotalReviews(Long productId) {
        Long count = reviewRepository.getTotalReviewsByProductId(productId);
        return count != null ? count : 0L;
    }

    @Override
    public List<Object[]> getProductRatingDistribution(Long productId) {
        return reviewRepository.getRatingDistribution(productId);
    }

    @Override
    public long countByProductIdAndRating(Long productId, Integer rating) {
        return reviewRepository.countByProductIdAndRating(productId, rating);
    }

    // ========== Helper Methods ==========

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private void updateProductRating(Long productId) {
        double averageRating = getProductAverageRating(productId);
        long totalReviews = getProductTotalReviews(productId);
        productRepository.updateRating(productId, averageRating, (int) totalReviews);
    }
}