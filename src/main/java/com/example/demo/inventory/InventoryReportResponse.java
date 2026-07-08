package com.example.demo.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryReportResponse {

    private Long productId;
    private String productName;
    private String productSku;  // CHANGED: from productUniqueNumber
    private Integer currentStock;
    private Integer totalReceived;
    private Integer totalSold;
    private Integer totalReturned;
    private Integer totalDamaged;
    private Integer totalAdjusted;
    private BigDecimal currentStockValue;
    private LocalDateTime lastTransactionDate;
    private List<TransactionSummary> recentTransactions;

    // Nested DTO
    public static class TransactionSummary {
        private InventoryTransactionType type;
        private Integer quantity;
        private LocalDateTime date;
        private String referenceInfo;

        public InventoryTransactionType getType() { return type; }
        public void setType(InventoryTransactionType type) { this.type = type; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }

        public String getReferenceInfo() { return referenceInfo; }
        public void setReferenceInfo(String referenceInfo) { this.referenceInfo = referenceInfo; }
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSku() { return productSku; }  // CHANGED
    public void setProductSku(String productSku) { this.productSku = productSku; }  // CHANGED

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getTotalReceived() { return totalReceived; }
    public void setTotalReceived(Integer totalReceived) { this.totalReceived = totalReceived; }

    public Integer getTotalSold() { return totalSold; }
    public void setTotalSold(Integer totalSold) { this.totalSold = totalSold; }

    public Integer getTotalReturned() { return totalReturned; }
    public void setTotalReturned(Integer totalReturned) { this.totalReturned = totalReturned; }

    public Integer getTotalDamaged() { return totalDamaged; }
    public void setTotalDamaged(Integer totalDamaged) { this.totalDamaged = totalDamaged; }

    public Integer getTotalAdjusted() { return totalAdjusted; }
    public void setTotalAdjusted(Integer totalAdjusted) { this.totalAdjusted = totalAdjusted; }

    public BigDecimal getCurrentStockValue() { return currentStockValue; }
    public void setCurrentStockValue(BigDecimal currentStockValue) { this.currentStockValue = currentStockValue; }

    public LocalDateTime getLastTransactionDate() { return lastTransactionDate; }
    public void setLastTransactionDate(LocalDateTime lastTransactionDate) { this.lastTransactionDate = lastTransactionDate; }

    public List<TransactionSummary> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<TransactionSummary> recentTransactions) { this.recentTransactions = recentTransactions; }
}