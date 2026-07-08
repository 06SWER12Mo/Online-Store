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
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    List<ReviewReport> findByReviewId(Long reviewId);
    
    Page<ReviewReport> findByResolvedFalse(Pageable pageable);
    
    List<ReviewReport> findByResolvedFalse();
    
    @Query("SELECT rr FROM ReviewReport rr WHERE rr.review.id = :reviewId AND rr.reportedBy.id = :userId")
    Optional<ReviewReport> findByReviewIdAndReportedBy(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(rr) > 0 THEN true ELSE false END FROM ReviewReport rr WHERE rr.review.id = :reviewId AND rr.reportedBy.id = :userId")
    boolean existsByReviewIdAndReportedBy(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE ReviewReport rr SET rr.resolved = true, rr.resolvedBy = :resolvedBy, rr.resolvedAt = CURRENT_TIMESTAMP WHERE rr.id = :id")
    void resolveReport(@Param("id") Long id, @Param("resolvedBy") String resolvedBy);
    
    @Query("SELECT COUNT(rr) FROM ReviewReport rr WHERE rr.review.id = :reviewId AND rr.resolved = false")
    long countUnresolvedByReviewId(@Param("reviewId") Long reviewId);
}