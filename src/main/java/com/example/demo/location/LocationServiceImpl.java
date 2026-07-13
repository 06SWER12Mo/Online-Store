package com.example.demo.location;

import com.example.demo.location.dtos.BigAreaRequest;
import com.example.demo.location.dtos.BigAreaResponse;
import com.example.demo.location.dtos.DeliveryAddressRequest;
import com.example.demo.location.dtos.DeliveryAddressResponse;
import com.example.demo.location.dtos.TownRequest;
import com.example.demo.location.dtos.TownResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private final BigAreaRepository bigAreaRepository;
    private final TownRepository townRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;
    private final LocationMapper locationMapper;

    public LocationServiceImpl(BigAreaRepository bigAreaRepository,
                               TownRepository townRepository,
                               DeliveryAddressRepository deliveryAddressRepository,
                               UserRepository userRepository,
                               LocationMapper locationMapper) {
        this.bigAreaRepository = bigAreaRepository;
        this.townRepository = townRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.userRepository = userRepository;
        this.locationMapper = locationMapper;
    }

    // ========== BigArea Operations ==========

    @Override
    public BigAreaResponse createBigArea(BigAreaRequest request) {
        if (bigAreaRepository.existsByName(request.getName())) {
            throw new RuntimeException("Big area with name '" + request.getName() + "' already exists");
        }
        if (request.getCode() != null && bigAreaRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Big area with code '" + request.getCode() + "' already exists");
        }

        BigArea bigArea = locationMapper.toBigAreaEntity(request);
        BigArea savedBigArea = bigAreaRepository.save(bigArea);
        return locationMapper.toBigAreaResponse(savedBigArea);
    }

    @Override
    public BigAreaResponse updateBigArea(Long id, BigAreaRequest request) {
        BigArea bigArea = findBigAreaById(id);

        if (request.getName() != null && bigAreaRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Big area with name '" + request.getName() + "' already exists");
        }

        locationMapper.updateBigAreaEntity(bigArea, request);
        BigArea updatedBigArea = bigAreaRepository.save(bigArea);
        return locationMapper.toBigAreaResponse(updatedBigArea);
    }

    @Override
    public void deleteBigArea(Long id) {
        BigArea bigArea = findBigAreaById(id);
        if (!bigArea.getTowns().isEmpty()) {
            throw new RuntimeException("Cannot delete big area with existing towns. Please delete towns first.");
        }
        bigAreaRepository.delete(bigArea);
    }

    @Override
    public BigAreaResponse getBigAreaById(Long id) {
        BigArea bigArea = findBigAreaById(id);
        return locationMapper.toBigAreaResponse(bigArea);
    }

    @Override
    public List<BigAreaResponse> getAllBigAreas() {
        return locationMapper.toBigAreaResponseList(bigAreaRepository.findAllOrdered());
    }

    @Override
    public List<BigAreaResponse> getActiveBigAreas() {
        return locationMapper.toBigAreaResponseList(bigAreaRepository.findActiveBigAreas());
    }

    @Override
    public List<BigAreaResponse> searchBigAreas(String keyword) {
        return locationMapper.toBigAreaResponseList(bigAreaRepository.searchBigAreas(keyword));
    }

    @Override
    public void toggleBigAreaActive(Long id) {
        BigArea bigArea = findBigAreaById(id);
        bigArea.setActive(!bigArea.isActive());
        bigAreaRepository.save(bigArea);
    }

    @Override
    public long countTownsByBigArea(Long bigAreaId) {
        return bigAreaRepository.countTownsByBigAreaId(bigAreaId);
    }

    // ========== Town Operations ==========

    @Override
    public TownResponse createTown(TownRequest request) {
        BigArea bigArea = findBigAreaById(request.getBigAreaId());

        if (townRepository.existsByNameAndBigAreaId(request.getName(), request.getBigAreaId())) {
            throw new RuntimeException("Town with name '" + request.getName() + "' already exists in this big area");
        }
        if (request.getCode() != null && townRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Town with code '" + request.getCode() + "' already exists");
        }

        Town town = locationMapper.toTownEntity(request, bigArea);
        Town savedTown = townRepository.save(town);
        return locationMapper.toTownResponse(savedTown);
    }

    @Override
    public TownResponse updateTown(Long id, TownRequest request) {
        Town town = findTownById(id);

        if (request.getBigAreaId() != null) {
            BigArea bigArea = findBigAreaById(request.getBigAreaId());
            town.setBigArea(bigArea);
        }

        locationMapper.updateTownEntity(town, request, town.getBigArea());
        Town updatedTown = townRepository.save(town);
        return locationMapper.toTownResponse(updatedTown);
    }

    @Override
    public void deleteTown(Long id) {
        Town town = findTownById(id);
        if (!town.getDeliveryAddresses().isEmpty()) {
            throw new RuntimeException("Cannot delete town with existing delivery addresses.");
        }
        townRepository.delete(town);
    }

    @Override
    public TownResponse getTownById(Long id) {
        Town town = findTownById(id);
        return locationMapper.toTownResponse(town);
    }

    @Override
    public List<TownResponse> getTownsByBigArea(Long bigAreaId) {
        return locationMapper.toTownResponseList(townRepository.findByBigAreaIdOrderByDisplayOrderAsc(bigAreaId));
    }

    @Override
    public Page<TownResponse> getTownsByBigAreaPaginated(Long bigAreaId, Pageable pageable) {
        return townRepository.findByBigAreaId(bigAreaId, pageable)
                .map(locationMapper::toTownResponse);
    }

    @Override
    public List<TownResponse> getActiveTownsByBigArea(Long bigAreaId) {
        return locationMapper.toTownResponseList(townRepository.findActiveTownsByBigArea(bigAreaId));
    }

    @Override
    public List<TownResponse> getDeliveryAvailableTowns(Long bigAreaId) {
        return locationMapper.toTownResponseList(townRepository.findDeliveryAvailableTowns(bigAreaId));
    }

    @Override
    public List<TownResponse> searchTowns(String keyword) {
        return locationMapper.toTownResponseList(townRepository.searchTowns(keyword));
    }

    @Override
    public void toggleTownActive(Long id) {
        Town town = findTownById(id);
        town.setActive(!town.isActive());
        townRepository.save(town);
    }

    @Override
    public void toggleTownDeliveryAvailability(Long id) {
        Town town = findTownById(id);
        town.setDeliveryAvailable(!town.isDeliveryAvailable());
        townRepository.save(town);
    }

    @Override
    public long countDeliveryAddressesByTown(Long townId) {
        return townRepository.countDeliveryAddressesByTownId(townId);
    }

    // ========== DeliveryAddress Operations ==========

    @Override
    public DeliveryAddressResponse createDeliveryAddress(Long userId, DeliveryAddressRequest request) {
        User user = findUserById(userId);

        // If this address is set as default, clear any existing default
        if (request.isDefault()) {
            deliveryAddressRepository.clearDefaultAddress(userId);
        }

        Town town = null;
        if (request.getTownId() != null) {
            town = findTownById(request.getTownId());
        }

        DeliveryAddress address = locationMapper.toDeliveryAddressEntity(request, user, town);
        DeliveryAddress savedAddress = deliveryAddressRepository.save(address);
        return locationMapper.toDeliveryAddressResponse(savedAddress);
    }

    @Override
    public DeliveryAddressResponse updateDeliveryAddress(Long userId, Long addressId, DeliveryAddressRequest request) {
        DeliveryAddress address = findDeliveryAddressByIdAndUserId(addressId, userId);

        // If this address is set as default, clear any existing default
        if (request.isDefault()) {
            deliveryAddressRepository.clearDefaultAddress(userId);
        }

        Town town = null;
        if (request.getTownId() != null) {
            town = findTownById(request.getTownId());
        }

        locationMapper.updateDeliveryAddressEntity(address, request, town);
        DeliveryAddress updatedAddress = deliveryAddressRepository.save(address);
        return locationMapper.toDeliveryAddressResponse(updatedAddress);
    }

    @Override
    public void deleteDeliveryAddress(Long userId, Long addressId) {
        DeliveryAddress address = findDeliveryAddressByIdAndUserId(addressId, userId);
        deliveryAddressRepository.delete(address);
    }

    @Override
    public DeliveryAddressResponse getDeliveryAddressById(Long userId, Long addressId) {
        DeliveryAddress address = findDeliveryAddressByIdAndUserId(addressId, userId);
        return locationMapper.toDeliveryAddressResponse(address);
    }

    @Override
    public List<DeliveryAddressResponse> getUserDeliveryAddresses(Long userId) {
        return locationMapper.toDeliveryAddressResponseList(
                deliveryAddressRepository.findByUserIdAndActiveTrue(userId)
        );
    }

    @Override
    public Page<DeliveryAddressResponse> getUserDeliveryAddressesPaginated(Long userId, Pageable pageable) {
        return deliveryAddressRepository.findByUserId(userId, pageable)
                .map(locationMapper::toDeliveryAddressResponse);
    }

    @Override
    public DeliveryAddressResponse getDefaultDeliveryAddress(Long userId) {
        DeliveryAddress defaultAddress = deliveryAddressRepository.findDefaultActiveAddress(userId)
                .orElse(null);
        return defaultAddress != null ? locationMapper.toDeliveryAddressResponse(defaultAddress) : null;
    }

    @Override
    public void setDefaultDeliveryAddress(Long userId, Long addressId) {
        DeliveryAddress address = findDeliveryAddressByIdAndUserId(addressId, userId);
        deliveryAddressRepository.clearDefaultAddress(userId);
        address.setDefault(true);
        deliveryAddressRepository.save(address);
    }

    @Override
    public void toggleDeliveryAddressActive(Long userId, Long addressId) {
        DeliveryAddress address = findDeliveryAddressByIdAndUserId(addressId, userId);
        address.setActive(!address.isActive());
        deliveryAddressRepository.save(address);
    }

    // ========== Helper Methods ==========

    private BigArea findBigAreaById(Long id) {
        return bigAreaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Big area not found with id: " + id));
    }

    private Town findTownById(Long id) {
        return townRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Town not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private DeliveryAddress findDeliveryAddressByIdAndUserId(Long addressId, Long userId) {
        return deliveryAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Delivery address not found with id: " + addressId));
    }
}