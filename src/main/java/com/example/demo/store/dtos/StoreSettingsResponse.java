package com.example.demo.store.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.store.StoreSettings;

public class StoreSettingsResponse {

    private Long id;
    
    // Store Info
    private String storeName;
    private String storeDescription;
    private String storeLogo;

    // Contact
    private String contactEmail;
    private String contactPhone;
    private String contactAddress;

    // Social Media
    private String facebookUrl;
    private String instagramUrl;
    private String twitterUrl;

    // Shipping
    private BigDecimal defaultShippingCost;
    private BigDecimal freeShippingThreshold;

    // Payment
    private String currencyCode;
    private String currencySymbol;
    private BigDecimal taxRate;

    // System
    private Integer itemsPerPage;
    private boolean allowRegistration;
    private boolean maintenanceMode;
    private String maintenanceMessage;

    // Tracking
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Constructors
    public StoreSettingsResponse() {}

    public StoreSettingsResponse(StoreSettings settings) {
        this.id = settings.getId();
        this.storeName = settings.getStoreName();
        this.storeDescription = settings.getStoreDescription();
        this.storeLogo = settings.getStoreLogo();
        this.contactEmail = settings.getContactEmail();
        this.contactPhone = settings.getContactPhone();
        this.contactAddress = settings.getContactAddress();
        this.facebookUrl = settings.getFacebookUrl();
        this.instagramUrl = settings.getInstagramUrl();
        this.twitterUrl = settings.getTwitterUrl();
        this.defaultShippingCost = settings.getDefaultShippingCost();
        this.freeShippingThreshold = settings.getFreeShippingThreshold();
        this.currencyCode = settings.getCurrencyCode();
        this.currencySymbol = settings.getCurrencySymbol();
        this.taxRate = settings.getTaxRate();
        this.itemsPerPage = settings.getItemsPerPage();
        this.allowRegistration = settings.isAllowRegistration();
        this.maintenanceMode = settings.isMaintenanceMode();
        this.maintenanceMessage = settings.getMaintenanceMessage();
        this.updatedAt = settings.getUpdatedAt();
        this.updatedBy = settings.getUpdatedBy();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreDescription() { return storeDescription; }
    public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }

    public String getStoreLogo() { return storeLogo; }
    public void setStoreLogo(String storeLogo) { this.storeLogo = storeLogo; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactAddress() { return contactAddress; }
    public void setContactAddress(String contactAddress) { this.contactAddress = contactAddress; }

    public String getFacebookUrl() { return facebookUrl; }
    public void setFacebookUrl(String facebookUrl) { this.facebookUrl = facebookUrl; }

    public String getInstagramUrl() { return instagramUrl; }
    public void setInstagramUrl(String instagramUrl) { this.instagramUrl = instagramUrl; }

    public String getTwitterUrl() { return twitterUrl; }
    public void setTwitterUrl(String twitterUrl) { this.twitterUrl = twitterUrl; }

    public BigDecimal getDefaultShippingCost() { return defaultShippingCost; }
    public void setDefaultShippingCost(BigDecimal defaultShippingCost) { this.defaultShippingCost = defaultShippingCost; }

    public BigDecimal getFreeShippingThreshold() { return freeShippingThreshold; }
    public void setFreeShippingThreshold(BigDecimal freeShippingThreshold) { this.freeShippingThreshold = freeShippingThreshold; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public Integer getItemsPerPage() { return itemsPerPage; }
    public void setItemsPerPage(Integer itemsPerPage) { this.itemsPerPage = itemsPerPage; }

    public boolean isAllowRegistration() { return allowRegistration; }
    public void setAllowRegistration(boolean allowRegistration) { this.allowRegistration = allowRegistration; }

    public boolean isMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public String getMaintenanceMessage() { return maintenanceMessage; }
    public void setMaintenanceMessage(String maintenanceMessage) { this.maintenanceMessage = maintenanceMessage; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}