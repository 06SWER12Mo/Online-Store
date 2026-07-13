package com.example.demo.store.dtos;

import java.math.BigDecimal;

public class StoreSettingsRequest {

    // ====== STORE BASIC INFO ======
    private String storeName;
    private String storeDescription;
    private String storeLogo;      
    private String storeFavicon;   

    // ====== CONTACT INFO ======
    private String contactEmail;
    private String contactPhone;
    private String contactAddress;
    private String contactCity;
    private String contactState;
    private String contactZip;
    private String contactCountry;

    // ====== SOCIAL MEDIA ======
    private String facebookUrl;
    private String instagramUrl;
    private String twitterUrl;

    // ====== SHIPPING ======
    private BigDecimal defaultShippingCost;
    private BigDecimal freeShippingThreshold;

    // ====== PAYMENT ======
    private String currencyCode;
    private String currencySymbol;
    private BigDecimal taxRate;

    // ====== SYSTEM ======
    private Integer itemsPerPage;
    private Boolean allowRegistration;

    // ====== MAINTENANCE ======
    private Boolean maintenanceMode;
    private String maintenanceMessage;

    // ====== OTHER FIELDS ======
    private String paymentMethods;
    private String businessHours;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private String smtpEncryption;
    private String orderEmailSubject;
    private String orderEmailBody;
    private Long maxUploadSize;
    private String allowedImageTypes;
    private Boolean requireEmailVerification;
    private Boolean guestCheckout;
    private String googleAnalyticsId;
    private String facebookPixelId;
    private BigDecimal shippingTaxRate;
    private Integer estimatedDeliveryDays;
    private Boolean paymentTestMode;

    // Constructors
    public StoreSettingsRequest() {}

    // ============================================================
    // ✅ GETTERS AND SETTERS (ALL FIELDS)
    // ============================================================

    // ---- STORE INFO ----
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreDescription() { return storeDescription; }
    public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }

    // ✅ ADD THESE TWO METHODS!
    public String getStoreLogo() { return storeLogo; }
    public void setStoreLogo(String storeLogo) { this.storeLogo = storeLogo; }

    public String getStoreFavicon() { return storeFavicon; }
    public void setStoreFavicon(String storeFavicon) { this.storeFavicon = storeFavicon; }

    // ---- CONTACT ----
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactAddress() { return contactAddress; }
    public void setContactAddress(String contactAddress) { this.contactAddress = contactAddress; }

    public String getContactCity() { return contactCity; }
    public void setContactCity(String contactCity) { this.contactCity = contactCity; }

    public String getContactState() { return contactState; }
    public void setContactState(String contactState) { this.contactState = contactState; }

    public String getContactZip() { return contactZip; }
    public void setContactZip(String contactZip) { this.contactZip = contactZip; }

    public String getContactCountry() { return contactCountry; }
    public void setContactCountry(String contactCountry) { this.contactCountry = contactCountry; }

    // ---- SOCIAL MEDIA ----
    public String getFacebookUrl() { return facebookUrl; }
    public void setFacebookUrl(String facebookUrl) { this.facebookUrl = facebookUrl; }

    public String getInstagramUrl() { return instagramUrl; }
    public void setInstagramUrl(String instagramUrl) { this.instagramUrl = instagramUrl; }

    public String getTwitterUrl() { return twitterUrl; }
    public void setTwitterUrl(String twitterUrl) { this.twitterUrl = twitterUrl; }

    // ---- SHIPPING ----
    public BigDecimal getDefaultShippingCost() { return defaultShippingCost; }
    public void setDefaultShippingCost(BigDecimal defaultShippingCost) { this.defaultShippingCost = defaultShippingCost; }

    public BigDecimal getFreeShippingThreshold() { return freeShippingThreshold; }
    public void setFreeShippingThreshold(BigDecimal freeShippingThreshold) { this.freeShippingThreshold = freeShippingThreshold; }

    // ---- PAYMENT ----
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    // ---- SYSTEM ----
    public Integer getItemsPerPage() { return itemsPerPage; }
    public void setItemsPerPage(Integer itemsPerPage) { this.itemsPerPage = itemsPerPage; }

    public Boolean getAllowRegistration() { return allowRegistration; }
    public void setAllowRegistration(Boolean allowRegistration) { this.allowRegistration = allowRegistration; }

    // ---- MAINTENANCE ----
    public Boolean getMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(Boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public String getMaintenanceMessage() { return maintenanceMessage; }
    public void setMaintenanceMessage(String maintenanceMessage) { this.maintenanceMessage = maintenanceMessage; }

    // ---- OTHER ----
    public String getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(String paymentMethods) { this.paymentMethods = paymentMethods; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }

    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }

    public String getMetaKeywords() { return metaKeywords; }
    public void setMetaKeywords(String metaKeywords) { this.metaKeywords = metaKeywords; }

    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }

    public Integer getSmtpPort() { return smtpPort; }
    public void setSmtpPort(Integer smtpPort) { this.smtpPort = smtpPort; }

    public String getSmtpUsername() { return smtpUsername; }
    public void setSmtpUsername(String smtpUsername) { this.smtpUsername = smtpUsername; }

    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }

    public String getSmtpEncryption() { return smtpEncryption; }
    public void setSmtpEncryption(String smtpEncryption) { this.smtpEncryption = smtpEncryption; }

    public String getOrderEmailSubject() { return orderEmailSubject; }
    public void setOrderEmailSubject(String orderEmailSubject) { this.orderEmailSubject = orderEmailSubject; }

    public String getOrderEmailBody() { return orderEmailBody; }
    public void setOrderEmailBody(String orderEmailBody) { this.orderEmailBody = orderEmailBody; }

    public Long getMaxUploadSize() { return maxUploadSize; }
    public void setMaxUploadSize(Long maxUploadSize) { this.maxUploadSize = maxUploadSize; }

    public String getAllowedImageTypes() { return allowedImageTypes; }
    public void setAllowedImageTypes(String allowedImageTypes) { this.allowedImageTypes = allowedImageTypes; }

    public Boolean getRequireEmailVerification() { return requireEmailVerification; }
    public void setRequireEmailVerification(Boolean requireEmailVerification) { this.requireEmailVerification = requireEmailVerification; }

    public Boolean getGuestCheckout() { return guestCheckout; }
    public void setGuestCheckout(Boolean guestCheckout) { this.guestCheckout = guestCheckout; }

    public String getGoogleAnalyticsId() { return googleAnalyticsId; }
    public void setGoogleAnalyticsId(String googleAnalyticsId) { this.googleAnalyticsId = googleAnalyticsId; }

    public String getFacebookPixelId() { return facebookPixelId; }
    public void setFacebookPixelId(String facebookPixelId) { this.facebookPixelId = facebookPixelId; }

    public BigDecimal getShippingTaxRate() { return shippingTaxRate; }
    public void setShippingTaxRate(BigDecimal shippingTaxRate) { this.shippingTaxRate = shippingTaxRate; }

    public Integer getEstimatedDeliveryDays() { return estimatedDeliveryDays; }
    public void setEstimatedDeliveryDays(Integer estimatedDeliveryDays) { this.estimatedDeliveryDays = estimatedDeliveryDays; }

    public Boolean getPaymentTestMode() { return paymentTestMode; }
    public void setPaymentTestMode(Boolean paymentTestMode) { this.paymentTestMode = paymentTestMode; }
}