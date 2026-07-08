package com.example.demo.receipt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiptResponse {

    private Long id;
    private String receiptNumber;
    private LocalDateTime receiptDate;
    private BigDecimal totalAmount;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private String status;
    private String notes;
    private String paymentMethod;
    private String paymentStatus;
    private String receiptType;
    private Long supplierId;
    private String supplierName;
    private String supplierCode;
    private Long createdById;
    private String createdByName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private List<ReceiptItemResponse> items;
    private int itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ReceiptResponse() {}

    public ReceiptResponse(Receipt receipt) {
        this.id = receipt.getId();
        this.receiptNumber = receipt.getReceiptNumber();
        this.receiptDate = receipt.getReceiptDate();
        this.totalAmount = receipt.getTotalAmount();
        this.subtotal = receipt.getSubtotal();
        this.taxAmount = receipt.getTaxAmount();
        this.shippingCost = receipt.getShippingCost();
        this.discountAmount = receipt.getDiscountAmount();
        this.status = receipt.getStatus();
        this.notes = receipt.getNotes();
        this.paymentMethod = receipt.getPaymentMethod();
        this.paymentStatus = receipt.getPaymentStatus();
        this.receiptType = receipt.getReceiptType();
        this.itemCount = receipt.getItemCount();
        this.createdAt = receipt.getCreatedAt();
        this.updatedAt = receipt.getUpdatedAt();
        this.approvedAt = receipt.getApprovedAt();

        if (receipt.getSupplier() != null) {
            this.supplierId = receipt.getSupplier().getId();
            this.supplierName = receipt.getSupplier().getName();
            this.supplierCode = receipt.getSupplier().getCode();
        }

        if (receipt.getCreatedBy() != null) {
            this.createdById = receipt.getCreatedBy().getId();
            this.createdByName = receipt.getCreatedBy().getFirstName() + " " + receipt.getCreatedBy().getLastName();
        }

        if (receipt.getApprovedBy() != null) {
            this.approvedById = receipt.getApprovedBy().getId();
            this.approvedByName = receipt.getApprovedBy().getFirstName() + " " + receipt.getApprovedBy().getLastName();
        }

        this.items = receipt.getItems().stream()
                .map(ReceiptItemResponse::new)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public LocalDateTime getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(LocalDateTime receiptDate) {
        this.receiptDate = receiptDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Long getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }

    public String getApprovedByName() {
        return approvedByName;
    }

    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public List<ReceiptItemResponse> getItems() {
        return items;
    }

    public void setItems(List<ReceiptItemResponse> items) {
        this.items = items;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}