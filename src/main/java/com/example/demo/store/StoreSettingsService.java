package com.example.demo.store;

import com.example.demo.store.dtos.StoreSettingsRequest;
import com.example.demo.store.dtos.StoreSettingsResponse;

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