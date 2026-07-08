package com.example.demo.shipping;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ShippingBatchOrderEmbeddedId implements Serializable {

    private Long shippingBatchId;
    private Long orderId;  // Keep as orderId, but @MapsId will map it to order_id column

    public ShippingBatchOrderEmbeddedId() {}

    public ShippingBatchOrderEmbeddedId(Long shippingBatchId, Long orderId) {
        this.shippingBatchId = shippingBatchId;
        this.orderId = orderId;
    }

    public Long getShippingBatchId() {
        return shippingBatchId;
    }

    public void setShippingBatchId(Long shippingBatchId) {
        this.shippingBatchId = shippingBatchId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShippingBatchOrderEmbeddedId that = (ShippingBatchOrderEmbeddedId) o;
        return Objects.equals(shippingBatchId, that.shippingBatchId) &&
               Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shippingBatchId, orderId);
    }
}