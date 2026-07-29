package com.example.demo.mapper;

import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.dto.response.ImageResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.service.ImageService;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    
    private final ImageService imageService;

    public CartMapper(ImageService imageService) {
        this.imageService = imageService;
    }
    
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
     * Convert CartItem to CartItemResponse, including the primary image URL
     */
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

        // Load primary image for this product (same pattern as ProductServiceImpl.loadProductImages)
        try {
            ImageResponse primaryImage = imageService.getPrimaryImage("product", item.getProduct().getId());
            if (primaryImage != null) {
                response.setImageUrl(primaryImage.getImageUrl());
            } else {
                // Fallback: if no PRIMARY image, use the first image (same as ProductServiceImpl)
                List<ImageResponse> images = imageService.getProductImages(item.getProduct().getId());
                if (images != null && !images.isEmpty()) {
                    response.setImageUrl(images.get(0).getImageUrl());
                }
            }
        } catch (Exception e) {
            // If image lookup fails, just continue without an image URL
        }

        return response;
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
