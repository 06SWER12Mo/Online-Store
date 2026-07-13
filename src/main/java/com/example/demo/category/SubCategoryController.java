package com.example.demo.category;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.category.dtos.SubCategoryRequest;
import com.example.demo.category.dtos.SubCategoryResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subcategories")
@Tag(name = "Sub Categories", description = "Endpoints for managing sub categories")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    // Admin endpoints
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SubCategoryResponse> createSubCategory(@Valid @RequestBody SubCategoryRequest request) {
        SubCategoryResponse response = subCategoryService.createSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SubCategoryResponse> updateSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryRequest request) {
        SubCategoryResponse response = subCategoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> toggleSubCategoryActive(@PathVariable Long id) {
        subCategoryService.toggleSubCategoryActive(id);
        return ResponseEntity.ok().build();
    }

    // Public endpoints
    @GetMapping
    public ResponseEntity<Page<SubCategoryResponse>> getAllSubCategories(
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SubCategoryResponse> subCategories = subCategoryService.getAllSubCategories(pageable);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategoryResponse> getSubCategoryById(@PathVariable Long id) {
        SubCategoryResponse response = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<SubCategoryResponse>> getSubCategoriesByParent(@PathVariable Long parentId) {
        List<SubCategoryResponse> subCategories = subCategoryService.getSubCategoriesByParent(parentId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/parent/{parentId}/active")
    public ResponseEntity<List<SubCategoryResponse>> getActiveSubCategoriesByParent(@PathVariable Long parentId) {
        List<SubCategoryResponse> subCategories = subCategoryService.getActiveSubCategoriesByParent(parentId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/parent/{parentId}/paginated")
    public ResponseEntity<Page<SubCategoryResponse>> getSubCategoriesByParentPaginated(
            @PathVariable Long parentId,
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SubCategoryResponse> subCategories = subCategoryService.getSubCategoriesByParentPaginated(parentId, pageable);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SubCategoryResponse>> searchSubCategories(@RequestParam String keyword) {
        List<SubCategoryResponse> subCategories = subCategoryService.searchSubCategories(keyword);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/parent/{parentId}/count")
    public ResponseEntity<Long> countSubCategoriesByParent(@PathVariable Long parentId) {
        long count = subCategoryService.countSubCategoriesByParent(parentId);
        return ResponseEntity.ok(count);
    }
}