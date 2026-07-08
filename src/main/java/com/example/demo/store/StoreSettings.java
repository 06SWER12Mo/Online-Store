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

    // Store Information
    @Column(name = "store_name", length = 100)
    private String storeName;

    @Column(name = "store_description", length = 500)
    private String storeDescription;

    @Column(name = "store_logo", length = 255)
    private String storeLogo;

    @Column(name = "store_favicon", length = 255)
    private String storeFavicon;

    // Contact Information
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_address", length = 255)
    private String contactAddress;

    @Column(name = "contact_city", length = 50)
    private String contactCity;

    @Column(name = "contact_state", length = 50)
    private String contactState;

    @Column(name = "contact_zip", length = 20)
    private String contactZip;

    @Column(name = "contact_country", length = 50)
    private String contactCountry;

    // Social Media
    @Column(name = "facebook_url", length = 255)
    private String facebookUrl;

    @Column(name = "twitter_url", length = 255)
    private String twitterUrl;

    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;

    @Column(name = "youtube_url", length = 255)
    private String youtubeUrl;

    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    // Shipping Settings
    @Column(name = "free_shipping_threshold", precision = 10, scale = 2)
    private BigDecimal freeShippingThreshold;

    @Column(name = "default_shipping_cost", precision = 10, scale = 2)
    private BigDecimal defaultShippingCost;

    @Column(name = "shipping_tax_rate", precision = 5, scale = 2)
    private BigDecimal shippingTaxRate;

    @Column(name = "estimated_delivery_days")
    private Integer estimatedDeliveryDays;

    // Payment Settings
    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "currency_symbol", length = 5)
    private String currencySymbol;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "payment_methods")
    private String paymentMethods; // Comma separated: CREDIT_CARD,PAYPAL,BANK_TRANSFER

    @Column(name = "is_payment_test_mode")
    private boolean paymentTestMode = true;

    // Business Hours
    @Column(name = "business_hours", length = 500)
    private String businessHours;

    // SEO Settings
    @Column(name = "meta_title", length = 200)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    // Maintenance Mode
    @Column(name = "is_maintenance_mode")
    private boolean maintenanceMode = false;

    @Column(name = "maintenance_message", length = 500)
    private String maintenanceMessage;

    // Email Settings
    @Column(name = "smtp_host", length = 100)
    private String smtpHost;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_username", length = 100)
    private String smtpUsername;

    @Column(name = "smtp_password", length = 100)
    private String smtpPassword;

    @Column(name = "smtp_encryption", length = 10)
    private String smtpEncryption; // TLS, SSL

    @Column(name = "order_email_subject", length = 200)
    private String orderEmailSubject;

    @Column(name = "order_email_body", length = 1000)
    private String orderEmailBody;

    // System Settings
    @Column(name = "items_per_page")
    private Integer itemsPerPage = 20;

    @Column(name = "max_upload_size")
    private Long maxUploadSize = 5242880L; // 5MB

    @Column(name = "allowed_image_types", length = 255)
    private String allowedImageTypes; // jpg,png,gif,webp

    @Column(name = "is_allow_registration")
    private boolean allowRegistration = true;

    @Column(name = "is_require_email_verification")
    private boolean requireEmailVerification = true;

    @Column(name = "is_guest_checkout")
    private boolean guestCheckout = false;

    // Analytics
    @Column(name = "google_analytics_id", length = 50)
    private String googleAnalyticsId;

    @Column(name = "facebook_pixel_id", length = 50)
    private String facebookPixelId;

    // Tracking
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

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
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