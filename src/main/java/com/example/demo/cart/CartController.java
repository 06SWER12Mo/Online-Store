package com.example.demo.cart;

import com.example.demo.cart.dtos.AddToCartRequest;
import com.example.demo.cart.dtos.CartResponse;
import com.example.demo.cart.dtos.UpdateCartItemRequest;
import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.user.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart") 
@Tag(name = "Cart Management", description = "Endpoints for managing shopping carts")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Add product to cart",
            description = "Adds the given quantity of a product to the current user's cart, or increases the quantity if it's already present."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product added to cart successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {

        Cart cart = cartService.addProductToCart(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Product added to cart", cartMapper.toResponse(cart)));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Update cart item quantity",
            description = "Updates the quantity of an existing product in the current user's cart."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart updated successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found in cart", content = @Content)
    })
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @Valid @RequestBody UpdateCartItemRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {

        Cart cart = cartService.updateItemQuantity(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cart updated", cartMapper.toResponse(cart)));
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Remove product from cart",
            description = "Removes the given product entirely from the current user's cart."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product removed from cart successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found in cart", content = @Content)
    })
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @Parameter(description = "ID of the product to remove", required = true)
            @PathVariable Long productId,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {

        Cart cart = cartService.removeProductFromCart(user, productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from cart", cartMapper.toResponse(cart)));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get current user's cart",
            description = "Returns the shopping cart belonging to the currently authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Cart cart = cartService.getCart(user);
        return ResponseEntity.ok(ApiResponse.success(cartMapper.toResponse(cart)));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Clear cart",
            description = "Removes all items from the current user's cart."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get cart by user id",
            description = "Returns the shopping cart belonging to the given user. Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cart or user not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<CartResponse>> getCartByUser(
            @Parameter(description = "ID of the user whose cart to retrieve", required = true)
            @PathVariable Long userId) {
        return cartService.getCartByUserId(userId)
                .map(cart -> ResponseEntity.ok(ApiResponse.success(cartMapper.toResponse(cart))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get cart item count",
            description = "Returns the total number of items in the current user's cart."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<Integer>> getCartItemCount(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Cart cart = cartService.getCart(user);
        return ResponseEntity.ok(ApiResponse.success(cart.getTotalItems()));
    }
}