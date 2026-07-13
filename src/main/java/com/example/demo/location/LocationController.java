package com.example.demo.location;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.location.dtos.BigAreaRequest;
import com.example.demo.location.dtos.BigAreaResponse;
import com.example.demo.location.dtos.DeliveryAddressRequest;
import com.example.demo.location.dtos.DeliveryAddressResponse;
import com.example.demo.location.dtos.TownRequest;
import com.example.demo.location.dtos.TownResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Tag(name = "Location", description = "Endpoints for managing big areas, towns, and user delivery addresses")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // ========== BIG AREA ENDPOINTS ==========

    @PostMapping("/big-areas")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a big area", description = "Creates a new big area (top-level geographic region). Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Big area created successfully",
                    content = @Content(schema = @Schema(implementation = BigAreaResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<BigAreaResponse> createBigArea(@Valid @RequestBody BigAreaRequest request) {
        BigAreaResponse response = locationService.createBigArea(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/big-areas/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a big area", description = "Updates the big area identified by the given id. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Big area updated successfully",
                    content = @Content(schema = @Schema(implementation = BigAreaResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<BigAreaResponse> updateBigArea(
            @Parameter(description = "ID of the big area to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody BigAreaRequest request) {
        BigAreaResponse response = locationService.updateBigArea(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/big-areas/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a big area", description = "Deletes the big area identified by the given id. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Big area deleted successfully", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<Void> deleteBigArea(
            @Parameter(description = "ID of the big area to delete", required = true)
            @PathVariable Long id) {
        locationService.deleteBigArea(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/big-areas/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle big area active status", description = "Flips the active/inactive status of the big area identified by the given id. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status toggled successfully", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<Void> toggleBigAreaActive(
            @Parameter(description = "ID of the big area to toggle", required = true)
            @PathVariable Long id) {
        locationService.toggleBigAreaActive(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/big-areas")
    @Operation(summary = "Get all big areas", description = "Returns all big areas.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Big areas retrieved successfully")
    public ResponseEntity<List<BigAreaResponse>> getAllBigAreas() {
        return ResponseEntity.ok(locationService.getAllBigAreas());
    }

    @GetMapping("/big-areas/active")
    @Operation(summary = "Get active big areas", description = "Returns all big areas that are currently active.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active big areas retrieved successfully")
    public ResponseEntity<List<BigAreaResponse>> getActiveBigAreas() {
        return ResponseEntity.ok(locationService.getActiveBigAreas());
    }

    @GetMapping("/big-areas/{id}")
    @Operation(summary = "Get big area by id", description = "Returns the big area identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Big area retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BigAreaResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<BigAreaResponse> getBigAreaById(
            @Parameter(description = "ID of the big area", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(locationService.getBigAreaById(id));
    }

    @GetMapping("/big-areas/search")
    @Operation(summary = "Search big areas", description = "Searches big areas by matching the given keyword against name/description.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<BigAreaResponse>> searchBigAreas(
            @Parameter(description = "Keyword to search for", required = true, example = "north")
            @RequestParam String keyword) {
        return ResponseEntity.ok(locationService.searchBigAreas(keyword));
    }

    @GetMapping("/big-areas/{id}/town-count")
    @Operation(summary = "Count towns in a big area", description = "Returns the number of towns belonging to the given big area.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<Long> countTownsByBigArea(
            @Parameter(description = "ID of the big area", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(locationService.countTownsByBigArea(id));
    }

    // ========== TOWN ENDPOINTS ==========

    @PostMapping("/towns")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a town", description = "Creates a new town under a big area. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Town created successfully",
                    content = @Content(schema = @Schema(implementation = TownResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent big area not found", content = @Content)
    })
    public ResponseEntity<TownResponse> createTown(@Valid @RequestBody TownRequest request) {
        TownResponse response = locationService.createTown(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/towns/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a town", description = "Updates the town identified by the given id. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Town updated successfully",
                    content = @Content(schema = @Schema(implementation = TownResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Town not found", content = @Content)
    })
    public ResponseEntity<TownResponse> updateTown(
            @Parameter(description = "ID of the town to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody TownRequest request) {
        TownResponse response = locationService.updateTown(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/towns/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a town", description = "Deletes the town identified by the given id. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Town deleted successfully", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Town not found", content = @Content)
    })
    public ResponseEntity<Void> deleteTown(
            @Parameter(description = "ID of the town to delete", required = true)
            @PathVariable Long id) {
        locationService.deleteTown(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/towns/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle town active status", description = "Flips the active/inactive status of the town identified by the given id. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status toggled successfully", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Town not found", content = @Content)
    })
    public ResponseEntity<Void> toggleTownActive(
            @Parameter(description = "ID of the town to toggle", required = true)
            @PathVariable Long id) {
        locationService.toggleTownActive(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/towns/{id}/toggle-delivery")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle town delivery availability", description = "Flips whether delivery is available for the town identified by the given id. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Delivery availability toggled successfully", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Town not found", content = @Content)
    })
    public ResponseEntity<Void> toggleTownDeliveryAvailability(
            @Parameter(description = "ID of the town to toggle", required = true)
            @PathVariable Long id) {
        locationService.toggleTownDeliveryAvailability(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/towns/{id}")
    @Operation(summary = "Get town by id", description = "Returns the town identified by the given id.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Town retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TownResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Town not found", content = @Content)
    })
    public ResponseEntity<TownResponse> getTownById(
            @Parameter(description = "ID of the town", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(locationService.getTownById(id));
    }

    @GetMapping("/towns/by-big-area/{bigAreaId}")
    @Operation(summary = "Get towns by big area", description = "Returns all towns belonging to the given big area.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Towns retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<List<TownResponse>> getTownsByBigArea(
            @Parameter(description = "ID of the big area", required = true)
            @PathVariable Long bigAreaId) {
        return ResponseEntity.ok(locationService.getTownsByBigArea(bigAreaId));
    }

    @GetMapping("/towns/by-big-area/{bigAreaId}/paginated")
    @Operation(summary = "Get towns by big area (paginated)", description = "Returns a paginated list of towns belonging to the given big area, sorted by display order by default.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Towns retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<Page<TownResponse>> getTownsByBigAreaPaginated(
            @Parameter(description = "ID of the big area", required = true)
            @PathVariable Long bigAreaId,
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(locationService.getTownsByBigAreaPaginated(bigAreaId, pageable));
    }

    @GetMapping("/towns/by-big-area/{bigAreaId}/active")
    @Operation(summary = "Get active towns by big area", description = "Returns all active towns belonging to the given big area.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active towns retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<List<TownResponse>> getActiveTownsByBigArea(
            @Parameter(description = "ID of the big area", required = true)
            @PathVariable Long bigAreaId) {
        return ResponseEntity.ok(locationService.getActiveTownsByBigArea(bigAreaId));
    }

    @GetMapping("/towns/by-big-area/{bigAreaId}/delivery-available")
    @Operation(summary = "Get delivery-available towns by big area", description = "Returns all towns within the given big area that currently have delivery available.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Towns retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Big area not found", content = @Content)
    })
    public ResponseEntity<List<TownResponse>> getDeliveryAvailableTowns(
            @Parameter(description = "ID of the big area", required = true)
            @PathVariable Long bigAreaId) {
        return ResponseEntity.ok(locationService.getDeliveryAvailableTowns(bigAreaId));
    }

    @GetMapping("/towns/search")
    @Operation(summary = "Search towns", description = "Searches towns by matching the given keyword against name/description.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<TownResponse>> searchTowns(
            @Parameter(description = "Keyword to search for", required = true)
            @RequestParam String keyword) {
        return ResponseEntity.ok(locationService.searchTowns(keyword));
    }

    @GetMapping("/towns/{id}/address-count")
    @Operation(summary = "Count delivery addresses in a town", description = "Returns the number of delivery addresses registered in the given town.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Town not found", content = @Content)
    })
    public ResponseEntity<Long> countDeliveryAddressesByTown(
            @Parameter(description = "ID of the town", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(locationService.countDeliveryAddressesByTown(id));
    }

    // ========== ✅ DELIVERY ADDRESS ENDPOINTS (UNIFIED) ==========

    // --- User Self Endpoints ---

    @PostMapping("/users/me/addresses")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add address for current user", description = "Adds a new delivery address for the currently authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address added successfully",
                    content = @Content(schema = @Schema(implementation = DeliveryAddressResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<DeliveryAddressResponse>> addAddressForCurrentUser(
            @Valid @RequestBody DeliveryAddressRequest request) {
        Long userId = getCurrentUserId();
        DeliveryAddressResponse response = locationService.createDeliveryAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added successfully", response));
    }

    @GetMapping("/users/me/addresses")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user's addresses", description = "Returns all delivery addresses belonging to the currently authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<DeliveryAddressResponse>>> getCurrentUserAddresses() {
        Long userId = getCurrentUserId();
        List<DeliveryAddressResponse> addresses = locationService.getUserDeliveryAddresses(userId);
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @GetMapping("/users/me/addresses/default")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user's default address", description = "Returns the default delivery address for the currently authenticated user, if one is set.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Default address retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DeliveryAddressResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No default address set", content = @Content)
    })
    public ResponseEntity<ApiResponse<DeliveryAddressResponse>> getCurrentUserDefaultAddress() {
        Long userId = getCurrentUserId();
        DeliveryAddressResponse response = locationService.getDefaultDeliveryAddress(userId);
        return response != null ? ResponseEntity.ok(ApiResponse.success(response))
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/users/me/addresses/{addressId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update current user's address", description = "Updates the given delivery address belonging to the currently authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = DeliveryAddressResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<DeliveryAddressResponse>> updateCurrentUserAddress(
            @Parameter(description = "ID of the address to update", required = true)
            @PathVariable Long addressId,
            @Valid @RequestBody DeliveryAddressRequest request) {
        Long userId = getCurrentUserId();
        DeliveryAddressResponse response = locationService.updateDeliveryAddress(userId, addressId, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", response));
    }

    @DeleteMapping("/users/me/addresses/{addressId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete current user's address", description = "Deletes the given delivery address belonging to the currently authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUserAddress(
            @Parameter(description = "ID of the address to delete", required = true)
            @PathVariable Long addressId) {
        Long userId = getCurrentUserId();
        locationService.deleteDeliveryAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    @PatchMapping("/users/me/addresses/{addressId}/set-default")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Set current user's default address", description = "Marks the given address as the default delivery address for the currently authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Default address updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @Parameter(description = "ID of the address to set as default", required = true)
            @PathVariable Long addressId) {
        Long userId = getCurrentUserId();
        locationService.setDefaultDeliveryAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success("Default address updated"));
    }

    // --- Admin or Self Endpoints ---

    @PostMapping("/users/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add address for a user", description = "Adds a new delivery address for the given user. Requires ADMIN role, or the caller to be that same user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address added successfully",
                    content = @Content(schema = @Schema(implementation = DeliveryAddressResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<DeliveryAddressResponse>> addAddress(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody DeliveryAddressRequest request) {
        DeliveryAddressResponse response = locationService.createDeliveryAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added successfully", response));
    }

    @GetMapping("/users/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get addresses for a user", description = "Returns all delivery addresses belonging to the given user. Requires ADMIN role, or the caller to be that same user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<DeliveryAddressResponse>>> getUserAddresses(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        List<DeliveryAddressResponse> addresses = locationService.getUserDeliveryAddresses(userId);
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @GetMapping("/users/{userId}/addresses/default")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get default address for a user", description = "Returns the default delivery address for the given user, if one is set. Requires ADMIN role, or the caller to be that same user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Default address retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DeliveryAddressResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No default address set", content = @Content)
    })
    public ResponseEntity<ApiResponse<DeliveryAddressResponse>> getDefaultAddress(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        DeliveryAddressResponse response = locationService.getDefaultDeliveryAddress(userId);
        return response != null ? ResponseEntity.ok(ApiResponse.success(response))
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update address for a user", description = "Updates the given delivery address belonging to the given user. Requires ADMIN role, or the caller to be that same user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = DeliveryAddressResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<DeliveryAddressResponse>> updateAddress(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId,
            @Parameter(description = "ID of the address to update", required = true)
            @PathVariable Long addressId,
            @Valid @RequestBody DeliveryAddressRequest request) {
        DeliveryAddressResponse response = locationService.updateDeliveryAddress(userId, addressId, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", response));
    }

    @DeleteMapping("/users/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete address for a user", description = "Deletes the given delivery address belonging to the given user. Requires ADMIN role, or the caller to be that same user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId,
            @Parameter(description = "ID of the address to delete", required = true)
            @PathVariable Long addressId) {
        locationService.deleteDeliveryAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    @PatchMapping("/users/{userId}/addresses/{addressId}/set-default")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Set default address for a user", description = "Marks the given address as the default delivery address for the given user. Requires ADMIN role, or the caller to be that same user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Default address updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> setDefaultAddressByUser(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId,
            @Parameter(description = "ID of the address to set as default", required = true)
            @PathVariable Long addressId) {
        locationService.setDefaultDeliveryAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success("Default address updated"));
    }

    // ========== HELPER METHODS ==========

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }

        throw new RuntimeException("User not authenticated");
    }
}