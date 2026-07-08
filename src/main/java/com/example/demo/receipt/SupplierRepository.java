package com.example.demo.receipt;

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
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByCode(String code);
    
    Optional<Supplier> findByEmail(String email);
    
    List<Supplier> findByNameContainingIgnoreCase(String name);
    
    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    List<Supplier> findByActiveTrue();
    
    Page<Supplier> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Supplier> searchSuppliers(@Param("keyword") String keyword);
    
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Supplier> searchSuppliers(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByCode(String code);
    
    boolean existsByCodeAndIdNot(String code, Long id);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Supplier s SET s.active = :active WHERE s.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);
    
    @Query("SELECT COUNT(r) FROM Receipt r WHERE r.supplier.id = :supplierId")
    long countReceiptsBySupplierId(@Param("supplierId") Long supplierId);
}