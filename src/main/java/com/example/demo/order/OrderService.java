package com.example.demo.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse getOrderById(Long id);

    OrderResponse getOrderByOrderNumber(String orderNumber);

    List<OrderSummaryResponse> getOrdersByUserId(Long userId);

    Page<OrderSummaryResponse> getOrdersByUserId(Long userId, Pageable pageable);

    List<OrderSummaryResponse> getAllOrders();

    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    TrackingResponse trackOrder(String trackingCode);

    OrderResponse trackOrderByTrackingCode(OrderTrackingRequest request);

    void cancelOrder(Long orderId);

    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    OrderSummaryResponse getOrderSummary(Long orderId);
}