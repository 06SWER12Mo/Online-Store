package com.example.demo.order;

import com.example.demo.order.dtos.OrderResponse;
import com.example.demo.order.dtos.OrderSummaryResponse;
import com.example.demo.order.dtos.OrderTrackingRequest;
import com.example.demo.order.dtos.PlaceOrderRequest;
import com.example.demo.order.dtos.TrackingResponse;
import com.example.demo.order.dtos.UpdateOrderStatusRequest;
import com.example.demo.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "Endpoints for placing, retrieving, tracking, and managing orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ============================================================
    // ✅ GET CURRENT USER'S ORDERS WITH PAGINATION
    // ============================================================

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Get current user's orders with pagination",
        description = "Returns a paginated list of orders for the currently authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "Not authenticated",
            content = @Content
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<OrderSummaryResponse>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Long userId = userPrincipal.getId();
        Page<OrderSummaryResponse> orders = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    // ============================================================
    // ✅ GET CURRENT USER'S ORDERS WITH FILTERING BY STATUS
    // ============================================================

    @GetMapping("/my-orders/status/{status}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Get current user's orders by status with pagination",
        description = "Returns a paginated list of orders for the current user filtered by order status."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "Not authenticated",
            content = @Content
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<OrderSummaryResponse>> getMyOrdersByStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Order status to filter by", required = true)
            @PathVariable OrderStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Long userId = userPrincipal.getId();
        Page<OrderSummaryResponse> orders = orderService.getOrdersByUserIdAndStatus(userId, status, pageable);
        return ResponseEntity.ok(orders);
    }

    // ============================================================
    // ✅ GET CURRENT USER'S ORDER COUNT BY STATUS
    // ============================================================

    @GetMapping("/my-orders/count")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Get count of current user's orders",
        description = "Returns the total count of orders for the current user, optionally filtered by status."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderCountResponse> getMyOrderCount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) OrderStatus status) {
        
        Long userId = userPrincipal.getId();
        long count;
        if (status != null) {
            count = orderService.countOrdersByUserIdAndStatus(userId, status);
        } else {
            count = orderService.countOrdersByUserId(userId);
        }
        
        OrderCountResponse response = new OrderCountResponse(count, status);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // ✅ GET CURRENT USER'S RECENT ORDERS (LIMITED)
    // ============================================================

    @GetMapping("/my-orders/recent")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Get current user's recent orders",
        description = "Returns the most recent orders for the current user (limited to 5 by default)."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<OrderSummaryResponse>> getMyRecentOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "5") int limit) {
        
        Long userId = userPrincipal.getId();
        List<OrderSummaryResponse> orders = orderService.getRecentOrdersByUserId(userId, limit);
        return ResponseEntity.ok(orders);
    }

    // ============================================================
    // PLACE ORDER
    // ============================================================

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Place an order from cart",
        description = "Places a new order using items from the user's cart and a delivery address."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "Order placed successfully",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid request, cart is empty, or delivery address not found",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "Not authenticated",
            content = @Content
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getId();
        OrderResponse response = orderService.placeOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // GET ORDERS
    // ============================================================

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(
            @Parameter(description = "Order number", required = true)
            @PathVariable String orderNumber) {
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    @Operation(summary = "Get orders by user (Admin only or own user)")
    public ResponseEntity<List<OrderSummaryResponse>> getOrdersByUserId(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        List<OrderSummaryResponse> responses = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/paged")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    @Operation(summary = "Get orders by user (paged)")
    public ResponseEntity<Page<OrderSummaryResponse>> getOrdersByUserIdPaged(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId, 
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderSummaryResponse> responses = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders (Admin only)")
    public ResponseEntity<List<OrderSummaryResponse>> getAllOrders() {
        List<OrderSummaryResponse> responses = orderService.getAllOrders();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders (paged, Admin only)")
    public ResponseEntity<Page<OrderSummaryResponse>> getAllOrdersPaged(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderSummaryResponse> responses = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(responses);
    }

    // ============================================================
    // ORDER MANAGEMENT
    // ============================================================

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update order status", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Status to filter orders by", required = true)
            @PathVariable OrderStatus status) {
        List<OrderResponse> responses = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Get order summary")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id) {
        OrderSummaryResponse response = orderService.getOrderSummary(id);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // TRACKING
    // ============================================================

    @PostMapping("/track")
    @Operation(summary = "Track order by tracking code")
    public ResponseEntity<OrderResponse> trackOrderByTrackingCode(
            @Valid @RequestBody OrderTrackingRequest request) {
        OrderResponse response = orderService.trackOrderByTrackingCode(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/track/{trackingCode}")
    @Operation(summary = "Get tracking details")
    public ResponseEntity<TrackingResponse> trackOrder(
            @Parameter(description = "Tracking code", required = true)
            @PathVariable String trackingCode) {
        TrackingResponse response = orderService.trackOrder(trackingCode);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // DELIVERY (Admin/Manager)
    // ============================================================

    @PostMapping("/{id}/confirm-delivery")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Confirm delivery",
        description = "Marks order as DELIVERED.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderResponse> confirmDelivery(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id) {
        OrderResponse response = orderService.confirmDelivery(id);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // SHIPPING INTEGRATION
    // ============================================================

    @PostMapping("/{id}/ready-for-shipping")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Mark order ready for shipping",
        description = "Marks order as READY_FOR_SHIPPING and attempts to add to shipping batch.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderResponse> markOrderReadyForShipping(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id) {
        OrderResponse response = orderService.markOrderReadyForShipping(id);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // INNER CLASS FOR ORDER COUNT RESPONSE
    // ============================================================

    public static class OrderCountResponse {
        private long count;
        private OrderStatus status;
        private String message;

        public OrderCountResponse(long count, OrderStatus status) {
            this.count = count;
            this.status = status;
            this.message = status != null ? 
                "Found " + count + " orders with status: " + status : 
                "Found " + count + " total orders";
        }

        // Getters
        public long getCount() { return count; }
        public OrderStatus getStatus() { return status; }
        public String getMessage() { return message; }
    }
}