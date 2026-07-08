package com.example.demo.shipping;

import jakarta.persistence.*;
import java.io.Serializable;

import com.example.demo.order.Order;

@Entity
@Table(name = "shipping_batch_orders")
public class ShippingBatchOrder implements Serializable {

    @EmbeddedId
    private ShippingBatchOrderEmbeddedId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("shippingBatchId")
    @JoinColumn(name = "batch_id", nullable = false)
    private ShippingBatch shippingBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Constructors
    public ShippingBatchOrder() {}

    public ShippingBatchOrder(ShippingBatch shippingBatch, Order order) {
        this.shippingBatch = shippingBatch;
        this.order = order;
        this.id = new ShippingBatchOrderEmbeddedId(
            shippingBatch.getId(),
            order.getId()
        );
    }

    // Getters and Setters
    public ShippingBatchOrderEmbeddedId getId() {
        return id;
    }

    public void setId(ShippingBatchOrderEmbeddedId id) {
        this.id = id;
    }

    public ShippingBatch getShippingBatch() {
        return shippingBatch;
    }

    public void setShippingBatch(ShippingBatch shippingBatch) {
        this.shippingBatch = shippingBatch;
        if (shippingBatch != null && this.id == null) {
            this.id = new ShippingBatchOrderEmbeddedId();
        }
        if (shippingBatch != null && this.id != null) {
            this.id.setShippingBatchId(shippingBatch.getId());
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order != null && this.id == null) {
            this.id = new ShippingBatchOrderEmbeddedId();
        }
        if (order != null && this.id != null) {
            this.id.setOrderId(order.getId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShippingBatchOrder that = (ShippingBatchOrder) o;
        return shippingBatch.getId().equals(that.shippingBatch.getId()) &&
               order.getId().equals(that.order.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(shippingBatch.getId(), order.getId());
    }
}