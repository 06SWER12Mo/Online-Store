package com.example.demo.category;

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
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Category> findByParentCategoryIsNull();

    List<Category> findByParentCategoryId(Long parentId);

    Page<Category> findByParentCategoryIsNull(Pageable pageable);

    Page<Category> findByParentCategoryId(Long parentId, Pageable pageable);

    List<Category> findByActiveTrue();

    List<Category> findByParentCategoryIsNullAndActiveTrue();

    List<Category> findByParentCategoryIdAndActiveTrue(Long parentId);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Category> searchCategories(@Param("keyword") String keyword);

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL ORDER BY c.displayOrder ASC")
    List<Category> findRootCategoriesOrdered();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId ORDER BY c.displayOrder ASC")
    List<Category> findSubCategoriesOrdered(@Param("parentId") Long parentId);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.active = :active WHERE c.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.displayOrder = :displayOrder WHERE c.id = :id")
    void updateDisplayOrder(@Param("id") Long id, @Param("displayOrder") Integer displayOrder);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.parentCategory.id = :parentId")
    long countSubCategories(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL AND c.active = true ORDER BY c.displayOrder ASC")
    List<Category> findActiveRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId AND c.active = true ORDER BY c.displayOrder ASC")
    List<Category> findActiveSubCategories(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.id IN :ids")
    List<Category> findAllByIds(@Param("ids") List<Long> ids);
}