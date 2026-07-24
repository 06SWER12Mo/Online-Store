package com.example.demo.service;

import com.example.demo.dto.request.StoreSettingsRequest;
import com.example.demo.dto.response.StoreSettingsResponse;
import com.example.demo.entity.StoreSettings;

public interface StoreSettingsService {

    // Get full settings (Admin only)
    StoreSettingsResponse getSettings();

    // Update settings (Admin only)
    StoreSettingsResponse updateSettings(StoreSettingsRequest request, String updatedBy);

    // Get public store info (no auth needed)
    StoreSettingsResponse getPublicStoreInfo();

    // Toggle maintenance mode
    void toggleMaintenanceMode(boolean enabled, String message);

    // NEW METHODS FOR DIRECT SETTINGS UPDATE
    StoreSettings getOrCreateSettings();
    StoreSettings saveSettings(StoreSettings settings);

    // Helper methods for other services
    boolean isMaintenanceMode();
    boolean isRegistrationAllowed();
    String getCurrencyCode();
    String getCurrencySymbol();
    double getDefaultShippingCost();
    double getFreeShippingThreshold();
    double getTaxRate();
    int getItemsPerPage();
}