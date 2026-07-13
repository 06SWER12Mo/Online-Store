package com.example.demo.category;

import com.example.demo.category.dtos.CategoryRequest;
import com.example.demo.category.dtos.CategoryResponse;
import com.example.demo.image.ImageService;
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
    
    // ✅ ImageService
    private final ImageService imageService;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper,
                               ImageService imageService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.imageService = imageService;
    }

    // ========== CREATE ==========

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        Category parentCategory = null;
        if (request.getParentId() != null) {
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
        }

        Category category = categoryMapper.toEntity(request, parentCategory);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    // ========== UPDATE ==========

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryById(id);

        if (request.getName() != null && categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        Category parentCategory = null;
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new RuntimeException("Category cannot be its own parent");
            }
            parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
        }

        categoryMapper.updateEntity(category, request, parentCategory);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    // ========== DELETE ==========

    @Override
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);

        if (!category.getSubCategories().isEmpty()) {
            throw new RuntimeException("Cannot delete category with sub-categories. Please delete sub-categories first.");
        }

        // ✅ Delete category image via ImageService
        imageService.deleteAllImages("category", id);

        categoryRepository.delete(category);
    }

    // ========== GET ==========

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

    // ========== TOGGLE ==========

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

    // ========== SEARCH ==========

    @Override
    public List<CategoryResponse> searchCategories(String keyword) {
        List<Category> categories = categoryRepository.searchCategories(keyword);
        return categoryMapper.toResponseList(categories);
    }

    // ========== COUNT ==========

    @Override
    public long countSubCategories(Long parentId) {
        return categoryRepository.countSubCategories(parentId);
    }

    @Override
    public boolean isCategoryExists(Long id) {
        return categoryRepository.existsById(id);
    }

    // ========== HELPER ==========

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
}