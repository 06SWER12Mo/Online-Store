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

    // ========== FIND BY PRODUCT ==========
    List<InventoryTransaction> findByProductId(Long productId);
    Page<InventoryTransaction> findByProductId(Long productId, Pageable pageable);

    // ========== FIND BY REFERENCE ID (NO TYPE NEEDED!) ==========
    List<InventoryTransaction> findByReferenceId(Long referenceId);

    // ========== FIND BY TRANSACTION TYPE ==========
    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionType = :type ORDER BY it.createdAt DESC")
    List<InventoryTransaction> findByTransactionType(@Param("type") InventoryTransactionType type);

    // ========== FIND BY DATE RANGE ==========
    @Query("SELECT it FROM InventoryTransaction it WHERE it.createdAt BETWEEN :startDate AND :endDate ORDER BY it.createdAt DESC")
    List<InventoryTransaction> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ========== FIND BY USER ==========
    @Query("SELECT it FROM InventoryTransaction it WHERE it.createdByUser.id = :userId")
    List<InventoryTransaction> findByCreatedByUserId(@Param("userId") Long userId);

    // ========== STOCK SUMMARIES ==========

    // ✅ SUM quantity by product and transaction type
    @Query("SELECT COALESCE(SUM(it.quantity), 0) FROM InventoryTransaction it " +
           "WHERE it.product.id = :productId AND it.transactionType = :type")
    Integer sumQuantityByProductAndType(
            @Param("productId") Long productId,
            @Param("type") InventoryTransactionType type);

    // ✅ Total received stock (from all products)
    @Query("SELECT COALESCE(SUM(it.quantity), 0) FROM InventoryTransaction it WHERE it.transactionType = 'RECEIVED_STOCK'")
    Integer getTotalReceivedStock();

    // ✅ Total sold stock (from all products)
    @Query("SELECT COALESCE(SUM(it.quantity), 0) FROM InventoryTransaction it WHERE it.transactionType = 'SALE'")
    Integer getTotalSoldStock();

    // ✅ Total returned stock (from all products)
    @Query("SELECT COALESCE(SUM(it.quantity), 0) FROM InventoryTransaction it WHERE it.transactionType = 'RETURN'")
    Integer getTotalReturnedStock();

    // ✅ Total damaged stock (from all products)
    @Query("SELECT COALESCE(SUM(it.quantity), 0) FROM InventoryTransaction it WHERE it.transactionType = 'DAMAGED'")
    Integer getTotalDamagedStock();

    // ✅ Count by transaction type
    @Query("SELECT COUNT(it) FROM InventoryTransaction it WHERE it.transactionType = :type")
    Long countByTransactionType(@Param("type") InventoryTransactionType type);

    // ✅ Recent transactions for a product
    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.id = :productId ORDER BY it.createdAt DESC")
    Page<InventoryTransaction> findRecentTransactionsByProductId(@Param("productId") Long productId, Pageable pageable);

    // ✅ Get stock summary for a product (total IN - total OUT)
    @Query("SELECT COALESCE(SUM(CASE WHEN it.transactionType = 'RECEIVED_STOCK' OR it.transactionType = 'RETURN' THEN it.quantity ELSE 0 END), 0) - " +
           "COALESCE(SUM(CASE WHEN it.transactionType = 'SALE' OR it.transactionType = 'DAMAGED' THEN it.quantity ELSE 0 END), 0) " +
           "FROM InventoryTransaction it WHERE it.product.id = :productId")
    Integer calculateCurrentStockByProductId(@Param("productId") Long productId);

    // ✅ Search by notes
    @Query("SELECT it FROM InventoryTransaction it WHERE LOWER(it.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<InventoryTransaction> searchByNotes(@Param("keyword") String keyword);

    // ✅ Get transactions by product and date range
    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.id = :productId AND it.createdAt BETWEEN :startDate AND :endDate")
    List<InventoryTransaction> findByProductAndDateRange(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}