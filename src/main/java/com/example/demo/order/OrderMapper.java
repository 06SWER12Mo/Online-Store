package com.example.demo.order;

import org.springframework.stereotype.Component;

import com.example.demo.order.dtos.OrderResponse;
import com.example.demo.order.dtos.OrderSummaryResponse;
import com.example.demo.order.dtos.PlaceOrderRequest;
import com.example.demo.order.dtos.TrackingResponse;
import com.example.demo.product.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        response.setGuestName(order.getGuestName());
        response.setGuestEmail(order.getGuestEmail());
        response.setGuestPhone(order.getGuestPhone());
        response.setShippingName(order.getShippingName());
        response.setShippingPhone(order.getShippingPhone());
        response.setShippingTownName(order.getShippingTown() != null ? order.getShippingTown().getName() : null);
        response.setShippingStreet(order.getShippingStreet());
        response.setShippingBuilding(order.getShippingBuilding());
        response.setLatitude(order.getLatitude());
        response.setLongitude(order.getLongitude());
        response.setSubtotal(order.getSubtotal());
        response.setShippingCost(order.getShippingCost());
        response.setTotalPrice(order.getTotalPrice());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setOrderStatus(order.getOrderStatus());
        response.setTrackingCode(order.getTrackingCode());
        response.setCreatedAt(order.getCreatedAt());

        if (order.getOrderItems() != null) {
            List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }

    public OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) return null;

        OrderResponse.OrderItemResponse response = new OrderResponse.OrderItemResponse();
        response.setId(orderItem.getId());
        response.setProductId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null);
        response.setProductName(orderItem.getProductName());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setQuantity(orderItem.getQuantity());
        response.setLineTotal(orderItem.getLineTotal());

        return response;
    }

    public OrderSummaryResponse toOrderSummaryResponse(Order order) {
        if (order == null) return null;

        OrderSummaryResponse response = new OrderSummaryResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setGuestName(order.getGuestName());
        response.setTotalPrice(order.getTotalPrice());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setOrderStatus(order.getOrderStatus());
        response.setTrackingCode(order.getTrackingCode());
        response.setCreatedAt(order.getCreatedAt());
        response.setItemCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0);

        return response;
    }

    public List<OrderSummaryResponse> toOrderSummaryResponseList(List<Order> orders) {
        return orders.stream()
            .map(this::toOrderSummaryResponse)
            .collect(Collectors.toList());
    }

    public TrackingResponse toTrackingResponse(Order order) {
        if (order == null) return null;

        TrackingResponse response = new TrackingResponse();
        response.setOrderNumber(order.getOrderNumber());
        response.setGuestName(order.getGuestName());
        response.setShippingName(order.getShippingName());
        
        String address = String.format("%s, %s, %s", 
            order.getShippingBuilding(),
            order.getShippingStreet(),
            order.getShippingTown() != null ? order.getShippingTown().getName() : "");
        response.setShippingAddress(address);
        
        response.setTotalPrice(order.getTotalPrice());
        response.setCurrentStatus(order.getOrderStatus());
        response.setTrackingCode(order.getTrackingCode());
        response.setCreatedAt(order.getCreatedAt());

        return response;
    }

    public OrderItem toOrderItem(PlaceOrderRequest.OrderItemRequest itemRequest, Product product, Order order) {
        if (itemRequest == null || product == null) return null;

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setProductName(product.getName());
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setQuantity(itemRequest.getQuantity());
        
        BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
        orderItem.setLineTotal(lineTotal);

        return orderItem;
    }
}