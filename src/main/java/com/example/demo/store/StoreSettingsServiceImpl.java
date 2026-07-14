package com.example.demo.store;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.store.dtos.StoreSettingsRequest;
import com.example.demo.store.dtos.StoreSettingsResponse;

@Service
@Transactional
public class StoreSettingsServiceImpl implements StoreSettingsService {

    private final StoreSettingsRepository storeSettingsRepository;

    public StoreSettingsServiceImpl(StoreSettingsRepository storeSettingsRepository) {
        this.storeSettingsRepository = storeSettingsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StoreSettingsResponse getSettings() {
        StoreSettings settings = getOrCreateSettings();
        return new StoreSettingsResponse(settings);
    }

    @Override
    public StoreSettingsResponse updateSettings(StoreSettingsRequest request, String updatedBy) {
        StoreSettings settings = getOrCreateSettings();

        // Store Info
        if (request.getStoreName() != null) settings.setStoreName(request.getStoreName());
        if (request.getStoreDescription() != null) settings.setStoreDescription(request.getStoreDescription());
        if (request.getStoreLogo() != null) settings.setStoreLogo(request.getStoreLogo());
        if (request.getStoreFavicon() != null) settings.setStoreFavicon(request.getStoreFavicon());

        // Contact
        if (request.getContactEmail() != null) settings.setContactEmail(request.getContactEmail());
        if (request.getContactPhone() != null) settings.setContactPhone(request.getContactPhone());
        if (request.getContactAddress() != null) settings.setContactAddress(request.getContactAddress());

        // Social Media
        if (request.getFacebookUrl() != null) settings.setFacebookUrl(request.getFacebookUrl());
        if (request.getInstagramUrl() != null) settings.setInstagramUrl(request.getInstagramUrl());
        if (request.getTwitterUrl() != null) settings.setTwitterUrl(request.getTwitterUrl());

        // Shipping
        if (request.getDefaultShippingCost() != null) settings.setDefaultShippingCost(request.getDefaultShippingCost());
        if (request.getFreeShippingThreshold() != null) settings.setFreeShippingThreshold(request.getFreeShippingThreshold());

        // Payment
        if (request.getCurrencyCode() != null) settings.setCurrencyCode(request.getCurrencyCode());
        if (request.getCurrencySymbol() != null) settings.setCurrencySymbol(request.getCurrencySymbol());
        if (request.getTaxRate() != null) settings.setTaxRate(request.getTaxRate());

        // System
        if (request.getItemsPerPage() != null) settings.setItemsPerPage(request.getItemsPerPage());
        if (request.getAllowRegistration() != null) settings.setAllowRegistration(request.getAllowRegistration());

        settings.setUpdatedBy(updatedBy);
        StoreSettings updated = storeSettingsRepository.save(settings);

        return new StoreSettingsResponse(updated);
    }

    @Override
    public StoreSettingsResponse getPublicStoreInfo() {
        StoreSettings settings = getOrCreateSettings();
        StoreSettingsResponse response = new StoreSettingsResponse(settings);
        // Remove sensitive fields for public
        response.setUpdatedBy(null);
        return response;
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

    // ========== NEW METHODS ==========

    @Override
    public StoreSettings getOrCreateSettings() {
        return storeSettingsRepository.findFirst()
                .orElseGet(() -> {
                    StoreSettings newSettings = new StoreSettings();
                    newSettings.setStoreName("My Store");
                    newSettings.setCurrencyCode("ILS");
                    newSettings.setCurrencySymbol("₪");
                    newSettings.setItemsPerPage(20);
                    newSettings.setAllowRegistration(true);
                    return storeSettingsRepository.save(newSettings);
                });
    }

    @Override
    public StoreSettings saveSettings(StoreSettings settings) {
        return storeSettingsRepository.save(settings);
    }

    // ========== HELPER METHODS ==========

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
            return true;
        }
    }

    @Override
    public String getCurrencyCode() {
        StoreSettings settings = getOrCreateSettings();
        return settings.getCurrencyCode() != null ? settings.getCurrencyCode() : "ILS";
    }

    @Override
    public String getCurrencySymbol() {
        StoreSettings settings = getOrCreateSettings();
        return settings.getCurrencySymbol() != null ? settings.getCurrencySymbol() : "₪";
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
}