package com.example.demo.order;

import org.springframework.stereotype.Component;

import com.example.demo.order.dtos.OrderResponse;
import com.example.demo.order.dtos.OrderSummaryResponse;
import com.example.demo.order.dtos.TrackingResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    /**
     * Convert Order entity to OrderResponse DTO
     */
    public OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        response.setUserName(order.getUserName());
        response.setUserEmail(order.getUserEmail());
        response.setUserPhone(order.getUserPhone());
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

    /**
     * Convert OrderItem to OrderItemResponse DTO
     */
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

    /**
     * Convert Order entity to OrderSummaryResponse DTO
     */
    public OrderSummaryResponse toOrderSummaryResponse(Order order) {
        if (order == null) return null;

        OrderSummaryResponse response = new OrderSummaryResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserName(order.getUserName());
        response.setTotalPrice(order.getTotalPrice());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setOrderStatus(order.getOrderStatus());
        response.setTrackingCode(order.getTrackingCode());
        response.setCreatedAt(order.getCreatedAt());
        response.setItemCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0);

        return response;
    }

    /**
     * Convert List of Orders to List of OrderSummaryResponse DTOs
     */
    public List<OrderSummaryResponse> toOrderSummaryResponseList(List<Order> orders) {
        return orders.stream()
            .map(this::toOrderSummaryResponse)
            .collect(Collectors.toList());
    }

    /**
     * Convert Order entity to TrackingResponse DTO
     */
    public TrackingResponse toTrackingResponse(Order order) {
        if (order == null) return null;

        TrackingResponse response = new TrackingResponse();
        response.setOrderNumber(order.getOrderNumber());
        response.setUserName(order.getUserName());
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
}