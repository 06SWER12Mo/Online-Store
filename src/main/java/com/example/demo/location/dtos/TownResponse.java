package com.example.demo.location.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.location.Town;

public class TownResponse {

    private Long id;
    private String name;
    private String code;
    private String zipCode;
    private String description;
    private boolean active;
    private Integer displayOrder;
    private Double latitude;
    private Double longitude;
    private java.math.BigDecimal deliveryFee;
    private boolean deliveryAvailable;
    private Long bigAreaId;
    private String bigAreaName;
    private Integer deliveryAddressCount;
    private List<DeliveryAddressResponse> deliveryAddresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public TownResponse() {}

    public TownResponse(Town town) {
        this.id = town.getId();
        this.name = town.getName();
        this.code = town.getCode();
        this.zipCode = town.getZipCode();
        this.description = town.getDescription();
        this.active = town.isActive();
        this.displayOrder = town.getDisplayOrder();
        this.latitude = town.getLatitude();
        this.longitude = town.getLongitude();
        this.deliveryFee = town.getDeliveryFee();
        this.deliveryAvailable = town.isDeliveryAvailable();
        this.createdAt = town.getCreatedAt();
        this.updatedAt = town.getUpdatedAt();
        this.deliveryAddressCount = town.getDeliveryAddresses().size();
        
        if (town.getBigArea() != null) {
            this.bigAreaId = town.getBigArea().getId();
            this.bigAreaName = town.getBigArea().getName();
        }
        
        this.deliveryAddresses = town.getDeliveryAddresses().stream()
                .map(DeliveryAddressResponse::new)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public java.math.BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(java.math.BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public boolean isDeliveryAvailable() {
        return deliveryAvailable;
    }

    public void setDeliveryAvailable(boolean deliveryAvailable) {
        this.deliveryAvailable = deliveryAvailable;
    }

    public Long getBigAreaId() {
        return bigAreaId;
    }

    public void setBigAreaId(Long bigAreaId) {
        this.bigAreaId = bigAreaId;
    }

    public String getBigAreaName() {
        return bigAreaName;
    }

    public void setBigAreaName(String bigAreaName) {
        this.bigAreaName = bigAreaName;
    }

    public Integer getDeliveryAddressCount() {
        return deliveryAddressCount;
    }

    public void setDeliveryAddressCount(Integer deliveryAddressCount) {
        this.deliveryAddressCount = deliveryAddressCount;
    }

    public List<DeliveryAddressResponse> getDeliveryAddresses() {
        return deliveryAddresses;
    }

    public void setDeliveryAddresses(List<DeliveryAddressResponse> deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}