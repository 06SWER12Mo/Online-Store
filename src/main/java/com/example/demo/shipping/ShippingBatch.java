package com.example.demo.shipping;

import com.example.demo.location.BigArea;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shipping_batches")
public class ShippingBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "big_area_id")
    private BigArea bigArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus status = ShippingStatus.COLLECTING_ORDERS;

    @Column(nullable = false)
    private Integer minimumOrders = 10;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime dispatchedAt;

    private LocalDateTime deliveredAt;

    @Column(name = "auto_deliver_at")
    private LocalDateTime autoDeliverAt;

    @OneToMany(mappedBy = "shippingBatch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ShippingBatchOrder> shippingBatchOrders = new HashSet<>();

    public ShippingBatch() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigArea getBigArea() { return bigArea; }
    public void setBigArea(BigArea bigArea) { this.bigArea = bigArea; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public ShippingStatus getStatus() { return status; }
    public void setStatus(ShippingStatus status) { this.status = status; }

    public Integer getMinimumOrders() { return minimumOrders; }
    public void setMinimumOrders(Integer minimumOrders) { this.minimumOrders = minimumOrders; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(LocalDateTime dispatchedAt) { this.dispatchedAt = dispatchedAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public LocalDateTime getAutoDeliverAt() { return autoDeliverAt; }
    public void setAutoDeliverAt(LocalDateTime autoDeliverAt) { this.autoDeliverAt = autoDeliverAt; }

    public Set<ShippingBatchOrder> getShippingBatchOrders() { return shippingBatchOrders; }
    public void setShippingBatchOrders(Set<ShippingBatchOrder> shippingBatchOrders) { 
        this.shippingBatchOrders = shippingBatchOrders; 
    }

    // Helper methods
    public void addOrder(ShippingBatchOrder batchOrder) {
        shippingBatchOrders.add(batchOrder);
        batchOrder.setShippingBatch(this);
    }

    public void removeOrder(ShippingBatchOrder batchOrder) {
        shippingBatchOrders.remove(batchOrder);
        batchOrder.setShippingBatch(null);
    }

    public int getCurrentOrderCount() {
        return shippingBatchOrders.size();
    }

    public boolean isReadyToDispatch() {
        return getCurrentOrderCount() >= minimumOrders;
    }
}