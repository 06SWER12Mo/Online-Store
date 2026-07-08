package com.example.demo.order;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderSummaryResponse> responses = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<OrderSummaryResponse>> getOrdersByUserIdPaged(
            @PathVariable Long userId, Pageable pageable) {
        Page<OrderSummaryResponse> responses = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryResponse>> getAllOrders() {
        List<OrderSummaryResponse> responses = orderService.getAllOrders();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<OrderSummaryResponse>> getAllOrdersPaged(Pageable pageable) {
        Page<OrderSummaryResponse> responses = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/track")
    public ResponseEntity<OrderResponse> trackOrderByTrackingCode(
            @Valid @RequestBody OrderTrackingRequest request) {
        OrderResponse response = orderService.trackOrderByTrackingCode(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<TrackingResponse> trackOrder(@PathVariable String trackingCode) {
        TrackingResponse response = orderService.trackOrder(trackingCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> responses = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(@PathVariable Long id) {
        OrderSummaryResponse response = orderService.getOrderSummary(id);
        return ResponseEntity.ok(response);
    }
}