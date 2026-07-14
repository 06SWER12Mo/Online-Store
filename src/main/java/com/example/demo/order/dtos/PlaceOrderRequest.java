package com.example.demo.order.dtos;

import jakarta.validation.constraints.NotNull;

public class PlaceOrderRequest {

    // ====== DELIVERY ADDRESS (from Location package) ======
    @NotNull(message = "Delivery address ID is required")
    private Long deliveryAddressId;

    // ====== CART REFERENCE ======
    @NotNull(message = "Cart ID is required")
    private Long cartId;

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================

    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}