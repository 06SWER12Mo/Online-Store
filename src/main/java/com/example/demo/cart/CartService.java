package com.example.demo.cart;

import com.example.demo.user.User;

import java.util.Optional;

public interface CartService {

    // 1. Core Actions
    Cart addProductToCart(User user, Long productId, Integer quantity);

    Cart updateItemQuantity(User user, Long productId, Integer quantity);

    Cart removeProductFromCart(User user, Long productId);

    // 2. Retrieval
    Cart getCart(User user);

    // 3. Cleanup (Essential for the Checkout process)
    void clearCart(Long userId);

    // 4. Admin/System Helpers
    Optional<Cart> getCartByUserId(Long userId);
}