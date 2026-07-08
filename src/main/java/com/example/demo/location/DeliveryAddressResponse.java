package com.example.demo.location;

import java.time.LocalDateTime;

public class DeliveryAddressResponse {

    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String street;
    private String building;
    private String floor;
    private String apartment;
    private String landmark;
    private boolean isDefault;
    private String addressType;
    private String recipientName;
    private String recipientPhone;
    private String additionalInstructions;
    private Double latitude;
    private Double longitude;
    private boolean active;
    private Long userId;
    private String userFullName;
    private Long townId;
    private String townName;
    private String bigAreaName;
    private String fullAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public DeliveryAddressResponse() {}

    public DeliveryAddressResponse(DeliveryAddress address) {
        this.id = address.getId();
        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.street = address.getStreet();
        this.building = address.getBuilding();
        this.floor = address.getFloor();
        this.apartment = address.getApartment();
        this.landmark = address.getLandmark();
        this.isDefault = address.isDefault();
        this.addressType = address.getAddressType();
        this.recipientName = address.getRecipientName();
        this.recipientPhone = address.getRecipientPhone();
        this.additionalInstructions = address.getAdditionalInstructions();
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        this.active = address.isActive();
        this.createdAt = address.getCreatedAt();
        this.updatedAt = address.getUpdatedAt();
        this.fullAddress = address.getFullAddress();

        if (address.getUser() != null) {
            this.userId = address.getUser().getId();
            this.userFullName = address.getUser().getFirstName() + " " + address.getUser().getLastName();
        }

        if (address.getTown() != null) {
            this.townId = address.getTown().getId();
            this.townName = address.getTown().getName();
            if (address.getTown().getBigArea() != null) {
                this.bigAreaName = address.getTown().getBigArea().getName();
            }
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void setAdditionalInstructions(String additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Long getTownId() {
        return townId;
    }

    public void setTownId(Long townId) {
        this.townId = townId;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getBigAreaName() {
        return bigAreaName;
    }

    public void setBigAreaName(String bigAreaName) {
        this.bigAreaName = bigAreaName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
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