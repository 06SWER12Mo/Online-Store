package com.example.demo.store;

public interface StoreSettingsService {

    // Get settings
    StoreSettingsResponse getSettings();

    // Update settings
    StoreSettingsResponse updateSettings(StoreSettingsRequest request, String updatedBy);

    // Maintenance mode
    void toggleMaintenanceMode(boolean enabled, String message);

    // Get store info (for public use)
    StoreSettingsResponse getPublicStoreInfo();

    // Check if maintenance mode is enabled
    boolean isMaintenanceMode();

    // Check if registration is allowed
    boolean isRegistrationAllowed();

    // Get currency settings
    String getCurrencyCode();
    String getCurrencySymbol();

    // Get shipping settings
    double getDefaultShippingCost();
    double getFreeShippingThreshold();

    // Get tax rate
    double getTaxRate();

    // Get items per page
    int getItemsPerPage();
}