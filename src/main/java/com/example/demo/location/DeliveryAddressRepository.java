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
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    // Find by user
    List<DeliveryAddress> findByUserId(Long userId);
    
    Page<DeliveryAddress> findByUserId(Long userId, Pageable pageable);
    
    Optional<DeliveryAddress> findByIdAndUserId(Long id, Long userId);
    
    List<DeliveryAddress> findByUserIdAndActiveTrue(Long userId);
    
    Optional<DeliveryAddress> findByUserIdAndIsDefaultTrue(Long userId);
    
    // Find by town
    List<DeliveryAddress> findByTownId(Long townId);
    
    Page<DeliveryAddress> findByTownId(Long townId, Pageable pageable);
    
    List<DeliveryAddress> findByTownIdAndActiveTrue(Long townId);
    
    // Find by user and town
    List<DeliveryAddress> findByUserIdAndTownId(Long userId, Long townId);
    
    // Find by user and address type
    List<DeliveryAddress> findByUserIdAndAddressType(Long userId, String addressType);
    
    // Find default active address
    @Query("SELECT da FROM DeliveryAddress da WHERE da.user.id = :userId AND da.isDefault = true AND da.active = true")
    Optional<DeliveryAddress> findDefaultActiveAddress(@Param("userId") Long userId);
    
    // Find active addresses for user
    @Query("SELECT da FROM DeliveryAddress da WHERE da.user.id = :userId AND da.active = true ORDER BY da.isDefault DESC, da.createdAt DESC")
    List<DeliveryAddress> findActiveAddressesByUserId(@Param("userId") Long userId);
    
    // Count addresses by user
    @Query("SELECT COUNT(da) FROM DeliveryAddress da WHERE da.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // Count active addresses by user
    @Query("SELECT COUNT(da) FROM DeliveryAddress da WHERE da.user.id = :userId AND da.active = true")
    long countActiveByUserId(@Param("userId") Long userId);
    
    // Count addresses by town
    @Query("SELECT COUNT(da) FROM DeliveryAddress da WHERE da.town.id = :townId")
    long countByTownId(@Param("townId") Long townId);
    
    // Clear default address for a user
    @Modifying
    @Transactional
    @Query("UPDATE DeliveryAddress da SET da.isDefault = false WHERE da.user.id = :userId AND da.isDefault = true")
    void clearDefaultAddress(@Param("userId") Long userId);
    
    // Update active status
    @Modifying
    @Transactional
    @Query("UPDATE DeliveryAddress da SET da.active = :active WHERE da.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);
    
    // Update default status
    @Modifying
    @Transactional
    @Query("UPDATE DeliveryAddress da SET da.isDefault = :isDefault WHERE da.id = :id")
    void updateDefaultStatus(@Param("id") Long id, @Param("isDefault") boolean isDefault);
    
    // Search addresses by keyword (street, building, landmark, etc.)
    @Query("SELECT da FROM DeliveryAddress da WHERE " +
           "da.user.id = :userId AND " +
           "(da.street LIKE %:keyword% OR " +
           "da.building LIKE %:keyword% OR " +
           "da.landmark LIKE %:keyword% OR " +
           "da.addressLine1 LIKE %:keyword% OR " +
           "da.addressLine2 LIKE %:keyword%)")
    List<DeliveryAddress> searchUserAddresses(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    // Find addresses by recipient name
    @Query("SELECT da FROM DeliveryAddress da WHERE da.user.id = :userId AND LOWER(da.recipientName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<DeliveryAddress> findByUserIdAndRecipientNameContaining(@Param("userId") Long userId, @Param("name") String name);
    
    // Find addresses by phone number
    @Query("SELECT da FROM DeliveryAddress da WHERE da.user.id = :userId AND da.recipientPhone = :phone")
    Optional<DeliveryAddress> findByUserIdAndRecipientPhone(@Param("userId") Long userId, @Param("phone") String phone);
    
    // Find recent addresses for a user (with pagination)
    @Query("SELECT da FROM DeliveryAddress da WHERE da.user.id = :userId AND da.active = true ORDER BY da.createdAt DESC")
    Page<DeliveryAddress> findRecentAddressesByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // Check if user has any default address
    @Query("SELECT CASE WHEN COUNT(da) > 0 THEN true ELSE false END FROM DeliveryAddress da WHERE da.user.id = :userId AND da.isDefault = true AND da.active = true")
    boolean hasDefaultAddress(@Param("userId") Long userId);
    
    // Get all distinct towns where user has addresses
    @Query("SELECT DISTINCT da.town.id FROM DeliveryAddress da WHERE da.user.id = :userId")
    List<Long> findDistinctTownIdsByUserId(@Param("userId") Long userId);
    
    // Soft delete (update active to false)
    @Modifying
    @Transactional
    @Query("UPDATE DeliveryAddress da SET da.active = false WHERE da.id = :id AND da.user.id = :userId")
    int softDeleteByUserIdAndAddressId(@Param("userId") Long userId, @Param("id") Long id);
    
    // Get default address with town and big area eagerly loaded
    @Query("SELECT da FROM DeliveryAddress da LEFT JOIN FETCH da.town t LEFT JOIN FETCH t.bigArea WHERE da.user.id = :userId AND da.isDefault = true AND da.active = true")
    Optional<DeliveryAddress> findDefaultAddressWithTownAndBigArea(@Param("userId") Long userId);
    
    // Get address by id with user, town and big area eagerly loaded
    @Query("SELECT da FROM DeliveryAddress da LEFT JOIN FETCH da.user LEFT JOIN FETCH da.town t LEFT JOIN FETCH t.bigArea WHERE da.id = :id")
    Optional<DeliveryAddress> findByIdWithDetails(@Param("id") Long id);
}