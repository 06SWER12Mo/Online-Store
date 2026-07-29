package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.Town;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    /** Lightweight constructor for list/search endpoints — avoids lazy-loading delivery addresses */
    public TownResponse(Long id, String name, String code, String zipCode,
                         String description, boolean active, Integer displayOrder,
                         Double latitude, Double longitude,
                         java.math.BigDecimal deliveryFee, boolean deliveryAvailable,
                         Long bigAreaId, String bigAreaName,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.zipCode = zipCode;
        this.description = description;
        this.active = active;
        this.displayOrder = displayOrder;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryFee = deliveryFee;
        this.deliveryAvailable = deliveryAvailable;
        this.bigAreaId = bigAreaId;
        this.bigAreaName = bigAreaName;
        this.deliveryAddressCount = 0;
        this.deliveryAddresses = null;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Full constructor for detail views */
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
        this.deliveryAddressCount = 0;
        this.deliveryAddresses = null;
        
        if (town.getBigArea() != null) {
            this.bigAreaId = town.getBigArea().getId();
            this.bigAreaName = town.getBigArea().getName();
        }
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

    @JsonIgnore
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