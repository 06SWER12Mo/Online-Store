package com.example.demo.location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocationService {

    // BigArea operations
    BigAreaResponse createBigArea(BigAreaRequest request);
    BigAreaResponse updateBigArea(Long id, BigAreaRequest request);
    void deleteBigArea(Long id);
    BigAreaResponse getBigAreaById(Long id);
    List<BigAreaResponse> getAllBigAreas();
    List<BigAreaResponse> getActiveBigAreas();
    List<BigAreaResponse> searchBigAreas(String keyword);
    void toggleBigAreaActive(Long id);
    long countTownsByBigArea(Long bigAreaId);

    // Town operations
    TownResponse createTown(TownRequest request);
    TownResponse updateTown(Long id, TownRequest request);
    void deleteTown(Long id);
    TownResponse getTownById(Long id);
    List<TownResponse> getTownsByBigArea(Long bigAreaId);
    Page<TownResponse> getTownsByBigAreaPaginated(Long bigAreaId, Pageable pageable);
    List<TownResponse> getActiveTownsByBigArea(Long bigAreaId);
    List<TownResponse> getDeliveryAvailableTowns(Long bigAreaId);
    List<TownResponse> searchTowns(String keyword);
    void toggleTownActive(Long id);
    void toggleTownDeliveryAvailability(Long id);
    long countDeliveryAddressesByTown(Long townId);

    // DeliveryAddress operations
    DeliveryAddressResponse createDeliveryAddress(Long userId, DeliveryAddressRequest request);
    DeliveryAddressResponse updateDeliveryAddress(Long userId, Long addressId, DeliveryAddressRequest request);
    void deleteDeliveryAddress(Long userId, Long addressId);
    DeliveryAddressResponse getDeliveryAddressById(Long userId, Long addressId);
    List<DeliveryAddressResponse> getUserDeliveryAddresses(Long userId);
    Page<DeliveryAddressResponse> getUserDeliveryAddressesPaginated(Long userId, Pageable pageable);
    DeliveryAddressResponse getDefaultDeliveryAddress(Long userId);
    void setDefaultDeliveryAddress(Long userId, Long addressId);
    void toggleDeliveryAddressActive(Long userId, Long addressId);
}