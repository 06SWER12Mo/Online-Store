package com.example.demo.store;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_settings")
public class StoreSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ====== STORE BASIC INFO ======
    @Column(name = "store_name", length = 100)
    private String storeName;

    @Column(name = "store_description", length = 500)
    private String storeDescription;

    @Column(name = "store_logo", length = 255)
    private String storeLogo;

    @Column(name = "store_favicon", length = 255)  // ✅ ADD THIS FIELD
    private String storeFavicon;

    // ====== CONTACT INFO ======
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_address", length = 255)
    private String contactAddress;

    // ====== SOCIAL MEDIA (Optional) ======
    @Column(name = "facebook_url", length = 255)
    private String facebookUrl;

    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;

    @Column(name = "twitter_url", length = 255)
    private String twitterUrl;

    // ====== SHIPPING ======
    @Column(name = "default_shipping_cost", precision = 10, scale = 2)
    private BigDecimal defaultShippingCost;

    @Column(name = "free_shipping_threshold", precision = 10, scale = 2)
    private BigDecimal freeShippingThreshold;

    // ====== PAYMENT ======
    @Column(name = "currency_code", length = 3)
    private String currencyCode = "USD";

    @Column(name = "currency_symbol", length = 5)
    private String currencySymbol = "$";

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    // ====== SYSTEM ======
    @Column(name = "items_per_page")
    private Integer itemsPerPage = 20;

    @Column(name = "is_allow_registration")
    private boolean allowRegistration = true;

    @Column(name = "is_maintenance_mode")
    private boolean maintenanceMode = false;

    @Column(name = "maintenance_message", length = 500)
    private String maintenanceMessage;

    // ====== TRACKING ======
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // Constructors
    public StoreSettings() {}

    // Getters and Setters (ALL)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreDescription() { return storeDescription; }
    public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }

    public String getStoreLogo() { return storeLogo; }
    public void setStoreLogo(String storeLogo) { this.storeLogo = storeLogo; }

    public String getStoreFavicon() { return storeFavicon; }  // ✅ ADD THIS
    public void setStoreFavicon(String storeFavicon) { this.storeFavicon = storeFavicon; }  // ✅ ADD THIS

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}