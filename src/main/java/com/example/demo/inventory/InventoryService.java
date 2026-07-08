package com.example.demo.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryService {

    // Transaction operations
    List<InventoryTransactionResponse> getAllTransactions();

    Page<InventoryTransactionResponse> getAllTransactions(Pageable pageable);

    InventoryTransactionResponse getTransactionById(Long id);

    List<InventoryTransactionResponse> getTransactionsByProductId(Long productId);

    Page<InventoryTransactionResponse> getTransactionsByProductId(Long productId, Pageable pageable);

    List<InventoryTransactionResponse> getTransactionsByType(InventoryTransactionType type);

    List<InventoryTransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<InventoryTransactionResponse> getTransactionsByReference(InventoryReferenceType referenceType, Long referenceId);

    // Stock adjustment operations
    InventoryTransactionResponse adjustStock(StockAdjustmentRequest request, Long adjustedByUserId);

    List<InventoryTransactionResponse> getStockAdjustmentsByProductId(Long productId);

    // Inventory reports
    InventoryReportResponse getInventoryReportByProductId(Long productId);

    List<InventoryReportResponse> getAllInventoryReports();

    // Stock level operations
    Integer getCurrentStockByProductId(Long productId);

    List<InventoryReportResponse> getLowStockProducts(Integer threshold);

    List<InventoryReportResponse> getOutOfStockProducts();

    // Dashboard statistics
    Long getTotalTransactionCount();

    Long getTransactionCountByType(InventoryTransactionType type);

    Integer getTotalStockValue();
}