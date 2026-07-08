package com.example.demo.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        // Check if category name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        // Get parent category if provided
        Category parentCategory = null;
        if (request.getParentId() != null) {
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
        }

        Category category = categoryMapper.toEntity(request, parentCategory);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryById(id);

        // Check if category name already exists (excluding current category)
        if (request.getName() != null && categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        // Get parent category if provided
        Category parentCategory = null;
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new RuntimeException("Category cannot be its own parent");
            }
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
        }

        // Update category
        categoryMapper.updateEntity(category, request, parentCategory);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);

        // Check if category has sub-categories
        if (!category.getSubCategories().isEmpty()) {
            throw new RuntimeException("Cannot delete category with sub-categories. Please delete sub-categories first.");
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return categoryMapper.toResponse(category);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    public List<CategoryResponse> getRootCategories() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        return categoryMapper.toResponseList(rootCategories);
    }

    @Override
    public List<CategoryResponse> getSubCategories(Long parentId) {
        List<Category> subCategories = categoryRepository.findByParentCategoryId(parentId);
        return categoryMapper.toResponseList(subCategories);
    }

    @Override
    public Page<CategoryResponse> getRootCategoriesPaginated(Pageable pageable) {
        return categoryRepository.findByParentCategoryIsNull(pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    public Page<CategoryResponse> getSubCategoriesPaginated(Long parentId, Pageable pageable) {
        return categoryRepository.findByParentCategoryId(parentId, pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    public List<CategoryResponse> getActiveRootCategories() {
        List<Category> rootCategories = categoryRepository.findActiveRootCategories();
        return categoryMapper.toResponseList(rootCategories);
    }

    @Override
    public List<CategoryResponse> getActiveSubCategories(Long parentId) {
        List<Category> subCategories = categoryRepository.findActiveSubCategories(parentId);
        return categoryMapper.toResponseList(subCategories);
    }

    @Override
    public void toggleCategoryActive(Long id) {
        Category category = findCategoryById(id);
        category.setActive(!category.isActive());
        categoryRepository.save(category);
    }

    @Override
    public void updateCategoryDisplayOrder(Long id, Integer displayOrder) {
        categoryRepository.updateDisplayOrder(id, displayOrder);
    }

    @Override
    public List<CategoryResponse> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.searchCategories(keyword);
        return categoryMapper.toResponseList(categories);
    }

    @Override
    public long countSubCategories(Long parentId) {
        return categoryRepository.countSubCategories(parentId);
    }

    @Override
    public boolean isCategoryExists(Long id) {
        return categoryRepository.existsById(id);
    }

    // Helper method
    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
}