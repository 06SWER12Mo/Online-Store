package com.example.demo.location;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // ========== BigArea Endpoints ==========

    @PostMapping("/big-areas")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigAreaResponse> createBigArea(@Valid @RequestBody BigAreaRequest request) {
        BigAreaResponse response = locationService.createBigArea(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/big-areas/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigAreaResponse> updateBigArea(
            @PathVariable Long id,
            @Valid @RequestBody BigAreaRequest request) {
        BigAreaResponse response = locationService.updateBigArea(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/big-areas/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteBigArea(@PathVariable Long id) {
        locationService.deleteBigArea(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/big-areas/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> toggleBigAreaActive(@PathVariable Long id) {
        locationService.toggleBigAreaActive(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/big-areas")
    public ResponseEntity<List<BigAreaResponse>> getAllBigAreas() {
        return ResponseEntity.ok(locationService.getAllBigAreas());
    }

    @GetMapping("/big-areas/active")
    public ResponseEntity<List<BigAreaResponse>> getActiveBigAreas() {
        return ResponseEntity.ok(locationService.getActiveBigAreas());
    }

    @GetMapping("/big-areas/{id}")
    public ResponseEntity<BigAreaResponse> getBigAreaById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getBigAreaById(id));
    }

    @GetMapping("/big-areas/search")
    public ResponseEntity<List<BigAreaResponse>> searchBigAreas(@RequestParam String keyword) {
        return ResponseEntity.ok(locationService.searchBigAreas(keyword));
    }

    @GetMapping("/big-areas/{id}/town-count")
    public ResponseEntity<Long> countTownsByBigArea(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.countTownsByBigArea(id));
    }

    // ========== Town Endpoints ==========

    @PostMapping("/towns")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TownResponse> createTown(@Valid @RequestBody TownRequest request) {
        TownResponse response = locationService.createTown(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/towns/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TownResponse> updateTown(
            @PathVariable Long id,
            @Valid @RequestBody TownRequest request) {
        TownResponse response = locationService.updateTown(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/towns/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteTown(@PathVariable Long id) {
        locationService.deleteTown(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/towns/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> toggleTownActive(@PathVariable Long id) {
        locationService.toggleTownActive(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/towns/{id}/toggle-delivery")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> toggleTownDeliveryAvailability(@PathVariable Long id) {
        locationService.toggleTownDeliveryAvailability(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/towns/{id}")
    public ResponseEntity<TownResponse> getTownById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getTownById(id));
    }

    @GetMapping("/towns/by-big-area/{bigAreaId}")
    public ResponseEntity<List<TownResponse>> getTownsByBigArea(@PathVariable Long bigAreaId) {
        return ResponseEntity.ok(locationService.getTownsByBigArea(bigAreaId));
    }

    @GetMapping("/towns/by-big-area/{bigAreaId}/paginated")
    public ResponseEntity<Page<TownResponse>> getTownsByBigAreaPaginated(
            @PathVariable Long bigAreaId,
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(locationService.getTownsByBigAreaPaginated(bigAreaId, pageable));
    }

    @GetMapping("/towns/by-big-area/{bigAreaId}/active")
    public ResponseEntity<List<TownResponse>> getActiveTownsByBigArea(@PathVariable Long bigAreaId) {
        return ResponseEntity.ok(locationService.getActiveTownsByBigArea(bigAreaId));
    }

    @GetMapping("/towns/by-big-area/{bigAreaId}/delivery-available")
    public ResponseEntity<List<TownResponse>> getDeliveryAvailableTowns(@PathVariable Long bigAreaId) {
        return ResponseEntity.ok(locationService.getDeliveryAvailableTowns(bigAreaId));
    }

    @GetMapping("/towns/search")
    public ResponseEntity<List<TownResponse>> searchTowns(@RequestParam String keyword) {
        return ResponseEntity.ok(locationService.searchTowns(keyword));
    }

    @GetMapping("/towns/{id}/address-count")
    public ResponseEntity<Long> countDeliveryAddressesByTown(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.countDeliveryAddressesByTown(id));
    }

    // ========== DeliveryAddress Endpoints ==========

    @PostMapping("/users/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<DeliveryAddressResponse> createDeliveryAddress(
            @PathVariable Long userId,
            @Valid @RequestBody DeliveryAddressRequest request) {
        DeliveryAddressResponse response = locationService.createDeliveryAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/users/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<DeliveryAddressResponse> updateDeliveryAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody DeliveryAddressRequest request) {
        DeliveryAddressResponse response = locationService.updateDeliveryAddress(userId, addressId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<Void> deleteDeliveryAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        locationService.deleteDeliveryAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}/addresses/{addressId}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<Void> toggleDeliveryAddressActive(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        locationService.toggleDeliveryAddressActive(userId, addressId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{userId}/addresses/{addressId}/set-default")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<Void> setDefaultDeliveryAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        locationService.setDefaultDeliveryAddress(userId, addressId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<List<DeliveryAddressResponse>> getUserDeliveryAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(locationService.getUserDeliveryAddresses(userId));
    }

    @GetMapping("/users/{userId}/addresses/paginated")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<Page<DeliveryAddressResponse>> getUserDeliveryAddressesPaginated(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(locationService.getUserDeliveryAddressesPaginated(userId, pageable));
    }

    @GetMapping("/users/{userId}/addresses/default")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<DeliveryAddressResponse> getDefaultDeliveryAddress(@PathVariable Long userId) {
        DeliveryAddressResponse response = locationService.getDefaultDeliveryAddress(userId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<DeliveryAddressResponse> getDeliveryAddressById(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(locationService.getDeliveryAddressById(userId, addressId));
    }
}