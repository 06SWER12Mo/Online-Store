package com.example.demo.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    // ========== FIND BY ENTITY ==========

    List<ImageEntity> findByEntityTypeAndEntityIdOrderByDisplayOrderAsc(String entityType, Long entityId);

    List<ImageEntity> findByEntityTypeAndEntityIdAndImageType(String entityType, Long entityId, String imageType);

    // ========== FIND PRIMARY ==========

    @Query("SELECT i FROM ImageEntity i WHERE i.entityType = :entityType AND i.entityId = :entityId AND i.primary = true")
    Optional<ImageEntity> findPrimaryImage(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    // ========== DELETE BY ENTITY ==========

    @Modifying
    @Transactional
    @Query("DELETE FROM ImageEntity i WHERE i.entityType = :entityType AND i.entityId = :entityId")
    void deleteByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    // ========== UPDATE PRIMARY ==========

    @Modifying
    @Transactional
    @Query("UPDATE ImageEntity i SET i.primary = false WHERE i.entityType = :entityType AND i.entityId = :entityId")
    void clearPrimaryImages(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    // ========== COUNT ==========

    @Query("SELECT COUNT(i) FROM ImageEntity i WHERE i.entityType = :entityType AND i.entityId = :entityId")
    long countByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    // ========== EXISTS ==========

    boolean existsByEntityTypeAndEntityIdAndImageType(String entityType, Long entityId, String imageType);
}