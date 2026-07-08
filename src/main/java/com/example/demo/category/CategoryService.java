package com.example.demo.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    CategoryResponse getCategoryById(Long id);

    Page<CategoryResponse> getAllCategories(Pageable pageable);

    List<CategoryResponse> getRootCategories();

    List<CategoryResponse> getSubCategories(Long parentId);

    Page<CategoryResponse> getRootCategoriesPaginated(Pageable pageable);

    Page<CategoryResponse> getSubCategoriesPaginated(Long parentId, Pageable pageable);

    List<CategoryResponse> getActiveRootCategories();

    List<CategoryResponse> getActiveSubCategories(Long parentId);

    void toggleCategoryActive(Long id);

    void updateCategoryDisplayOrder(Long id, Integer displayOrder);

    List<CategoryResponse> searchCategories(String keyword);

    long countSubCategories(Long parentId);

    boolean isCategoryExists(Long id);
}