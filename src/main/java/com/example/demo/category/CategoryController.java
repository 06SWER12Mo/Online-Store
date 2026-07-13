package com.example.demo.category;

import com.example.demo.category.dtos.CategoryRequest;
import com.example.demo.category.dtos.CategoryResponse;
import com.example.demo.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category", description = "Endpoints for managing and browsing product categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ========== ADMIN ENDPOINTS ==========

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create a new category",
            description = "Creates a new category. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update an existing category",
            description = "Updates the category identified by the given id. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "ID of the category to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete a category",
            description = "Deletes the category identified by the given id. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true)
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Toggle category active status",
            description = "Flips the active/inactive status of the category identified by the given id. " +
                    "Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category status toggled successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<Void> toggleCategoryActive(
            @Parameter(description = "ID of the category to toggle", required = true)
            @PathVariable Long id) {
        categoryService.toggleCategoryActive(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/display-order")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update category display order",
            description = "Updates the display order (sort position) of the category identified by the given id. " +
                    "Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Display order updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid display order value", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<Void> updateDisplayOrder(
            @Parameter(description = "ID of the category to reorder", required = true)
            @PathVariable Long id,
            @Parameter(description = "New display order value", required = true)
            @RequestParam Integer displayOrder) {
        categoryService.updateCategoryDisplayOrder(id, displayOrder);
        return ResponseEntity.ok().build();
    }

    // ========== PUBLIC ENDPOINTS ==========

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Returns a paginated list of all categories, sorted by display order by default."
    )
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CategoryResponse> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/root")
    @Operation(
            summary = "Get root categories",
            description = "Returns all top-level (parent-less) categories."
    )
    @ApiResponse(responseCode = "200", description = "Root categories retrieved successfully")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        List<CategoryResponse> rootCategories = categoryService.getRootCategories();
        return ResponseEntity.ok(rootCategories);
    }

    @GetMapping("/root/active")
    @Operation(
            summary = "Get active root categories",
            description = "Returns all top-level categories that are currently active."
    )
    @ApiResponse(responseCode = "200", description = "Active root categories retrieved successfully")
    public ResponseEntity<List<CategoryResponse>> getActiveRootCategories() {
        List<CategoryResponse> rootCategories = categoryService.getActiveRootCategories();
        return ResponseEntity.ok(rootCategories);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by id",
            description = "Returns the category identified by the given id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "ID of the category to retrieve", required = true)
            @PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{parentId}/subcategories")
    @Operation(
            summary = "Get subcategories",
            description = "Returns all direct subcategories of the given parent category."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategories retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parent category not found", content = @Content)
    })
    public ResponseEntity<List<CategoryResponse>> getSubCategories(
            @Parameter(description = "ID of the parent category", required = true)
            @PathVariable Long parentId) {
        List<CategoryResponse> subCategories = categoryService.getSubCategories(parentId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{parentId}/subcategories/active")
    @Operation(
            summary = "Get active subcategories",
            description = "Returns all active direct subcategories of the given parent category."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active subcategories retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parent category not found", content = @Content)
    })
    public ResponseEntity<List<CategoryResponse>> getActiveSubCategories(
            @Parameter(description = "ID of the parent category", required = true)
            @PathVariable Long parentId) {
        List<CategoryResponse> subCategories = categoryService.getActiveSubCategories(parentId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search categories",
            description = "Searches categories by matching the given keyword against category name/description."
    )
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<CategoryResponse>> searchCategories(
            @Parameter(description = "Keyword to search for", required = true, example = "electronics")
            @RequestParam String keyword) {
        List<CategoryResponse> categories = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{parentId}/count")
    @Operation(
            summary = "Count subcategories",
            description = "Returns the number of direct subcategories under the given parent category."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parent category not found", content = @Content)
    })
    public ResponseEntity<Long> countSubCategories(
            @Parameter(description = "ID of the parent category", required = true)
            @PathVariable Long parentId) {
        long count = categoryService.countSubCategories(parentId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exists/{id}")
    @Operation(
            summary = "Check if category exists",
            description = "Returns whether a category with the given id exists."
    )
    @ApiResponse(responseCode = "200", description = "Existence check completed successfully")
    public ResponseEntity<Boolean> isCategoryExists(
            @Parameter(description = "ID of the category to check", required = true)
            @PathVariable Long id) {
        boolean exists = categoryService.isCategoryExists(id);
        return ResponseEntity.ok(exists);
    }
}