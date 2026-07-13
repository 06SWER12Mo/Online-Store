package com.example.demo.inventory;

public enum InventoryTransactionType {
    
    RECEIVED_STOCK,  // Stock came in from supplier
    SALE,            // Customer purchased
    RETURN,          // Order cancelled / returned
    ADJUSTMENT,      // Manual correction
    DAMAGED;         // Products damaged

    /**
     * Check if this type adds stock
     */
    public boolean isStockIn() {
        return this == RECEIVED_STOCK || this == RETURN;
    }

    /**
     * Check if this type removes stock
     */
    public boolean isStockOut() {
        return this == SALE || this == DAMAGED;
    }

    /**
     * Check if this type is a manual adjustment
     */
    public boolean isAdjustment() {
        return this == ADJUSTMENT;
    }

    /**
     * Get the effect on stock
     */
    public int getStockEffect() {
        if (isStockIn()) return 1;
        if (isStockOut()) return -1;
        return 0; // ADJUSTMENT
    }
}