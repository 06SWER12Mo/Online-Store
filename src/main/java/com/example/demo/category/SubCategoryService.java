package com.example.demo.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.category.dtos.SubCategoryRequest;
import com.example.demo.category.dtos.SubCategoryResponse;

import java.util.List;

public interface SubCategoryService {

    SubCategoryResponse createSubCategory(SubCategoryRequest request);

    SubCategoryResponse updateSubCategory(Long id, SubCategoryRequest request);

    void deleteSubCategory(Long id);

    SubCategoryResponse getSubCategoryById(Long id);

    Page<SubCategoryResponse> getAllSubCategories(Pageable pageable);

    List<SubCategoryResponse> getSubCategoriesByParent(Long parentId);

    Page<SubCategoryResponse> getSubCategoriesByParentPaginated(Long parentId, Pageable pageable);

    List<SubCategoryResponse> getActiveSubCategoriesByParent(Long parentId);

    void toggleSubCategoryActive(Long id);

    List<SubCategoryResponse> searchSubCategories(String keyword);

    long countSubCategoriesByParent(Long parentId);
}