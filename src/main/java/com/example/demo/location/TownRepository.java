package com.example.demo.location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TownRepository extends JpaRepository<Town, Long> {

    Optional<Town> findByName(String name);

    Optional<Town> findByCode(String code);

    Optional<Town> findByZipCode(String zipCode);

    List<Town> findByBigAreaId(Long bigAreaId);

    Page<Town> findByBigAreaId(Long bigAreaId, Pageable pageable);

    List<Town> findByBigAreaIdAndActiveTrue(Long bigAreaId);

    List<Town> findByActiveTrue();

    List<Town> findByDeliveryAvailableTrue();

    List<Town> findByBigAreaIdOrderByDisplayOrderAsc(Long bigAreaId);

    @Query("SELECT t FROM Town t WHERE t.bigArea.id = :bigAreaId AND t.active = true ORDER BY t.displayOrder ASC")
    List<Town> findActiveTownsByBigArea(@Param("bigAreaId") Long bigAreaId);

    @Query("SELECT t FROM Town t WHERE t.name LIKE %:keyword% OR t.code LIKE %:keyword% OR t.zipCode LIKE %:keyword%")
    List<Town> searchTowns(@Param("keyword") String keyword);

    @Query("SELECT t FROM Town t WHERE t.bigArea.id = :bigAreaId AND t.deliveryAvailable = true")
    List<Town> findDeliveryAvailableTowns(@Param("bigAreaId") Long bigAreaId);

    @Query("SELECT COUNT(da) FROM DeliveryAddress da WHERE da.town.id = :townId")
    long countDeliveryAddressesByTownId(@Param("townId") Long townId);

    @Modifying
    @Transactional
    @Query("UPDATE Town t SET t.active = :active WHERE t.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);

    @Modifying
    @Transactional
    @Query("UPDATE Town t SET t.deliveryAvailable = :available WHERE t.id = :id")
    void updateDeliveryAvailability(@Param("id") Long id, @Param("available") boolean available);

    boolean existsByNameAndBigAreaId(String name, Long bigAreaId);

    boolean existsByCode(String code);
}