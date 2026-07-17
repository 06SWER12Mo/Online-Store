package com.example.demo.employees;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ========== EXISTENCE CHECKS ==========

    Optional<Employee> findByPassportNumber(String passportNumber);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByPhoneNumber(String phoneNumber);

    boolean existsByPassportNumber(String passportNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPassportNumberAndIdNot(String passportNumber, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    // ========== ACTIVE STATUS ==========

    List<Employee> findByIsActiveTrue();

    List<Employee> findByIsActiveFalse();

    Page<Employee> findByIsActiveTrue(Pageable pageable);

    Page<Employee> findByIsActiveFalse(Pageable pageable);

    // ========== ROLE ==========

    List<Employee> findByRole(String role);

    Page<Employee> findByRole(String role, Pageable pageable);

    List<Employee> findByRoleAndIsActiveTrue(String role);

    Page<Employee> findByRoleIgnoreCase(String role, Pageable pageable);

    List<Employee> findByRoleIgnoreCase(String role);

    // ========== SEARCH BY NAME ==========

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Employee> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> findByNameContainingIgnoreCase(@Param("name") String name);

    // ========== SEARCH BY NAME AND ROLE ==========

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(e.role) = LOWER(:role)")
    Page<Employee> findByNameContainingIgnoreCaseAndRoleIgnoreCase(@Param("name") String name, 
                                                                   @Param("role") String role, 
                                                                   Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(e.role) = LOWER(:role)")
    List<Employee> findByNameContainingIgnoreCaseAndRoleIgnoreCase(@Param("name") String name, 
                                                                   @Param("role") String role);

    // ========== GENERAL SEARCH ==========

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.passportNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> searchEmployees(@Param("keyword") String keyword);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.passportNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> searchEmployees(@Param("keyword") String keyword, Pageable pageable);

    // ========== UPDATE OPERATIONS ==========

    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.isActive = :active WHERE e.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);

    // ========== STATISTICS ==========

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.isActive = true")
    Long countActiveEmployees();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.role = :role")
    Long countByRole(@Param("role") String role);

    @Query("SELECT e FROM Employee e WHERE e.employeeSince BETWEEN :startDate AND :endDate")
    List<Employee> findByEmployeeSinceBetween(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
}