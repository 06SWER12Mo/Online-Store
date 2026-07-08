package com.example.demo.cart;

import com.example.demo.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        if (cart == null) {
            return null;
        }

        return new CartResponse(
                cart.getId(),
                cart.getUser().getEmail(),
                cart.getCartItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList()),
                cart.getTotalPrice()
        );
    }

    public CartItemResponse toItemResponse(CartItem item) {
        if (item == null) {
            return null;
        }

        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        CartItemResponse response = new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getPrice(),
                subtotal
        );

        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            response.setProductImage(item.getProduct().getImages().get(0).getImageUrl());
        }

        return response;
    }

    public CartItem toEntity(Cart cart, Product product, Integer quantity) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }
}