package com.example.demo.store;

import com.example.demo.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StoreSettingsServiceImpl implements StoreSettingsService {

    private final StoreSettingsRepository storeSettingsRepository;

    public StoreSettingsServiceImpl(StoreSettingsRepository storeSettingsRepository) {
        this.storeSettingsRepository = storeSettingsRepository;
    }

    @Override
    public StoreSettingsResponse getSettings() {
        StoreSettings settings = getOrCreateSettings();
        return new StoreSettingsResponse(settings);
    }

    @Override
    public StoreSettingsResponse updateSettings(StoreSettingsRequest request, String updatedBy) {
        StoreSettings settings = getOrCreateSettings();

        // Store Information
        if (request.getStoreName() != null) {
            settings.setStoreName(request.getStoreName());
        }
        if (request.getStoreDescription() != null) {
            settings.setStoreDescription(request.getStoreDescription());
        }
        if (request.getStoreLogo() != null) {
            settings.setStoreLogo(request.getStoreLogo());
        }
        if (request.getStoreFavicon() != null) {
            settings.setStoreFavicon(request.getStoreFavicon());
        }

        // Contact Information
        if (request.getContactEmail() != null) {
            settings.setContactEmail(request.getContactEmail());
        }
        if (request.getContactPhone() != null) {
            settings.setContactPhone(request.getContactPhone());
        }
        if (request.getContactAddress() != null) {
            settings.setContactAddress(request.getContactAddress());
        }
        if (request.getContactCity() != null) {
            settings.setContactCity(request.getContactCity());
        }
        if (request.getContactState() != null) {
            settings.setContactState(request.getContactState());
        }
        if (request.getContactZip() != null) {
            settings.setContactZip(request.getContactZip());
        }
        if (request.getContactCountry() != null) {
            settings.setContactCountry(request.getContactCountry());
        }

        // Social Media
        if (request.getFacebookUrl() != null) {
            settings.setFacebookUrl(request.getFacebookUrl());
        }
        if (request.getTwitterUrl() != null) {
            settings.setTwitterUrl(request.getTwitterUrl());
        }
        if (request.getInstagramUrl() != null) {
            settings.setInstagramUrl(request.getInstagramUrl());
        }
        if (request.getYoutubeUrl() != null) {
            settings.setYoutubeUrl(request.getYoutubeUrl());
        }
        if (request.getLinkedinUrl() != null) {
            settings.setLinkedinUrl(request.getLinkedinUrl());
        }

        // Shipping Settings
        if (request.getFreeShippingThreshold() != null) {
            settings.setFreeShippingThreshold(request.getFreeShippingThreshold());
        }
        if (request.getDefaultShippingCost() != null) {
            settings.setDefaultShippingCost(request.getDefaultShippingCost());
        }
        if (request.getShippingTaxRate() != null) {
            settings.setShippingTaxRate(request.getShippingTaxRate());
        }
        if (request.getEstimatedDeliveryDays() != null) {
            settings.setEstimatedDeliveryDays(request.getEstimatedDeliveryDays());
        }

        // Payment Settings
        if (request.getCurrencyCode() != null) {
            settings.setCurrencyCode(request.getCurrencyCode());
        }
        if (request.getCurrencySymbol() != null) {
            settings.setCurrencySymbol(request.getCurrencySymbol());
        }
        if (request.getTaxRate() != null) {
            settings.setTaxRate(request.getTaxRate());
        }
        if (request.getPaymentMethods() != null) {
            settings.setPaymentMethods(request.getPaymentMethods());
        }
        if (request.getPaymentTestMode() != null) {
            settings.setPaymentTestMode(request.getPaymentTestMode());
        }

        // Business Hours
        if (request.getBusinessHours() != null) {
            settings.setBusinessHours(request.getBusinessHours());
        }

        // SEO Settings
        if (request.getMetaTitle() != null) {
            settings.setMetaTitle(request.getMetaTitle());
        }
        if (request.getMetaDescription() != null) {
            settings.setMetaDescription(request.getMetaDescription());
        }
        if (request.getMetaKeywords() != null) {
            settings.setMetaKeywords(request.getMetaKeywords());
        }

        // Maintenance Mode
        if (request.getMaintenanceMode() != null) {
            settings.setMaintenanceMode(request.getMaintenanceMode());
        }
        if (request.getMaintenanceMessage() != null) {
            settings.setMaintenanceMessage(request.getMaintenanceMessage());
        }

        // Email Settings
        if (request.getSmtpHost() != null) {
            settings.setSmtpHost(request.getSmtpHost());
        }
        if (request.getSmtpPort() != null) {
            settings.setSmtpPort(request.getSmtpPort());
        }
        if (request.getSmtpUsername() != null) {
            settings.setSmtpUsername(request.getSmtpUsername());
        }
        if (request.getSmtpPassword() != null) {
            settings.setSmtpPassword(request.getSmtpPassword());
        }
        if (request.getSmtpEncryption() != null) {
            settings.setSmtpEncryption(request.getSmtpEncryption());
        }
        if (request.getOrderEmailSubject() != null) {
            settings.setOrderEmailSubject(request.getOrderEmailSubject());
        }
        if (request.getOrderEmailBody() != null) {
            settings.setOrderEmailBody(request.getOrderEmailBody());
        }

        // System Settings
        if (request.getItemsPerPage() != null) {
            settings.setItemsPerPage(request.getItemsPerPage());
        }
        if (request.getMaxUploadSize() != null) {
            settings.setMaxUploadSize(request.getMaxUploadSize());
        }
        if (request.getAllowedImageTypes() != null) {
            settings.setAllowedImageTypes(request.getAllowedImageTypes());
        }
        if (request.getAllowRegistration() != null) {
            settings.setAllowRegistration(request.getAllowRegistration());
        }
        if (request.getRequireEmailVerification() != null) {
            settings.setRequireEmailVerification(request.getRequireEmailVerification());
        }
        if (request.getGuestCheckout() != null) {
            settings.setGuestCheckout(request.getGuestCheckout());
        }

        // Analytics
        if (request.getGoogleAnalyticsId() != null) {
            settings.setGoogleAnalyticsId(request.getGoogleAnalyticsId());
        }
        if (request.getFacebookPixelId() != null) {
            settings.setFacebookPixelId(request.getFacebookPixelId());
        }

        settings.setUpdatedBy(updatedBy);
        StoreSettings updatedSettings = storeSettingsRepository.save(settings);
        return new StoreSettingsResponse(updatedSettings);
    }

    @Override
    public void toggleMaintenanceMode(boolean enabled, String message) {
        StoreSettings settings = getOrCreateSettings();
        settings.setMaintenanceMode(enabled);
        if (message != null) {
            settings.setMaintenanceMessage(message);
        }
        storeSettingsRepository.save(settings);
    }

    @Override
    public StoreSettingsResponse getPublicStoreInfo() {
        StoreSettings settings = getOrCreateSettings();
        StoreSettingsResponse response = new StoreSettingsResponse(settings);
        // Remove sensitive fields for public
        response.setSmtpHost(null);
        response.setSmtpPort(null);
        response.setSmtpUsername(null);
        response.setSmtpEncryption(null);
        response.setOrderEmailBody(null);
        response.setUpdatedBy(null);
        return response;
    }

    @Override
    public boolean isMaintenanceMode() {
        try {
            return storeSettingsRepository.getMaintenanceMode();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isRegistrationAllowed() {
        try {
            return storeSettingsRepository.getAllowRegistration();
        } catch (Exception e) {
            return true; // Default to true if settings not found
        }
    }

    @Override
    public String getCurrencyCode() {
        StoreSettings settings = getOrCreateSettings();
        return settings.getCurrencyCode() != null ? settings.getCurrencyCode() : "USD";
    }

    @Override
    public String getCurrencySymbol() {
        StoreSettings settings = getOrCreateSettings();
        return settings.getCurrencySymbol() != null ? settings.getCurrencySymbol() : "$";
    }

    @Override
    public double getDefaultShippingCost() {
        StoreSettings settings = getOrCreateSettings();
        return settings.getDefaultShippingCost() != null ? settings.getDefaultShippingCost().doubleValue() : 0.0;
    }

    @Override
    public double getFreeShippingThreshold() {
        StoreSettings settings = getOrCreateSettings();
        return settings.getFreeShippingThreshold() != null ? settings.getFreeShippingThreshold().doubleValue() : 50.0;
    }

    @Override
    public double getTaxRate() {
        StoreSettings settings = getOrCreateSettings();
        return settings.getTaxRate() != null ? settings.getTaxRate().doubleValue() : 0.0;
    }

    @Override
    public int getItemsPerPage() {
        StoreSettings settings = getOrCreateSettings();
        return settings.getItemsPerPage() != null ? settings.getItemsPerPage() : 20;
    }

    // Helper method
    private StoreSettings getOrCreateSettings() {
        return storeSettingsRepository.findFirst()
                .orElseGet(() -> {
                    StoreSettings newSettings = new StoreSettings();
                    newSettings.setStoreName("My Online Store");
                    newSettings.setCurrencyCode("USD");
                    newSettings.setCurrencySymbol("$");
                    newSettings.setItemsPerPage(20);
                    newSettings.setAllowRegistration(true);
                    newSettings.setRequireEmailVerification(true);
                    newSettings.setPaymentTestMode(true);
                    return storeSettingsRepository.save(newSettings);
                });
    }
}