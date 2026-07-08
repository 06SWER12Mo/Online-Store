package com.example.demo.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {

    List<StockAdjustment> findByProductId(Long productId);

    Page<StockAdjustment> findByProductId(Long productId, Pageable pageable);

    @Query("SELECT sa FROM StockAdjustment sa WHERE sa.adjustedBy.id = :userId")
    List<StockAdjustment> findByAdjustedByUserId(@Param("userId") Long userId);

    @Query("SELECT sa FROM StockAdjustment sa WHERE sa.createdAt BETWEEN :startDate AND :endDate")
    List<StockAdjustment> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sa FROM StockAdjustment sa WHERE sa.product.id = :productId AND sa.createdAt BETWEEN :startDate AND :endDate")
    List<StockAdjustment> findByProductAndDateRange(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(sa) FROM StockAdjustment sa WHERE sa.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
}