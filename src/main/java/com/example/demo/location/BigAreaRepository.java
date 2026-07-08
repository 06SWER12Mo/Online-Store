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
public interface BigAreaRepository extends JpaRepository<BigArea, Long> {

    Optional<BigArea> findByName(String name);

    Optional<BigArea> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT b FROM BigArea b WHERE b.active = true ORDER BY b.displayOrder ASC")
    List<BigArea> findActiveBigAreas();

    @Query("SELECT b FROM BigArea b ORDER BY b.displayOrder ASC")
    List<BigArea> findAllOrdered();

    @Query("SELECT b FROM BigArea b WHERE b.name LIKE %:keyword% OR b.code LIKE %:keyword%")
    List<BigArea> searchBigAreas(@Param("keyword") String keyword);

    @Query("SELECT COUNT(t) FROM Town t WHERE t.bigArea.id = :bigAreaId")
    long countTownsByBigAreaId(@Param("bigAreaId") Long bigAreaId);

    @Modifying
    @Transactional
    @Query("UPDATE BigArea b SET b.active = :active WHERE b.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);
}