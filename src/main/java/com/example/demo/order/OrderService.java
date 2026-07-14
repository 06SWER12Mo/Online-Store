package com.example.demo.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.order.dtos.OrderResponse;
import com.example.demo.order.dtos.OrderSummaryResponse;
import com.example.demo.order.dtos.OrderTrackingRequest;
import com.example.demo.order.dtos.PlaceOrderRequest;
import com.example.demo.order.dtos.TrackingResponse;
import com.example.demo.order.dtos.UpdateOrderStatusRequest;

import java.util.List;

public interface OrderService {

    // ========== BASIC CRUD ==========
    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);

    OrderResponse getOrderById(Long id);

    OrderResponse getOrderByOrderNumber(String orderNumber);

    List<OrderSummaryResponse> getOrdersByUserId(Long userId);

    Page<OrderSummaryResponse> getOrdersByUserId(Long userId, Pageable pageable);

    List<OrderSummaryResponse> getAllOrders();

    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);

    // ========== NEW METHODS FOR CURRENT USER ORDERS ==========
    
    /**
     * Get orders for a specific user with pagination and filtering by status
     */
    Page<OrderSummaryResponse> getOrdersByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
    
    /**
     * Count total orders for a specific user
     */
    long countOrdersByUserId(Long userId);
    
    /**
     * Count orders for a specific user filtered by status
     */
    long countOrdersByUserIdAndStatus(Long userId, OrderStatus status);
    
    /**
     * Get recent orders for a specific user (limited number)
     */
    List<OrderSummaryResponse> getRecentOrdersByUserId(Long userId, int limit);

    // ========== ORDER MANAGEMENT ==========
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    void cancelOrder(Long orderId);

    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    OrderSummaryResponse getOrderSummary(Long orderId);

    // ========== SHIPPING INTEGRATION ==========
    OrderResponse markOrderReadyForShipping(Long orderId);

    // ========== TRACKING ==========
    TrackingResponse trackOrder(String trackingCode);

    OrderResponse trackOrderByTrackingCode(OrderTrackingRequest request);

    // ========== DELIVERY ==========
    OrderResponse confirmDelivery(Long orderId);
}