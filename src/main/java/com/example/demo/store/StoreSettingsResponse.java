package com.example.demo.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StoreSettingsResponse {

    private Long id;
    private String storeName;
    private String storeDescription;
    private String storeLogo;
    private String storeFavicon;
    private String contactEmail;
    private String contactPhone;
    private String contactAddress;
    private String contactCity;
    private String contactState;
    private String contactZip;
    private String contactCountry;
    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String youtubeUrl;
    private String linkedinUrl;
    private BigDecimal freeShippingThreshold;
    private BigDecimal defaultShippingCost;
    private BigDecimal shippingTaxRate;
    private Integer estimatedDeliveryDays;
    private String currencyCode;
    private String currencySymbol;
    private BigDecimal taxRate;
    private String paymentMethods;
    private boolean paymentTestMode;
    private String businessHours;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private boolean maintenanceMode;
    private String maintenanceMessage;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private String smtpEncryption;
    private String orderEmailSubject;
    private String orderEmailBody;
    private Integer itemsPerPage;
    private Long maxUploadSize;
    private String allowedImageTypes;
    private boolean allowRegistration;
    private boolean requireEmailVerification;
    private boolean guestCheckout;
    private String googleAnalyticsId;
    private String facebookPixelId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Constructors
    public StoreSettingsResponse() {}

    public StoreSettingsResponse(StoreSettings settings) {
        this.id = settings.getId();
        this.storeName = settings.getStoreName();
        this.storeDescription = settings.getStoreDescription();
        this.storeLogo = settings.getStoreLogo();
        this.storeFavicon = settings.getStoreFavicon();
        this.contactEmail = settings.getContactEmail();
        this.contactPhone = settings.getContactPhone();
        this.contactAddress = settings.getContactAddress();
        this.contactCity = settings.getContactCity();
        this.contactState = settings.getContactState();
        this.contactZip = settings.getContactZip();
        this.contactCountry = settings.getContactCountry();
        this.facebookUrl = settings.getFacebookUrl();
        this.twitterUrl = settings.getTwitterUrl();
        this.instagramUrl = settings.getInstagramUrl();
        this.youtubeUrl = settings.getYoutubeUrl();
        this.linkedinUrl = settings.getLinkedinUrl();
        this.freeShippingThreshold = settings.getFreeShippingThreshold();
        this.defaultShippingCost = settings.getDefaultShippingCost();
        this.shippingTaxRate = settings.getShippingTaxRate();
        this.estimatedDeliveryDays = settings.getEstimatedDeliveryDays();
        this.currencyCode = settings.getCurrencyCode();
        this.currencySymbol = settings.getCurrencySymbol();
        this.taxRate = settings.getTaxRate();
        this.paymentMethods = settings.getPaymentMethods();
        this.paymentTestMode = settings.isPaymentTestMode();
        this.businessHours = settings.getBusinessHours();
        this.metaTitle = settings.getMetaTitle();
        this.metaDescription = settings.getMetaDescription();
        this.metaKeywords = settings.getMetaKeywords();
        this.maintenanceMode = settings.isMaintenanceMode();
        this.maintenanceMessage = settings.getMaintenanceMessage();
        this.smtpHost = settings.getSmtpHost();
        this.smtpPort = settings.getSmtpPort();
        this.smtpUsername = settings.getSmtpUsername();
        this.smtpEncryption = settings.getSmtpEncryption();
        this.orderEmailSubject = settings.getOrderEmailSubject();
        this.orderEmailBody = settings.getOrderEmailBody();
        this.itemsPerPage = settings.getItemsPerPage();
        this.maxUploadSize = settings.getMaxUploadSize();
        this.allowedImageTypes = settings.getAllowedImageTypes();
        this.allowRegistration = settings.isAllowRegistration();
        this.requireEmailVerification = settings.isRequireEmailVerification();
        this.guestCheckout = settings.isGuestCheckout();
        this.googleAnalyticsId = settings.getGoogleAnalyticsId();
        this.facebookPixelId = settings.getFacebookPixelId();
        this.createdAt = settings.getCreatedAt();
        this.updatedAt = settings.getUpdatedAt();
        this.updatedBy = settings.getUpdatedBy();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getStoreFavicon() {
        return storeFavicon;
    }

    public void setStoreFavicon(String storeFavicon) {
        this.storeFavicon = storeFavicon;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactCity() {
        return contactCity;
    }

    public void setContactCity(String contactCity) {
        this.contactCity = contactCity;
    }

    public String getContactState() {
        return contactState;
    }

    public void setContactState(String contactState) {
        this.contactState = contactState;
    }

    public String getContactZip() {
        return contactZip;
    }

    public void setContactZip(String contactZip) {
        this.contactZip = contactZip;
    }

    public String getContactCountry() {
        return contactCountry;
    }

    public void setContactCountry(String contactCountry) {
        this.contactCountry = contactCountry;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public BigDecimal getFreeShippingThreshold() {
        return freeShippingThreshold;
    }

    public void setFreeShippingThreshold(BigDecimal freeShippingThreshold) {
        this.freeShippingThreshold = freeShippingThreshold;
    }

    public BigDecimal getDefaultShippingCost() {
        return defaultShippingCost;
    }

    public void setDefaultShippingCost(BigDecimal defaultShippingCost) {
        this.defaultShippingCost = defaultShippingCost;
    }

    public BigDecimal getShippingTaxRate() {
        return shippingTaxRate;
    }

    public void setShippingTaxRate(BigDecimal shippingTaxRate) {
        this.shippingTaxRate = shippingTaxRate;
    }

    public Integer getEstimatedDeliveryDays() {
        return estimatedDeliveryDays;
    }

    public void setEstimatedDeliveryDays(Integer estimatedDeliveryDays) {
        this.estimatedDeliveryDays = estimatedDeliveryDays;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(String paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public boolean isPaymentTestMode() {
        return paymentTestMode;
    }

    public void setPaymentTestMode(boolean paymentTestMode) {
        this.paymentTestMode = paymentTestMode;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    public String getMaintenanceMessage() {
        return maintenanceMessage;
    }

    public void setMaintenanceMessage(String maintenanceMessage) {
        this.maintenanceMessage = maintenanceMessage;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public Integer getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public String getSmtpEncryption() {
        return smtpEncryption;
    }

    public void setSmtpEncryption(String smtpEncryption) {
        this.smtpEncryption = smtpEncryption;
    }

    public String getOrderEmailSubject() {
        return orderEmailSubject;
    }

    public void setOrderEmailSubject(String orderEmailSubject) {
        this.orderEmailSubject = orderEmailSubject;
    }

    public String getOrderEmailBody() {
        return orderEmailBody;
    }

    public void setOrderEmailBody(String orderEmailBody) {
        this.orderEmailBody = orderEmailBody;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public Long getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(Long maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }

    public String getAllowedImageTypes() {
        return allowedImageTypes;
    }

    public void setAllowedImageTypes(String allowedImageTypes) {
        this.allowedImageTypes = allowedImageTypes;
    }

    public boolean isAllowRegistration() {
        return allowRegistration;
    }

    public void setAllowRegistration(boolean allowRegistration) {
        this.allowRegistration = allowRegistration;
    }

    public boolean isRequireEmailVerification() {
        return requireEmailVerification;
    }

    public void setRequireEmailVerification(boolean requireEmailVerification) {
        this.requireEmailVerification = requireEmailVerification;
    }

    public boolean isGuestCheckout() {
        return guestCheckout;
    }

    public void setGuestCheckout(boolean guestCheckout) {
        this.guestCheckout = guestCheckout;
    }

    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    public void setGoogleAnalyticsId(String googleAnalyticsId) {
        this.googleAnalyticsId = googleAnalyticsId;
    }

    public String getFacebookPixelId() {
        return facebookPixelId;
    }

    public void setFacebookPixelId(String facebookPixelId) {
        this.facebookPixelId = facebookPixelId;
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}