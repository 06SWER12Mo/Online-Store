package com.example.demo.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReviewId(Long reviewId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ReviewImage ri WHERE ri.review.id = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);
    
    @Query("SELECT ri FROM ReviewImage ri WHERE ri.review.id = :reviewId ORDER BY ri.displayOrder ASC")
    List<ReviewImage> findByReviewIdOrdered(@Param("reviewId") Long reviewId);
    
    @Query("SELECT COUNT(ri) FROM ReviewImage ri WHERE ri.review.id = :reviewId")
    long countByReviewId(@Param("reviewId") Long reviewId);
}