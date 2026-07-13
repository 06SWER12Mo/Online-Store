package com.example.demo.inventory.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "New quantity is required")
    private Integer newQuantity;

    @NotBlank(message = "Reason is required")
    private String reason;

    // Constructors
    public StockAdjustmentRequest() {}

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getNewQuantity() { return newQuantity; }
    public void setNewQuantity(Integer newQuantity) { this.newQuantity = newQuantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}