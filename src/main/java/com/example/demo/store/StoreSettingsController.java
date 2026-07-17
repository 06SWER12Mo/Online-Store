package com.example.demo.store;

import org.springframework.http.MediaType;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/store-settings")
@Tag(name = "Store Settings and Information", description = "Endpoints for managing store settings")
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
    @Operation(summary = "Get public store info", description = "Retrieves publicly visible store information (name, branding, contact info, etc). No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Store info retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StoreSettingsResponse.class)))
    })
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> getPublicStoreInfo() {
        StoreSettingsResponse response = storeSettingsService.getPublicStoreInfo();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/public/maintenance")
    @Operation(summary = "Get maintenance mode status", description = "Returns whether the store is currently in maintenance mode. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Maintenance status retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Boolean>> getMaintenanceStatus() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.isMaintenanceMode()));
    }

    @GetMapping("/public/currency")
    @Operation(summary = "Get store currency code", description = "Retrieves the store's configured currency code (e.g. USD, EUR). No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Currency code retrieved successfully")
    })
    public ResponseEntity<ApiResponse<String>> getCurrencyCode() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getCurrencyCode()));
    }

    @GetMapping("/public/currency-symbol")
    @Operation(summary = "Get store currency symbol", description = "Retrieves the store's configured currency symbol (e.g. $, €). No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Currency symbol retrieved successfully")
    })
    public ResponseEntity<ApiResponse<String>> getCurrencySymbol() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getCurrencySymbol()));
    }

    @GetMapping("/public/shipping-cost")
    @Operation(summary = "Get default shipping cost", description = "Retrieves the store's default shipping cost. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shipping cost retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Double>> getDefaultShippingCost() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getDefaultShippingCost()));
    }

    @GetMapping("/public/free-shipping-threshold")
    @Operation(summary = "Get free shipping threshold", description = "Retrieves the order amount above which shipping is free. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Free shipping threshold retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Double>> getFreeShippingThreshold() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getFreeShippingThreshold()));
    }

    @GetMapping("/public/tax-rate")
    @Operation(summary = "Get store tax rate", description = "Retrieves the store's configured tax rate. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tax rate retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Double>> getTaxRate() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.getTaxRate()));
    }

    @GetMapping("/public/registration-allowed")
    @Operation(summary = "Check if registration is allowed", description = "Returns whether new user registration is currently allowed on the store. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration status retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Boolean>> isRegistrationAllowed() {
        return ResponseEntity.ok(ApiResponse.success(storeSettingsService.isRegistrationAllowed()));
    }

    // ========== ADMIN ENDPOINTS ==========

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get full store settings", description = "Retrieves the complete store settings configuration. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Store settings retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StoreSettingsResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role")
    })
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> getSettings() {
        StoreSettingsResponse response = storeSettingsService.getSettings();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update store settings", description = "Updates the store settings configuration. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store settings updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role")
    })
    public ResponseEntity<ApiResponse<StoreSettingsResponse>> updateSettings(
            @RequestBody StoreSettingsRequest request) {
        String username = getCurrentUsername();
        StoreSettingsResponse response = storeSettingsService.updateSettings(request, username);
        return ResponseEntity.ok(ApiResponse.success("Store settings updated successfully", response));
    }

    // ========== ✅ STORE LOGO - FIXED (URL auto-generated) ==========

    @PostMapping(value = "/logo", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload store logo",
            description = "Uploads a new store logo image and updates the store settings with its URL. Requires ADMIN or MANAGER role.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Store logo uploaded successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or missing file"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role")
    })
    public ResponseEntity<ApiResponse<ImageResponse>> uploadLogo(
            @Parameter(description = "Logo image file", required = true) @RequestParam("file") MultipartFile file) {
        
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete store logo", description = "Deletes the current store logo and clears its URL from settings. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store logo deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No logo currently set")
    })
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload store favicon",
            description = "Uploads a new store favicon image and updates the store settings with its URL. Requires ADMIN or MANAGER role.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Store favicon uploaded successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or missing file"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role")
    })
    public ResponseEntity<ApiResponse<ImageResponse>> uploadFavicon(
            @Parameter(description = "Favicon image file", required = true) @RequestParam("file") MultipartFile file) {
        
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete store favicon", description = "Deletes the current store favicon and clears its URL from settings. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store favicon deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No favicon currently set")
    })
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle maintenance mode", description = "Enables or disables store maintenance mode, optionally with a custom message. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Maintenance mode toggled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role")
    })
    public ResponseEntity<ApiResponse<Void>> toggleMaintenanceMode(
            @Parameter(description = "Whether maintenance mode should be enabled", required = true) @RequestParam boolean enabled,
            @Parameter(description = "Optional message to display to users while in maintenance mode") @RequestParam(required = false) String message) {
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