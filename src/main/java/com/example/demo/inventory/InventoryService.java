package com.example.demo.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.inventory.dtos.InventoryReportResponse;
import com.example.demo.inventory.dtos.InventoryTransactionResponse;
import com.example.demo.inventory.dtos.StockAdjustmentRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryService {

    // ========== ✅ CORE TRANSACTION METHOD ==========

    /**
     * Create an inventory transaction
     * @param productId Product ID
     * @param type Transaction type (RECEIVED_STOCK, SALE, etc.)
     * @param quantity Quantity (positive number)
     * @param referenceId ID of source document (Receipt ID, Order ID, etc.)
     * @param userId User who created the transaction
     * @param notes Additional notes
     */
    void createInventoryTransaction(
        Long productId,
        InventoryTransactionType type,
        Integer quantity,
        Long referenceId,
        Long userId,
        String notes
    );

    // ========== TRANSACTION QUERIES ==========

    List<InventoryTransactionResponse> getAllTransactions();
    Page<InventoryTransactionResponse> getAllTransactions(Pageable pageable);

    InventoryTransactionResponse getTransactionById(Long id);

    List<InventoryTransactionResponse> getTransactionsByProductId(Long productId);
    Page<InventoryTransactionResponse> getTransactionsByProductId(Long productId, Pageable pageable);

    List<InventoryTransactionResponse> getTransactionsByType(InventoryTransactionType type);

    List<InventoryTransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<InventoryTransactionResponse> getTransactionsByReferenceId(Long referenceId);

    // ========== STOCK ADJUSTMENT ==========

    InventoryTransactionResponse adjustStock(StockAdjustmentRequest request, Long adjustedByUserId);

    List<InventoryTransactionResponse> getStockAdjustmentsByProductId(Long productId);

    // ========== INVENTORY REPORTS ==========

    InventoryReportResponse getInventoryReportByProductId(Long productId);

    List<InventoryReportResponse> getAllInventoryReports();

    // ========== STOCK LEVELS ==========

    Integer getCurrentStockByProductId(Long productId);

    List<InventoryReportResponse> getLowStockProducts(Integer threshold);

    List<InventoryReportResponse> getOutOfStockProducts();

    // ========== DASHBOARD STATISTICS ==========

    Long getTotalTransactionCount();

    Long getTransactionCountByType(InventoryTransactionType type);

    Integer getTotalStockValue();

    Integer getTotalReceivedStock();

    Integer getTotalSoldStock();

    Integer getTotalDamagedStock();

    Integer getTotalReturnedStock();
}