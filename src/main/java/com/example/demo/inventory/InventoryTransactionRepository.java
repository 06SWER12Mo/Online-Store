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
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByProductId(Long productId);

    Page<InventoryTransaction> findByProductId(Long productId, Pageable pageable);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.referenceType = :referenceType AND it.referenceId = :referenceId")
    List<InventoryTransaction> findByReferenceTypeAndReferenceId(
            @Param("referenceType") InventoryReferenceType referenceType,
            @Param("referenceId") Long referenceId);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionType = :type ORDER BY it.createdAt DESC")
    List<InventoryTransaction> findByTransactionType(@Param("type") InventoryTransactionType type);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.createdByUser.id = :userId")
    List<InventoryTransaction> findByCreatedByUserId(@Param("userId") Long userId);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.createdAt BETWEEN :startDate AND :endDate ORDER BY it.createdAt DESC")
    List<InventoryTransaction> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // FIXED: Returns Integer with COALESCE instead of Optional<Integer>
    @Query("SELECT COALESCE(SUM(it.quantity), 0) FROM InventoryTransaction it " +
           "WHERE it.product.id = :productId AND it.transactionType = :type")
    Integer sumQuantityByProductAndType(
            @Param("productId") Long productId,
            @Param("type") InventoryTransactionType type);

    @Query("SELECT it.product.id, SUM(it.quantity) FROM InventoryTransaction it GROUP BY it.product.id")
    List<Object[]> getStockSummaryByProduct();

    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.id = :productId AND it.createdAt BETWEEN :startDate AND :endDate")
    List<InventoryTransaction> findByProductAndDateRange(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.id = :productId ORDER BY it.createdAt DESC")
    Page<InventoryTransaction> findRecentTransactionsByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT COUNT(it) FROM InventoryTransaction it WHERE it.transactionType = :type")
    Long countByTransactionType(@Param("type") InventoryTransactionType type);

    @Query("SELECT COALESCE(SUM(it.quantity), 0) FROM InventoryTransaction it WHERE it.transactionType = 'ReceivedStock'")
    Integer getTotalReceivedStock();

    @Query("SELECT COALESCE(SUM(it.quantity), 0) FROM InventoryTransaction it WHERE it.transactionType = 'Sale'")
    Integer getTotalSoldStock();
}