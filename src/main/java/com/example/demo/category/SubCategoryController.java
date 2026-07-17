package com.example.demo.category;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.category.dtos.SubCategoryRequest;
import com.example.demo.category.dtos.SubCategoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a sub category", description = "Creates a new sub category under a parent category. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Sub category created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubCategoryResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent category not found")
    })
    public ResponseEntity<SubCategoryResponse> createSubCategory(@Valid @RequestBody SubCategoryRequest request) {
        SubCategoryResponse response = subCategoryService.createSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a sub category", description = "Updates an existing sub category's details. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sub category updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sub category not found")
    })
    public ResponseEntity<SubCategoryResponse> updateSubCategory(
            @Parameter(description = "ID of the sub category to update", required = true) @PathVariable Long id,
            @Valid @RequestBody SubCategoryRequest request) {
        SubCategoryResponse response = subCategoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a sub category", description = "Deletes a sub category by ID. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Sub category deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sub category not found")
    })
    public ResponseEntity<Void> deleteSubCategory(
            @Parameter(description = "ID of the sub category to delete", required = true) @PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle sub category active status", description = "Toggles a sub category between active and inactive states. Requires ADMIN or MANAGER role.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sub category status toggled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sub category not found")
    })
    public ResponseEntity<Void> toggleSubCategoryActive(
            @Parameter(description = "ID of the sub category to toggle", required = true) @PathVariable Long id) {
        subCategoryService.toggleSubCategoryActive(id);
        return ResponseEntity.ok().build();
    }

    // Public endpoints

    @GetMapping
    @Operation(summary = "Get all sub categories", description = "Retrieves a paginated list of all sub categories, sorted by display order ascending by default. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sub categories retrieved successfully")
    })
    public ResponseEntity<Page<SubCategoryResponse>> getAllSubCategories(
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SubCategoryResponse> subCategories = subCategoryService.getAllSubCategories(pageable);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sub category by ID", description = "Retrieves a single sub category by its ID. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Sub category found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubCategoryResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sub category not found")
    })
    public ResponseEntity<SubCategoryResponse> getSubCategoryById(
            @Parameter(description = "ID of the sub category to retrieve", required = true) @PathVariable Long id) {
        SubCategoryResponse response = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parent/{parentId}")
    @Operation(summary = "Get sub categories by parent", description = "Retrieves all sub categories belonging to a given parent category. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sub categories retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent category not found")
    })
    public ResponseEntity<List<SubCategoryResponse>> getSubCategoriesByParent(
            @Parameter(description = "ID of the parent category", required = true) @PathVariable Long parentId) {
        List<SubCategoryResponse> subCategories = subCategoryService.getSubCategoriesByParent(parentId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/parent/{parentId}/active")
    @Operation(summary = "Get active sub categories by parent", description = "Retrieves only active sub categories belonging to a given parent category. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active sub categories retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent category not found")
    })
    public ResponseEntity<List<SubCategoryResponse>> getActiveSubCategoriesByParent(
            @Parameter(description = "ID of the parent category", required = true) @PathVariable Long parentId) {
        List<SubCategoryResponse> subCategories = subCategoryService.getActiveSubCategoriesByParent(parentId);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/parent/{parentId}/paginated")
    @Operation(summary = "Get sub categories by parent (paginated)", description = "Retrieves a paginated list of sub categories belonging to a given parent category. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sub categories retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent category not found")
    })
    public ResponseEntity<Page<SubCategoryResponse>> getSubCategoriesByParentPaginated(
            @Parameter(description = "ID of the parent category", required = true) @PathVariable Long parentId,
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SubCategoryResponse> subCategories = subCategoryService.getSubCategoriesByParentPaginated(parentId, pageable);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/search")
    @Operation(summary = "Search sub categories", description = "Searches sub categories by a keyword matching name or other identifying fields. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results returned successfully")
    })
    public ResponseEntity<List<SubCategoryResponse>> searchSubCategories(
            @Parameter(description = "Keyword to search for", required = true) @RequestParam String keyword) {
        List<SubCategoryResponse> subCategories = subCategoryService.searchSubCategories(keyword);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/parent/{parentId}/count")
    @Operation(summary = "Count sub categories by parent", description = "Counts the number of sub categories belonging to a given parent category. No authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count calculated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent category not found")
    })
    public ResponseEntity<Long> countSubCategoriesByParent(
            @Parameter(description = "ID of the parent category", required = true) @PathVariable Long parentId) {
        long count = subCategoryService.countSubCategoriesByParent(parentId);
        return ResponseEntity.ok(count);
    }
}