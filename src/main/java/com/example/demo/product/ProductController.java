package com.example.demo.product;

import com.example.demo.common.dtos.ErrorResponse;
import com.example.demo.product.dtos.ProductRequest;
import com.example.demo.product.dtos.ProductResponse;
import com.example.demo.product.dtos.ProductSearchRequest;
import com.example.demo.product.dtos.ProductSpecificationRequest;
import com.example.demo.product.dtos.ProductSummaryResponse;
import com.example.demo.product.dtos.ProductUpdateRequest;
import com.example.demo.product.dtos.ProductVariantRequest;
import com.example.demo.product.dtos.ProductVariantResponse;

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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "Endpoints for managing products, specifications, and variants")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ============================================================
    // ADMIN/MANAGER ENDPOINTS
    // ============================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Create a new product",
        description = "Creates a new product with specifications and variants. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation failed or SKU already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Update a product",
        description = "Updates an existing product by ID. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation failed or SKU already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Delete a product",
        description = "Permanently deletes a product by ID. Also deletes all associated images. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Toggle product active status",
        description = "Activates or deactivates a product. Inactive products are not visible to customers. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product status toggled successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> toggleActive(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        productService.toggleActive(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle-featured")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Toggle product featured status",
        description = "Marks or unmarks a product as featured. Featured products appear on the homepage. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Featured status toggled successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> toggleFeatured(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        productService.toggleFeatured(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Update product stock",
        description = "Sets the exact stock quantity for a product. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> updateStock(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "New stock quantity", required = true, example = "50")
            @RequestParam Integer quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/increment-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Increment product stock",
        description = "Adds to the current stock quantity. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock incremented successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> incrementStock(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Quantity to add", required = true, example = "10")
            @RequestParam Integer quantity) {
        productService.incrementStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/decrement-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Decrement product stock",
        description = "Subtracts from the current stock quantity. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock decremented successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Insufficient stock",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> decrementStock(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Quantity to subtract", required = true, example = "5")
            @RequestParam Integer quantity) {
        productService.decrementStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    // ============================================================
    // SPECIFICATION MANAGEMENT
    // ============================================================

    @PostMapping("/{productId}/specifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Add specification to product",
        description = "Adds a technical specification to a product. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Specification added successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> addSpecification(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long productId,
            @Valid @RequestBody ProductSpecificationRequest request) {
        productService.addSpecification(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/specifications/{specificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Update specification",
        description = "Updates an existing product specification. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Specification updated successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Specification not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> updateSpecification(
            @Parameter(description = "Specification ID", required = true, example = "1")
            @PathVariable Long specificationId,
            @Valid @RequestBody ProductSpecificationRequest request) {
        productService.updateSpecification(specificationId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/specifications/{specificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Remove specification",
        description = "Permanently removes a product specification. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Specification removed successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Specification not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> removeSpecification(
            @Parameter(description = "Specification ID", required = true, example = "1")
            @PathVariable Long specificationId) {
        productService.removeSpecification(specificationId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // VARIANT MANAGEMENT
    // ============================================================

    @PostMapping("/{productId}/variants")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Add variant to product",
        description = "Adds a new variant (e.g., size, color) to a product. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Variant added successfully",
            content = @Content(schema = @Schema(implementation = ProductVariantResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Variant SKU already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ProductVariantResponse> addVariant(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantRequest request) {
        ProductVariantResponse response = productService.addVariant(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/variants/{variantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Update variant",
        description = "Updates an existing product variant. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Variant updated successfully",
            content = @Content(schema = @Schema(implementation = ProductVariantResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Variant SKU already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Variant not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ProductVariantResponse> updateVariant(
            @Parameter(description = "Variant ID", required = true, example = "1")
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        ProductVariantResponse response = productService.updateVariant(variantId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/variants/{variantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Remove variant",
        description = "Permanently removes a product variant. Also deletes associated images. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Variant removed successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Variant not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> removeVariant(
            @Parameter(description = "Variant ID", required = true, example = "1")
            @PathVariable Long variantId) {
        productService.removeVariant(variantId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // PUBLIC ENDPOINTS
    // ============================================================

    @GetMapping("/{id}")
    @Operation(
        summary = "Get product by ID",
        description = "Retrieves a product with all details including images, specifications, and variants."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product found",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        productService.incrementViewCount(id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    @Operation(
        summary = "Get product summary",
        description = "Retrieves a lightweight product summary with key details."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product summary found",
            content = @Content(schema = @Schema(implementation = ProductSummaryResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ProductSummaryResponse> getProductSummary(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        ProductSummaryResponse response = productService.getProductSummaryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Get all products",
        description = "Retrieves a paginated list of all products with full details."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/summaries")
    @Operation(
        summary = "Get all product summaries",
        description = "Retrieves a paginated list of lightweight product summaries."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product summaries retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<ProductSummaryResponse>> getAllProductSummaries(
            @Parameter(description = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductSummaryResponse> products = productService.getAllProductSummaries(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search products",
        description = "Searches products by keyword across name, description, and short description."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @Parameter(description = "Search keyword", required = true, example = "laptop")
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/search/advanced")
    @Operation(
        summary = "Advanced product search",
        description = "Searches products with multiple filters including price range, category, stock status, and more."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> advancedSearch(
            @RequestBody ProductSearchRequest searchRequest) {
        Page<ProductResponse> products = productService.searchProducts(searchRequest);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
        summary = "Get products by category",
        description = "Retrieves all products belonging to a specific category."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}/active")
    @Operation(
        summary = "Get active products by category",
        description = "Retrieves only active products belonging to a specific category."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Active products retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> getActiveProductsByCategory(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getActiveProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/featured")
    @Operation(
        summary = "Get featured products",
        description = "Retrieves all products marked as featured."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Featured products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        )
    })
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        List<ProductResponse> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/featured/paginated")
    @Operation(
        summary = "Get featured products (paginated)",
        description = "Retrieves a paginated list of products marked as featured."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Featured products retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> getFeaturedProductsPaginated(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/in-stock")
    @Operation(
        summary = "Get products in stock",
        description = "Retrieves all products that are currently in stock."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "In-stock products retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> getInStockProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getInStockProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get low stock products (Admin/Manager only)",
        description = "Retrieves products that are below their low stock threshold. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Low stock products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
        List<ProductResponse> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
        summary = "Get low stock products (paginated, Admin/Manager only)",
        description = "Retrieves a paginated list of products below their low stock threshold. Only accessible to ADMIN or MANAGER.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Low stock products retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin or Manager role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> getLowStockProductsPaginated(
            @PageableDefault(size = 20, sort = "stockQuantity", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponse> products = productService.getLowStockProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    @Operation(
        summary = "Get products by price range",
        description = "Retrieves products within a specific price range."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> getProductsByPriceRange(
            @Parameter(description = "Minimum price", required = true, example = "10.00")
            @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price", required = true, example = "100.00")
            @RequestParam BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductResponse> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/on-sale")
    @Operation(
        summary = "Get discounted products",
        description = "Retrieves products that have a compare-at-price higher than the current price (on sale)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discounted products retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<ProductResponse>> getDiscountedProducts(
            @PageableDefault(size = 20, sort = "discountPercentage", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> products = productService.getDiscountedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-rated")
    @Operation(
        summary = "Get top rated products",
        description = "Retrieves products with the highest average ratings."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Top rated products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        )
    })
    public ResponseEntity<List<ProductResponse>> getTopRatedProducts(
            @PageableDefault(size = 10, sort = "averageRating", direction = Sort.Direction.DESC) Pageable pageable) {
        List<ProductResponse> products = productService.getTopRatedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/new-arrivals")
    @Operation(
        summary = "Get new arrivals",
        description = "Retrieves the newest products (by creation date)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "New arrivals retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        )
    })
    public ResponseEntity<List<ProductResponse>> getNewArrivals(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        List<ProductResponse> products = productService.getNewArrivals(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/best-sellers")
    @Operation(
        summary = "Get best sellers",
        description = "Retrieves products with the highest sold counts."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Best sellers retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        )
    })
    public ResponseEntity<List<ProductResponse>> getBestSellers(
            @PageableDefault(size = 10, sort = "soldCount", direction = Sort.Direction.DESC) Pageable pageable) {
        List<ProductResponse> products = productService.getBestSellers(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/most-viewed")
    @Operation(
        summary = "Get most viewed products",
        description = "Retrieves products with the highest view counts."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Most viewed products retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        )
    })
    public ResponseEntity<List<ProductResponse>> getMostViewed(
            @PageableDefault(size = 10, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {
        List<ProductResponse> products = productService.getMostViewed(pageable);
        return ResponseEntity.ok(products);
    }

    // ============================================================
    // STATISTICS
    // ============================================================

    @GetMapping("/stats/count")
    @Operation(
        summary = "Get total product count",
        description = "Returns the total number of products in the system."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(schema = @Schema(implementation = Long.class))
        )
    })
    public ResponseEntity<Long> countTotalProducts() {
        return ResponseEntity.ok(productService.countTotalProducts());
    }

    @GetMapping("/stats/count/active")
    @Operation(
        summary = "Get active product count",
        description = "Returns the number of active (enabled) products."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(schema = @Schema(implementation = Long.class))
        )
    })
    public ResponseEntity<Long> countActiveProducts() {
        return ResponseEntity.ok(productService.countActiveProducts());
    }

    @GetMapping("/stats/count/category/{categoryId}")
    @Operation(
        summary = "Get product count by category",
        description = "Returns the number of products in a specific category."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(schema = @Schema(implementation = Long.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Long> countProductsByCategory(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.countProductsByCategory(categoryId));
    }
}