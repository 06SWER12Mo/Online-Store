package com.example.demo.store;

import com.example.demo.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store-settings")
public class StoreSettingsController {

    private final StoreSettingsService storeSettingsService;

    public StoreSettingsController(StoreSettingsService storeSettingsService) {
        this.storeSettingsService = storeSettingsService;
    }

    // Public endpoints (no authentication required)
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> getPublicStoreInfo() {
        StoreSettingsResponse response = storeSettingsService.getPublicStoreInfo();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/public/maintenance")
    public ResponseEntity<ApiResponse<Boolean>> getMaintenanceStatus() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.isMaintenanceMode()));
    }

    @GetMapping("/public/currency")
    public ResponseEntity<ApiResponse<String>> getCurrency() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getCurrencyCode()));
    }

    @GetMapping("/public/currency-symbol")
    public ResponseEntity<ApiResponse<String>> getCurrencySymbol() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getCurrencySymbol()));
    }

    @GetMapping("/public/shipping-cost")
    public ResponseEntity<ApiResponse<Double>> getDefaultShippingCost() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getDefaultShippingCost()));
    }

    @GetMapping("/public/free-shipping-threshold")
    public ResponseEntity<ApiResponse<Double>> getFreeShippingThreshold() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getFreeShippingThreshold()));
    }

    @GetMapping("/public/tax-rate")
    public ResponseEntity<ApiResponse<Double>> getTaxRate() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getTaxRate()));
    }

    @GetMapping("/public/items-per-page")
    public ResponseEntity<ApiResponse<Integer>> getItemsPerPage() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getItemsPerPage()));
    }

    @GetMapping("/public/registration-allowed")
    public ResponseEntity<ApiResponse<Boolean>> isRegistrationAllowed() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.isRegistrationAllowed()));
    }

    // Admin endpoints (authentication required)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> getSettings() {
        StoreSettingsResponse response = storeSettingsService.getSettings();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> updateSettings(
            @RequestBody StoreSettingsRequest request) {
        String username = getCurrentUsername();
        StoreSettingsResponse response = storeSettingsService.updateSettings(request, username);
        return ResponseEntity.ok(ApiResponse.success("Store settings updated successfully", response));
    }

    @PatchMapping("/maintenance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Void>> toggleMaintenanceMode(
            @RequestParam boolean enabled,
            @RequestParam(required = false) String message) {
        storeSettingsService.toggleMaintenanceMode(enabled, message);
        String status = enabled ? "enabled" : "disabled";
        return ResponseEntity.ok(ApiResponse.success("Maintenance mode " + status));
    }

    // Helper method
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "SYSTEM";
    }
}