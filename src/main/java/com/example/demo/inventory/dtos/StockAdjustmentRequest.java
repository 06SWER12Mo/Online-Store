package com.example.demo.inventory.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Adjustment amount is required")
    private Integer adjustmentDelta;  // Positive = add stock, Negative = remove stock

    @NotBlank(message = "Reason is required")
    private String reason;

    // Constructors
    public StockAdjustmentRequest() {}

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getAdjustmentDelta() { return adjustmentDelta; }
    public void setAdjustmentDelta(Integer adjustmentDelta) { this.adjustmentDelta = adjustmentDelta; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}