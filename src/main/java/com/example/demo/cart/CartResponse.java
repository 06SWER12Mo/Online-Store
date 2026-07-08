package com.example.demo.cart;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Long id;
    private String userEmail;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;

    public CartResponse(Long id, String userEmail, List<CartItemResponse> items, BigDecimal totalPrice) {
        this.id = id;
        this.userEmail = userEmail;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public CartResponse(Long id, String userEmail, List<CartItemResponse> items, double totalPrice) {
        this.id = id;
        this.userEmail = userEmail;
        this.items = items;
        this.totalPrice = BigDecimal.valueOf(totalPrice);
    }

    // Getters
    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public List<CartItemResponse> getItems() { return items; }
    public BigDecimal getTotalPrice() { return totalPrice; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = BigDecimal.valueOf(totalPrice); }
}