package com.example.demo.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NOT NULL")
    List<Category> findAllSubCategories();

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NOT NULL")
    Page<Category> findAllSubCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId")
    List<Category> findByParentCategoryId(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId")
    Page<Category> findByParentCategoryId(@Param("parentId") Long parentId, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId AND c.active = true")
    List<Category> findActiveByParentCategoryId(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NOT NULL AND (c.name LIKE %:keyword% OR c.description LIKE %:keyword%)")
    List<Category> searchSubCategories(@Param("keyword") String keyword);

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NOT NULL ORDER BY c.displayOrder ASC")
    List<Category> findAllSubCategoriesOrdered();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId ORDER BY c.displayOrder ASC")
    List<Category> findByParentCategoryIdOrdered(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.parentCategory.id = :parentId")
    long countByParentCategoryId(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NOT NULL AND c.id IN :ids")
    List<Category> findAllByIds(@Param("ids") List<Long> ids);
}