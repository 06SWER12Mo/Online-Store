package com.example.demo.review;

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

    // Get approved reviews for a product (sorted newest first)
    List<Review> findByProductIdAndApprovedTrueOrderByCreatedAtDesc(Long productId);

    // Get all reviews for a product (including pending)
    List<Review> findByProductId(Long productId);

    // Get all reviews by a user
    List<Review> findByUserId(Long userId);

    // Check if user already reviewed this product
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Get review by user and product
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    // Get average rating for a product (only approved reviews)
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.approved = true")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    // Count total reviews for a product (only approved)
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.approved = true")
    Long countByProductId(@Param("productId") Long productId);

    // Get all pending reviews (not approved)
    @Query("SELECT r FROM Review r WHERE r.approved = false ORDER BY r.createdAt ASC")
    List<Review> findPendingReviews();

    // Update approval status
    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.approved = :approved WHERE r.id = :id")
    void updateApprovalStatus(@Param("id") Long id, @Param("approved") boolean approved);

    // Delete all reviews for a product (useful for product deletion)
    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    // Delete all reviews by a user (useful for user deletion)
    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}