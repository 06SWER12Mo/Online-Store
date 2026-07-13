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

    // ========== ✅ STORE LOGO ==========

    @PostMapping("/logo")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ImageResponse>> uploadLogo(
            @RequestParam("file") MultipartFile file) {
        
        // ✅ Upload logo
        ImageResponse response = imageService.uploadStoreLogo(file);
        
        // ✅ Update settings with logo URL
        StoreSettingsRequest request = new StoreSettingsRequest();
        String logoUrl = response != null ? response.getImageUrl() : null;
        request.setStoreLogo(logoUrl);
        
        // ✅ Only update if URL is not null
        if (logoUrl != null) {
            storeSettingsService.updateSettings(request, getCurrentUsername());
        }
        
        return ResponseEntity.ok(ApiResponse.success("Store logo uploaded successfully", response));
    }

    @DeleteMapping("/logo")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteLogo() {
        // ✅ Delete logo file
        imageService.deleteStoreLogo();
        
        // ✅ Remove logo URL from settings
        StoreSettingsRequest request = new StoreSettingsRequest();
        request.setStoreLogo(null);
        storeSettingsService.updateSettings(request, getCurrentUsername());
        
        return ResponseEntity.ok(ApiResponse.success("Store logo deleted successfully"));
    }

    // ========== ✅ STORE FAVICON ==========

    @PostMapping("/favicon")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ImageResponse>> uploadFavicon(
            @RequestParam("file") MultipartFile file) {
        
        // ✅ Upload favicon
        ImageResponse response = imageService.uploadStoreFavicon(file);
        
        // ✅ Update settings with favicon URL
        StoreSettingsRequest request = new StoreSettingsRequest();
        String faviconUrl = response != null ? response.getImageUrl() : null;
        request.setStoreFavicon(faviconUrl);
        
        // ✅ Only update if URL is not null
        if (faviconUrl != null) {
            storeSettingsService.updateSettings(request, getCurrentUsername());
        }
        
        return ResponseEntity.ok(ApiResponse.success("Store favicon uploaded successfully", response));
    }

    @DeleteMapping("/favicon")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteFavicon() {
        // ✅ Delete favicon file
        imageService.deleteStoreFavicon();
        
        // ✅ Remove favicon URL from settings
        StoreSettingsRequest request = new StoreSettingsRequest();
        request.setStoreFavicon(null);
        storeSettingsService.updateSettings(request, getCurrentUsername());
        
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