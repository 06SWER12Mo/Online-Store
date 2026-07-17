package com.example.demo.image;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.image.dtos.ImageResponse;
import com.example.demo.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@Tag(name = "Images", description = "Endpoints for uploading, retrieving, and deleting images for products, variants, categories, subcategories, and user avatars")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // ========== PRODUCT IMAGES ==========

    @PostMapping(value = "/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload a product image",
            description = "Uploads an image file for the given product. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file or parameters", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<ImageResponse>> uploadProductImage(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId,
            @Parameter(description = "Image file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Type of image being uploaded", example = "GALLERY")
            @RequestParam(defaultValue = "GALLERY") String imageType,
            @Parameter(description = "Display order of the image in the gallery")
            @RequestParam(required = false) Integer displayOrder,
            @Parameter(description = "Alt text for accessibility/SEO")
            @RequestParam(required = false) String altText) {
        
        ImageResponse response = imageService.uploadProductImage(productId, file, imageType, displayOrder, altText);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }

    @GetMapping("/products/{productId}")
    @Operation(
            summary = "Get product images",
            description = "Returns all images associated with the given product."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Images retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ImageResponse>>> getProductImages(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(imageService.getProductImages(productId)));
    }

    @GetMapping("/products/{productId}/primary")
    @Operation(
            summary = "Get primary product image",
            description = "Returns the primary (main) image for the given product."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Primary image retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or primary image not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<ImageResponse>> getPrimaryProductImage(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId) {
        ImageResponse response = imageService.getPrimaryImage("product", productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/products/{productId}/primary/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Set primary product image",
            description = "Marks the given image as the primary image for the product. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Primary image updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or image not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> setPrimaryProductImage(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId,
            @Parameter(description = "ID of the image to set as primary", required = true)
            @PathVariable Long imageId) {
        imageService.setPrimaryImage("product", productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Primary image updated"));
    }

    // ========== VARIANT IMAGES ==========

    @PostMapping(value = "/variants/{variantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload a variant image",
            description = "Uploads an image file for the given product variant. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variant image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Variant not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<ImageResponse>> uploadVariantImage(
            @Parameter(description = "ID of the product variant", required = true)
            @PathVariable Long variantId,
            @Parameter(description = "Image file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file) {
        
        ImageResponse response = imageService.uploadVariantImage(variantId, file);
        return ResponseEntity.ok(ApiResponse.success("Variant image uploaded successfully", response));
    }

    // ========== CATEGORY IMAGES ==========

    @PostMapping(value = "/categories/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload a category image",
            description = "Uploads an image file for the given category. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<ImageResponse>> uploadCategoryImage(
            @Parameter(description = "ID of the category", required = true)
            @PathVariable Long categoryId,
            @Parameter(description = "Image file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file) {
        
        ImageResponse response = imageService.uploadCategoryImage(categoryId, file);
        return ResponseEntity.ok(ApiResponse.success("Category image uploaded successfully", response));
    }

    // ========== SUBCATEGORY IMAGES ==========

    @PostMapping(value = "/subcategories/{subcategoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload a subcategory image",
            description = "Uploads an image file for the given subcategory. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subcategory image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subcategory not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<ImageResponse>> uploadSubcategoryImage(
            @Parameter(description = "ID of the subcategory", required = true)
            @PathVariable Long subcategoryId,
            @Parameter(description = "Image file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file) {
        
        ImageResponse response = imageService.uploadSubcategoryImage(subcategoryId, file);
        return ResponseEntity.ok(ApiResponse.success("Subcategory image uploaded successfully", response));
    }

    // ========== USER AVATAR (Admin only) ==========

    @PostMapping(value = "/users/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload a user avatar (Admin only)",
            description = "Uploads an avatar image for the given user. Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Avatar uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<ImageResponse>> uploadUserAvatar(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Avatar image file to upload", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file) {
        
        ImageResponse response = imageService.uploadUserAvatar(userId, file);
        return ResponseEntity.ok(ApiResponse.success("Avatar uploaded successfully", response));
    }

    // ========== CURRENT USER AVATAR (Self-service) ==========

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload/Update current user's avatar",
            description = "Uploads or updates the avatar image for the currently authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Avatar uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<ImageResponse>> uploadCurrentUserAvatar(
            @Parameter(description = "Avatar image file", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        ImageResponse response = imageService.uploadUserAvatar(userPrincipal.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Avatar uploaded successfully", response));
    }

    @DeleteMapping("/me/avatar")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete current user's avatar",
            description = "Deletes the avatar for the currently authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Avatar deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Avatar not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUserAvatar(
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        imageService.deleteAllImages("user", userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Avatar deleted successfully"));
    }

    @GetMapping("/me/avatar")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get current user's avatar URL",
            description = "Returns the avatar URL for the currently authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Avatar retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    public ResponseEntity<ApiResponse<String>> getCurrentUserAvatar(
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String avatarUrl = imageService.getUserAvatarUrl(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(avatarUrl));
    }

    // ========== DELETE ==========

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete an image",
            description = "Deletes the image identified by the given id. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Image not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @Parameter(description = "ID of the image to delete", required = true)
            @PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully"));
    }

    @DeleteMapping("/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete all images for an entity",
            description = "Deletes all images associated with the given entity type and id " +
                    "(e.g. product, variant, category, subcategory, user). Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All images deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Entity not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deleteAllImages(
            @Parameter(description = "Type of the entity (e.g. product, variant, category, subcategory, user)", required = true, example = "product")
            @PathVariable String entityType,
            @Parameter(description = "ID of the entity", required = true)
            @PathVariable Long entityId) {
        imageService.deleteAllImages(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success("All images deleted successfully"));
    }
}