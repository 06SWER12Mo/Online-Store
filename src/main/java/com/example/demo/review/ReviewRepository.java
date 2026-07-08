package com.example.demo.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find by product
    List<Review> findByProductId(Long productId);
    
    Page<Review> findByProductId(Long productId, Pageable pageable);
    
    List<Review> findByProductIdAndApprovedTrue(Long productId);
    
    Page<Review> findByProductIdAndApprovedTrue(Long productId, Pageable pageable);
    
    // Find by user
    List<Review> findByUserId(Long userId);
    
    Page<Review> findByUserId(Long userId, Pageable pageable);
    
    List<Review> findByUserIdAndApprovedTrue(Long userId);
    
    // Find by rating
    List<Review> findByProductIdAndRating(Long productId, Integer rating);
    
    Page<Review> findByProductIdAndRating(Long productId, Integer rating, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.rating >= :minRating AND r.approved = true")
    List<Review> findByProductIdAndMinRating(@Param("productId") Long productId, @Param("minRating") Integer minRating);
    
    // Find approved reviews
    Page<Review> findByApprovedTrue(Pageable pageable);
    
    List<Review> findByApprovedTrueAndFeaturedTrue();
    
    // Find pending reviews (not approved)
    Page<Review> findByApprovedFalse(Pageable pageable);
    
    // Find verified purchases
    List<Review> findByProductIdAndVerifiedPurchaseTrue(Long productId);
    
    // Check if user has reviewed product
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.user.id = :userId AND r.product.id = :productId AND r.deletedAt IS NULL")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    // Get review by user and product
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.product.id = :productId AND r.deletedAt IS NULL")
    Optional<Review> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    // Get average rating for product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.approved = true AND r.deletedAt IS NULL")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
    
    // Get total reviews count for product
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.approved = true AND r.deletedAt IS NULL")
    Long getTotalReviewsByProductId(@Param("productId") Long productId);
    
    // Get rating distribution for product
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.approved = true AND r.deletedAt IS NULL GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistribution(@Param("productId") Long productId);
    
    // Get recent reviews for product
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.approved = true AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsByProductId(@Param("productId") Long productId, Pageable pageable);
    
    // Search reviews by keyword in comment
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND LOWER(r.comment) LIKE LOWER(CONCAT('%', :keyword, '%')) AND r.approved = true AND r.deletedAt IS NULL")
    List<Review> searchReviewsByProductId(@Param("productId") Long productId, @Param("keyword") String keyword);
    
    // Update approval status
    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.approved = :approved WHERE r.id = :id")
    void updateApprovalStatus(@Param("id") Long id, @Param("approved") boolean approved);
    
    // Soft delete review
    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.deletedAt = CURRENT_TIMESTAMP WHERE r.id = :id")
    void softDelete(@Param("id") Long id);
    
    // Get reviews with images
    @Query("SELECT DISTINCT r FROM Review r LEFT JOIN FETCH r.images WHERE r.product.id = :productId AND r.approved = true AND r.deletedAt IS NULL")
    List<Review> findReviewsWithImagesByProductId(@Param("productId") Long productId);
    
    // Count reviews by rating for a product
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.rating = :rating AND r.approved = true AND r.deletedAt IS NULL")
    Long countByProductIdAndRating(@Param("productId") Long productId, @Param("rating") Integer rating);
    
    // Get reviews with highest helpful votes
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.approved = true AND r.deletedAt IS NULL ORDER BY r.helpfulCount DESC")
    List<Review> findMostHelpfulReviews(@Param("productId") Long productId, Pageable pageable);
}