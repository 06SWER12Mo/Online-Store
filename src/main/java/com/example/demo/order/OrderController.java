package com.example.demo.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.order.dtos.OrderResponse;
import com.example.demo.order.dtos.OrderSummaryResponse;
import com.example.demo.order.dtos.OrderTrackingRequest;
import com.example.demo.order.dtos.PlaceOrderRequest;
import com.example.demo.order.dtos.TrackingResponse;
import com.example.demo.order.dtos.UpdateOrderStatusRequest;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "Endpoints for placing, retrieving, tracking, and managing orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Place an order", description = "Places a new order based on the given request.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order placed successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content)
    })
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id", description = "Returns the order identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number", description = "Returns the order identified by the given human-readable order number.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(
            @Parameter(description = "Order number", required = true, example = "ORD-2026-00123")
            @PathVariable String orderNumber) {
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user", description = "Returns summaries of all orders placed by the given user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<List<OrderSummaryResponse>> getOrdersByUserId(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        List<OrderSummaryResponse> responses = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/paged")
    @Operation(summary = "Get orders by user (paged)", description = "Returns a paginated list of order summaries for the given user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Page<OrderSummaryResponse>> getOrdersByUserIdPaged(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId, Pageable pageable) {
        Page<OrderSummaryResponse> responses = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns summaries of all orders in the system.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderSummaryResponse>> getAllOrders() {
        List<OrderSummaryResponse> responses = orderService.getAllOrders();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/paged")
    @Operation(summary = "Get all orders (paged)", description = "Returns a paginated list of order summaries.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<Page<OrderSummaryResponse>> getAllOrdersPaged(Pageable pageable) {
        Page<OrderSummaryResponse> responses = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Updates the status of the order identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order status updated successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/track")
    @Operation(summary = "Track order by tracking code", description = "Looks up and returns the order matching the given tracking code.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No order found for tracking code", content = @Content)
    })
    public ResponseEntity<OrderResponse> trackOrderByTrackingCode(
            @Valid @RequestBody OrderTrackingRequest request) {
        OrderResponse response = orderService.trackOrderByTrackingCode(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/track/{trackingCode}")
    @Operation(summary = "Get tracking details", description = "Returns shipment tracking details for the given tracking code.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tracking details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TrackingResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No tracking details found for code", content = @Content)
    })
    public ResponseEntity<TrackingResponse> trackOrder(
            @Parameter(description = "Tracking code", required = true)
            @PathVariable String trackingCode) {
        TrackingResponse response = orderService.trackOrder(trackingCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order", description = "Cancels the order identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Order cancelled successfully", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Order cannot be cancelled in its current state", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Returns all orders currently in the given status.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Status to filter orders by", required = true)
            @PathVariable OrderStatus status) {
        List<OrderResponse> responses = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Get order summary", description = "Returns a condensed summary of the order identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderSummaryResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id) {
        OrderSummaryResponse response = orderService.getOrderSummary(id);
        return ResponseEntity.ok(response);
    }
}