package com.example.demo.shipping;

import com.example.demo.order.Order;
import com.example.demo.shipping.dtos.ShippingBatchResponse;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShippingMapper {

    public ShippingBatchResponse toShippingBatchResponse(ShippingBatch batch) {
        if (batch == null) return null;

        ShippingBatchResponse response = new ShippingBatchResponse();
        response.setId(batch.getId());
        response.setBigAreaName(batch.getBigArea() != null ? batch.getBigArea().getName() : null);
        response.setBigAreaId(batch.getBigArea() != null ? batch.getBigArea().getId() : null);
        response.setBusPlateNumber(batch.getBus() != null ? batch.getBus().getPlateNumber() : null);
        response.setBusId(batch.getBus() != null ? batch.getBus().getId() : null);
        response.setDriverName(batch.getBus() != null ? batch.getBus().getDriverName() : null);
        response.setStatus(batch.getStatus());
        response.setMinimumOrders(batch.getMinimumOrders());
        response.setCurrentOrderCount(batch.getCurrentOrderCount());
        response.setCreatedAt(batch.getCreatedAt());
        response.setDispatchedAt(batch.getDispatchedAt());
        response.setDeliveredAt(batch.getDeliveredAt());
        response.setAutoDeliverAt(batch.getAutoDeliverAt());

        if (batch.getShippingBatchOrders() != null) {
            List<ShippingBatchResponse.OrderSummary> orderSummaries = batch.getShippingBatchOrders().stream()
                .map(this::toOrderSummary)
                .collect(Collectors.toList());
            response.setOrders(orderSummaries);
        }

        return response;
    }

    public ShippingBatchResponse.OrderSummary toOrderSummary(ShippingBatchOrder batchOrder) {
        if (batchOrder == null || batchOrder.getOrder() == null) return null;

        Order order = batchOrder.getOrder();
        ShippingBatchResponse.OrderSummary summary = new ShippingBatchResponse.OrderSummary();
        summary.setOrderId(order.getId());
        summary.setOrderNumber(order.getOrderNumber());
        summary.setShippingName(order.getShippingName());
        
        String address = String.format("%s, %s, %s",
            order.getShippingBuilding(),
            order.getShippingStreet(),
            order.getShippingTown() != null ? order.getShippingTown().getName() : "");
        summary.setShippingAddress(address);

        return summary;
    }

    public List<ShippingBatchResponse> toShippingBatchResponseList(List<ShippingBatch> batches) {
        return batches.stream()
            .map(this::toShippingBatchResponse)
            .collect(Collectors.toList());
    }
}