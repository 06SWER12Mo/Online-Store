package com.example.demo.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ReviewHelpfulRepository extends JpaRepository<ReviewHelpful, Long> {

    @Query("SELECT rh FROM ReviewHelpful rh WHERE rh.review.id = :reviewId AND rh.user.id = :userId")
    Optional<ReviewHelpful> findByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(rh) > 0 THEN true ELSE false END FROM ReviewHelpful rh WHERE rh.review.id = :reviewId AND rh.user.id = :userId")
    boolean existsByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ReviewHelpful rh WHERE rh.review.id = :reviewId AND rh.user.id = :userId")
    void deleteByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ReviewHelpful rh WHERE rh.review.id = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);
    
    @Query("SELECT COUNT(rh) FROM ReviewHelpful rh WHERE rh.review.id = :reviewId AND rh.helpful = true")
    long countHelpfulByReviewId(@Param("reviewId") Long reviewId);
    
    @Query("SELECT COUNT(rh) FROM ReviewHelpful rh WHERE rh.review.id = :reviewId AND rh.helpful = false")
    long countUnhelpfulByReviewId(@Param("reviewId") Long reviewId);
}