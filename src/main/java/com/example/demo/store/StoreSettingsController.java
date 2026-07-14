package com.example.demo.store;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.image.ImageService;
import com.example.demo.image.dtos.ImageResponse;
import com.example.demo.store.dtos.StoreSettingsRequest;
import com.example.demo.store.dtos.StoreSettingsResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/store-settings")
@Tag(name = "Store Settings", description = "Endpoints for managing store settings")
public class StoreSettingsController {

    private final StoreSettingsService storeSettingsService;
    private final ImageService imageService;

    public StoreSettingsController(StoreSettingsService storeSettingsService,
                                   ImageService imageService) {
        this.storeSettingsService = storeSettingsService;
        this.imageService = imageService;
    }

    // ========== PUBLIC ENDPOINTS ==========

    @GetMapping("/public")
    @Tag(name = "Public Store Info", description = "Get public store information")
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> getPublicStoreInfo() {
        StoreSettingsResponse response = storeSettingsService.getPublicStoreInfo();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/public/maintenance")
    public ResponseEntity<ApiResponse<Boolean>> getMaintenanceStatus() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.isMaintenanceMode()));
    }

    @GetMapping("/public/currency")
    public ResponseEntity<ApiResponse<String>> getCurrencyCode() {
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

    @GetMapping("/public/registration-allowed")
    public ResponseEntity<ApiResponse<Boolean>> isRegistrationAllowed() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.isRegistrationAllowed()));
    }

    // ========== ADMIN ENDPOINTS ==========

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> getSettings() {
        StoreSettingsResponse response = storeSettingsService.getSettings();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> updateSettings(
            @RequestBody StoreSettingsRequest request) {
        String username = getCurrentUsername();
        StoreSettingsResponse response = storeSettingsService.updateSettings(request, username);
        return ResponseEntity.ok(ApiResponse.success("Store settings updated successfully", response));
    }

    // ========== ✅ STORE LOGO - FIXED (URL auto-generated) ==========

    @PostMapping(value = "/logo", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ImageResponse>> uploadLogo(
            @RequestParam("file") MultipartFile file) {
        
        String username = getCurrentUsername();
        
        // 1. Upload logo - ImageService handles file saving and URL generation
        ImageResponse response = imageService.uploadStoreLogo(file);
        
        // 2. The URL is already generated in ImageService
        //    We just need to save it to settings
        if (response != null && response.getImageUrl() != null) {
            // Update the store settings with the new logo URL
            StoreSettings settings = storeSettingsService.getOrCreateSettings();
            settings.setStoreLogo(response.getImageUrl());
            settings.setUpdatedBy(username);
            storeSettingsService.saveSettings(settings);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Store logo uploaded successfully", response));
    }

    @DeleteMapping("/logo")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteLogo() {
        String username = getCurrentUsername();
        
        // 1. Delete the logo file
        imageService.deleteStoreLogo();
        
        // 2. Remove logo URL from settings
        StoreSettings settings = storeSettingsService.getOrCreateSettings();
        settings.setStoreLogo(null);
        settings.setUpdatedBy(username);
        storeSettingsService.saveSettings(settings);
        
        return ResponseEntity.ok(ApiResponse.success("Store logo deleted successfully"));
    }

    // ========== ✅ STORE FAVICON - FIXED (URL auto-generated) ==========

    @PostMapping(value = "/favicon", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ImageResponse>> uploadFavicon(
            @RequestParam("file") MultipartFile file) {
        
        String username = getCurrentUsername();
        
        // 1. Upload favicon - ImageService handles file saving and URL generation
        ImageResponse response = imageService.uploadStoreFavicon(file);
        
        // 2. The URL is already generated in ImageService
        //    We just need to save it to settings
        if (response != null && response.getImageUrl() != null) {
            // Update the store settings with the new favicon URL
            StoreSettings settings = storeSettingsService.getOrCreateSettings();
            settings.setStoreFavicon(response.getImageUrl());
            settings.setUpdatedBy(username);
            storeSettingsService.saveSettings(settings);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Store favicon uploaded successfully", response));
    }

    @DeleteMapping("/favicon")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteFavicon() {
        String username = getCurrentUsername();
        
        // 1. Delete the favicon file
        imageService.deleteStoreFavicon();
        
        // 2. Remove favicon URL from settings
        StoreSettings settings = storeSettingsService.getOrCreateSettings();
        settings.setStoreFavicon(null);
        settings.setUpdatedBy(username);
        storeSettingsService.saveSettings(settings);
        
        return ResponseEntity.ok(ApiResponse.success("Store favicon deleted successfully"));
    }

    @PatchMapping("/maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> toggleMaintenanceMode(
            @RequestParam boolean enabled,
            @RequestParam(required = false) String message) {
        storeSettingsService.toggleMaintenanceMode(enabled, message);
        String status = enabled ? "enabled" : "disabled";
        return ResponseEntity.ok(ApiResponse.success("Maintenance mode " + status));
    }

    // ========== HELPER ==========

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "SYSTEM";
    }
}