package com.example.demo.receipt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {

    List<ReceiptItem> findByReceiptId(Long receiptId);

    List<ReceiptItem> findByProductId(Long productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReceiptItem ri WHERE ri.receipt.id = :receiptId")
    void deleteByReceiptId(@Param("receiptId") Long receiptId);

    @Query("SELECT SUM(ri.quantity) FROM ReceiptItem ri WHERE ri.product.id = :productId")
    Long getTotalReceivedQuantityByProductId(@Param("productId") Long productId);
}