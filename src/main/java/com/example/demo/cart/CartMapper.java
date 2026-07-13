package com.example.demo.cart;

import com.example.demo.cart.dtos.CartItemResponse;
import com.example.demo.cart.dtos.CartResponse;
import com.example.demo.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    /**
     * Convert Cart entity to CartResponse DTO
     */
    public CartResponse toResponse(Cart cart) {
        if (cart == null) {
            return null;
        }

        String userEmail = cart.getUser() != null ? cart.getUser().getEmail() : "Guest";

        List<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                userEmail,
                itemResponses,
                cart.getTotalPrice()
        );
    }

    /**
     * Convert CartItem to CartItemResponse
     */
    public CartItemResponse toItemResponse(CartItem item) {
        if (item == null) {
            return null;
        }

        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getPrice(),
                subtotal
        );
    }

    /**
     * Convert list of CartItems to list of CartItemResponses
     */
    public List<CartItemResponse> toItemResponseList(List<CartItem> items) {
        return items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create CartItem entity from Cart, Product, and quantity
     */
    public CartItem toEntity(Cart cart, Product product, Integer quantity) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }
}