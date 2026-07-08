package com.example.demo.receipt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByReceiptNumber(String receiptNumber);
    
    List<Receipt> findBySupplierId(Long supplierId);
    
    Page<Receipt> findBySupplierId(Long supplierId, Pageable pageable);
    
    List<Receipt> findByStatus(String status);
    
    Page<Receipt> findByStatus(String status, Pageable pageable);
    
    List<Receipt> findByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT r FROM Receipt r WHERE r.receiptDate BETWEEN :startDate AND :endDate")
    List<Receipt> findByReceiptDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM Receipt r WHERE r.totalAmount >= :minAmount AND r.totalAmount <= :maxAmount")
    List<Receipt> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT r FROM Receipt r WHERE r.status = 'PENDING' AND r.createdAt <= :cutoffDate")
    List<Receipt> findPendingReceiptsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Modifying
    @Transactional
    @Query("UPDATE Receipt r SET r.status = :status WHERE r.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Modifying
    @Transactional
    @Query("UPDATE Receipt r SET r.paymentStatus = :paymentStatus WHERE r.id = :id")
    void updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") String paymentStatus);
    
    @Query("SELECT COALESCE(SUM(r.totalAmount), 0) FROM Receipt r WHERE r.status = 'APPROVED' AND r.receiptDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalReceiptsAmountBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(r) FROM Receipt r WHERE r.status = 'APPROVED' AND r.receiptDate BETWEEN :startDate AND :endDate")
    long countApprovedReceiptsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // FIXED: Use @Query for search methods
    @Query("SELECT r FROM Receipt r WHERE LOWER(r.receiptNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Receipt> searchReceipts(@Param("keyword") String keyword);
    
    @Query("SELECT r FROM Receipt r WHERE LOWER(r.receiptNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Receipt> searchReceipts(@Param("keyword") String keyword, Pageable pageable);
}