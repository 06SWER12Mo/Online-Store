package com.example.demo.location;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationMapper {

    private final UserRepository userRepository;

    public LocationMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // BigArea mappings
    public BigArea toBigAreaEntity(BigAreaRequest request) {
        BigArea bigArea = new BigArea();
        bigArea.setName(request.getName());
        bigArea.setCode(request.getCode());
        bigArea.setDescription(request.getDescription());
        bigArea.setActive(request.getActive() != null ? request.getActive() : true);
        bigArea.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        return bigArea;
    }

    public void updateBigAreaEntity(BigArea bigArea, BigAreaRequest request) {
        if (request.getName() != null) {
            bigArea.setName(request.getName());
        }
        if (request.getCode() != null) {
            bigArea.setCode(request.getCode());
        }
        if (request.getDescription() != null) {
            bigArea.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            bigArea.setActive(request.getActive());
        }
        if (request.getDisplayOrder() != null) {
            bigArea.setDisplayOrder(request.getDisplayOrder());
        }
    }

    public BigAreaResponse toBigAreaResponse(BigArea bigArea) {
        return new BigAreaResponse(bigArea);
    }

    public List<BigAreaResponse> toBigAreaResponseList(List<BigArea> bigAreas) {
        return bigAreas.stream()
                .map(this::toBigAreaResponse)
                .collect(Collectors.toList());
    }

    // Town mappings
    public Town toTownEntity(TownRequest request, BigArea bigArea) {
        Town town = new Town();
        town.setName(request.getName());
        town.setCode(request.getCode());
        town.setZipCode(request.getZipCode());
        town.setDescription(request.getDescription());
        town.setActive(request.getActive() != null ? request.getActive() : true);
        town.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        town.setLatitude(request.getLatitude());
        town.setLongitude(request.getLongitude());
        town.setDeliveryFee(request.getDeliveryFee());
        town.setDeliveryAvailable(request.getDeliveryAvailable() != null ? request.getDeliveryAvailable() : true);
        town.setBigArea(bigArea);
        return town;
    }

    public void updateTownEntity(Town town, TownRequest request, BigArea bigArea) {
        if (request.getName() != null) {
            town.setName(request.getName());
        }
        if (request.getCode() != null) {
            town.setCode(request.getCode());
        }
        if (request.getZipCode() != null) {
            town.setZipCode(request.getZipCode());
        }
        if (request.getDescription() != null) {
            town.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            town.setActive(request.getActive());
        }
        if (request.getDisplayOrder() != null) {
            town.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getLatitude() != null) {
            town.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            town.setLongitude(request.getLongitude());
        }
        if (request.getDeliveryFee() != null) {
            town.setDeliveryFee(request.getDeliveryFee());
        }
        if (request.getDeliveryAvailable() != null) {
            town.setDeliveryAvailable(request.getDeliveryAvailable());
        }
        if (bigArea != null) {
            town.setBigArea(bigArea);
        }
    }

    public TownResponse toTownResponse(Town town) {
        return new TownResponse(town);
    }

    public List<TownResponse> toTownResponseList(List<Town> towns) {
        return towns.stream()
                .map(this::toTownResponse)
                .collect(Collectors.toList());
    }

    // DeliveryAddress mappings
    public DeliveryAddress toDeliveryAddressEntity(DeliveryAddressRequest request, User user, Town town) {
        DeliveryAddress address = new DeliveryAddress();
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setStreet(request.getStreet());
        address.setBuilding(request.getBuilding());
        address.setFloor(request.getFloor());
        address.setApartment(request.getApartment());
        address.setLandmark(request.getLandmark());
        address.setDefault(request.isDefault());
        address.setAddressType(request.getAddressType());
        address.setRecipientName(request.getRecipientName());
        address.setRecipientPhone(request.getRecipientPhone());
        address.setAdditionalInstructions(request.getAdditionalInstructions());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setActive(true);
        address.setUser(user);
        address.setTown(town);
        return address;
    }

    public void updateDeliveryAddressEntity(DeliveryAddress address, DeliveryAddressRequest request, Town town) {
        if (request.getAddressLine1() != null) {
            address.setAddressLine1(request.getAddressLine1());
        }
        if (request.getAddressLine2() != null) {
            address.setAddressLine2(request.getAddressLine2());
        }
        if (request.getStreet() != null) {
            address.setStreet(request.getStreet());
        }
        if (request.getBuilding() != null) {
            address.setBuilding(request.getBuilding());
        }
        if (request.getFloor() != null) {
            address.setFloor(request.getFloor());
        }
        if (request.getApartment() != null) {
            address.setApartment(request.getApartment());
        }
        if (request.getLandmark() != null) {
            address.setLandmark(request.getLandmark());
        }
        address.setDefault(request.isDefault());
        if (request.getAddressType() != null) {
            address.setAddressType(request.getAddressType());
        }
        if (request.getRecipientName() != null) {
            address.setRecipientName(request.getRecipientName());
        }
        if (request.getRecipientPhone() != null) {
            address.setRecipientPhone(request.getRecipientPhone());
        }
        if (request.getAdditionalInstructions() != null) {
            address.setAdditionalInstructions(request.getAdditionalInstructions());
        }
        if (request.getLatitude() != null) {
            address.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            address.setLongitude(request.getLongitude());
        }
        if (town != null) {
            address.setTown(town);
        }
    }

    public DeliveryAddressResponse toDeliveryAddressResponse(DeliveryAddress address) {
        return new DeliveryAddressResponse(address);
    }

    public List<DeliveryAddressResponse> toDeliveryAddressResponseList(List<DeliveryAddress> addresses) {
        return addresses.stream()
                .map(this::toDeliveryAddressResponse)
                .collect(Collectors.toList());
    }
}