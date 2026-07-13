package com.example.demo.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.category.dtos.SubCategoryRequest;
import com.example.demo.category.dtos.SubCategoryResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository,
                                  CategoryRepository categoryRepository,
                                  CategoryMapper categoryMapper) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public SubCategoryResponse createSubCategory(SubCategoryRequest request) {
        // Check if parent category exists
        Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentCategoryId()));

        // Check if category name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Subcategory with name '" + request.getName() + "' already exists");
        }

        // Create category as subcategory
        Category subCategory = new Category();
        subCategory.setName(request.getName());
        subCategory.setDescription(request.getDescription());
        subCategory.setImageUrl(request.getImageUrl());
        subCategory.setActive(request.getActive() != null ? request.getActive() : true);
        subCategory.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        subCategory.setParentCategory(parentCategory);

        Category savedSubCategory = subCategoryRepository.save(subCategory);
        return new SubCategoryResponse(savedSubCategory);
    }

    @Override
    public SubCategoryResponse updateSubCategory(Long id, SubCategoryRequest request) {
        Category subCategory = findSubCategoryById(id);

        // Check if category name already exists (excluding current category)
        if (request.getName() != null && categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Subcategory with name '" + request.getName() + "' already exists");
        }

        // Update parent category if changed
        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentCategoryId()));
            subCategory.setParentCategory(parentCategory);
        }

        // Update fields
        if (request.getName() != null) {
            subCategory.setName(request.getName());
        }
        if (request.getDescription() != null) {
            subCategory.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            subCategory.setImageUrl(request.getImageUrl());
        }
        if (request.getActive() != null) {
            subCategory.setActive(request.getActive());
        }
        if (request.getDisplayOrder() != null) {
            subCategory.setDisplayOrder(request.getDisplayOrder());
        }

        Category updatedSubCategory = subCategoryRepository.save(subCategory);
        return new SubCategoryResponse(updatedSubCategory);
    }

    @Override
    public void deleteSubCategory(Long id) {
        Category subCategory = findSubCategoryById(id);
        subCategoryRepository.delete(subCategory);
    }

    @Override
    public SubCategoryResponse getSubCategoryById(Long id) {
        Category subCategory = findSubCategoryById(id);
        return new SubCategoryResponse(subCategory);
    }

    @Override
    public Page<SubCategoryResponse> getAllSubCategories(Pageable pageable) {
        return subCategoryRepository.findAllSubCategories(pageable)
                .map(SubCategoryResponse::new);
    }

    @Override
    public List<SubCategoryResponse> getSubCategoriesByParent(Long parentId) {
        return subCategoryRepository.findByParentCategoryId(parentId)
                .stream()
                .map(SubCategoryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SubCategoryResponse> getSubCategoriesByParentPaginated(Long parentId, Pageable pageable) {
        return subCategoryRepository.findByParentCategoryId(parentId, pageable)
                .map(SubCategoryResponse::new);
    }

    @Override
    public List<SubCategoryResponse> getActiveSubCategoriesByParent(Long parentId) {
        return subCategoryRepository.findActiveByParentCategoryId(parentId)
                .stream()
                .map(SubCategoryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleSubCategoryActive(Long id) {
        Category subCategory = findSubCategoryById(id);
        subCategory.setActive(!subCategory.isActive());
        subCategoryRepository.save(subCategory);
    }

    @Override
    public List<SubCategoryResponse> searchSubCategories(String keyword) {
        return subCategoryRepository.searchSubCategories(keyword)
                .stream()
                .map(SubCategoryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public long countSubCategoriesByParent(Long parentId) {
        return subCategoryRepository.countByParentCategoryId(parentId);
    }

    // Helper method
    private Category findSubCategoryById(Long id) {
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + id));
    }
}