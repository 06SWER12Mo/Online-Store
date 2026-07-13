package com.example.demo.receipt;

import com.example.demo.product.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "receipt_items")
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_percent")
    private BigDecimal taxPercent = BigDecimal.ZERO;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Constructors
    public ReceiptItem() {}

    public ReceiptItem(Product product, Integer quantity, BigDecimal unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotals();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotals();
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotals();
    }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
        calculateTotals();
    }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        calculateTotals();
    }

    public BigDecimal getTaxPercent() { return taxPercent; }
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
        calculateTotals();
    }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
        calculateTotals();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Receipt getReceipt() { return receipt; }
    public void setReceipt(Receipt receipt) { this.receipt = receipt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    // Helper methods
    public void calculateTotals() {
        if (unitPrice == null || quantity == null) {
            this.totalPrice = BigDecimal.ZERO;
            this.discountAmount = BigDecimal.ZERO;
            this.taxAmount = BigDecimal.ZERO;
            return;
        }

        BigDecimal subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        
        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        if (this.discountPercent != null && this.discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            discount = subtotal.multiply(this.discountPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (this.discountAmount != null) {
            discount = this.discountAmount;
        }
        
        // Calculate tax
        BigDecimal tax = BigDecimal.ZERO;
        if (this.taxPercent != null && this.taxPercent.compareTo(BigDecimal.ZERO) > 0) {
            tax = subtotal.subtract(discount)
                    .multiply(this.taxPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        this.discountAmount = discount;
        this.taxAmount = tax;
        this.totalPrice = subtotal.subtract(discount).add(tax);
    }
}