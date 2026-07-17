package com.example.demo.image;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.image.dtos.ImageResponse;
import com.example.demo.image.util.ImageConstants;
import com.example.demo.product.ProductVariant;
import com.example.demo.product.ProductVariantRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;  
    private final ProductVariantRepository productVariantRepository;  

    public ImageServiceImpl(ImageRepository imageRepository, 
                           ImageMapper imageMapper,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductVariantRepository productVariantRepository) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productVariantRepository = productVariantRepository;
    }

    // ========== UPLOAD METHODS ==========

    @Override
    public ImageResponse uploadProductImage(Long productId, MultipartFile file, String imageType, Integer displayOrder, String altText) {
        validateFile(file);
        
        String folderPath = getFolderPath("products", productId);
        String fileName = generateFileName("product", productId, imageType, displayOrder);
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "products/" + productId + "/" + fileName;
        
        boolean isPrimary = "MAIN".equalsIgnoreCase(imageType) || 
                           (displayOrder != null && displayOrder == 0);
        
        if (isPrimary) {
            imageRepository.clearPrimaryImages("product", productId);
        }
        
        ImageEntity entity = imageMapper.toEntity(
            imageUrl,
            fileName,
            file.getContentType(),
            file.getSize(),
            "product",
            productId,
            imageType,
            displayOrder,
            altText,
            isPrimary
        );
        
        ImageEntity saved = imageRepository.save(entity);
        return imageMapper.toResponse(saved);
    }

    @Override
    public ImageResponse uploadVariantImage(Long variantId, MultipartFile file) {
        validateFile(file);
        
        String folderPath = getFolderPath("product-variants", variantId);
        String fileName = "image.jpg";
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "product-variants/" + variantId + "/" + fileName;
        
        deleteAllImages("variant", variantId);
        
        ImageEntity entity = imageMapper.toEntity(
            imageUrl,
            fileName,
            file.getContentType(),
            file.getSize(),
            "variant",
            variantId,
            "VARIANT",
            0,
            null,
            true
        );
        
        ImageEntity saved = imageRepository.save(entity);
        
        // ✅ Update the variant's imageUrl in the database
        updateVariantImageUrl(variantId, imageUrl);
        
        return imageMapper.toResponse(saved);
    }

    @Override
    public ImageResponse uploadCategoryImage(Long categoryId, MultipartFile file) {
        validateFile(file);
        
        String folderPath = getFolderPath("categories", categoryId);
        String fileName = "image.jpg";
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "categories/" + categoryId + "/" + fileName;
        
        deleteAllImages("category", categoryId);
        
        ImageEntity entity = imageMapper.toEntity(
            imageUrl,
            fileName,
            file.getContentType(),
            file.getSize(),
            "category",
            categoryId,
            "CATEGORY",
            0,
            null,
            true
        );
        
        ImageEntity saved = imageRepository.save(entity);
        
        updateCategoryImageUrl(categoryId, imageUrl);
        
        return imageMapper.toResponse(saved);
    }

    @Override
    public ImageResponse uploadSubcategoryImage(Long subcategoryId, MultipartFile file) {
        validateFile(file);
        
        String folderPath = getFolderPath("subcategories", subcategoryId);
        String fileName = "image.jpg";
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "subcategories/" + subcategoryId + "/" + fileName;
        
        deleteAllImages("subcategory", subcategoryId);
        
        ImageEntity entity = imageMapper.toEntity(
            imageUrl,
            fileName,
            file.getContentType(),
            file.getSize(),
            "subcategory",
            subcategoryId,
            "SUBCATEGORY",
            0,
            null,
            true
        );
        
        ImageEntity saved = imageRepository.save(entity);
        
        updateCategoryImageUrl(subcategoryId, imageUrl);
        
        return imageMapper.toResponse(saved);
    }

    @Override
    public ImageResponse uploadUserAvatar(Long userId, MultipartFile file) {
        validateFile(file);
        
        String folderPath = getFolderPath("users", userId);
        String fileName = "avatar.jpg";
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "users/" + userId + "/" + fileName;
        
        // Delete old avatar from database and filesystem
        deleteAllImages("user", userId);
        
        ImageEntity entity = imageMapper.toEntity(
            imageUrl,
            fileName,
            file.getContentType(),
            file.getSize(),
            "user",
            userId,
            "AVATAR",
            0,
            null,
            true
        );
        
        ImageEntity saved = imageRepository.save(entity);
        
        // Update user's profile picture URL
        updateUserAvatar(userId, imageUrl);
        
        return imageMapper.toResponse(saved);
    }

    // ========== STORE IMAGES ==========

    @Override
    public ImageResponse uploadStoreLogo(MultipartFile file) {
        validateFile(file);
        
        deleteStoreLogo();
        
        String folderPath = getStoreFolderPath();
        String fileName = "logo.png";
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "store/" + fileName;
        
        ImageEntity entity = imageMapper.toEntity(
            imageUrl,
            fileName,
            file.getContentType(),
            file.getSize(),
            "store",
            1L,
            "LOGO",
            0,
            "Store Logo",
            true
        );
        
        ImageEntity saved = imageRepository.save(entity);
        return imageMapper.toResponse(saved);
    }

    @Override
    public ImageResponse uploadStoreFavicon(MultipartFile file) {
        validateFile(file);
        
        deleteStoreFavicon();
        
        String folderPath = getStoreFolderPath();
        String fileName = "favicon.ico";
        
        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("image/x-icon")) {
            fileName = "favicon.png";
        }
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "store/" + fileName;
        
        ImageEntity entity = imageMapper.toEntity(
            imageUrl,
            fileName,
            file.getContentType(),
            file.getSize(),
            "store",
            1L,
            "FAVICON",
            0,
            "Store Favicon",
            true
        );
        
        ImageEntity saved = imageRepository.save(entity);
        return imageMapper.toResponse(saved);
    }

    @Override
    public void deleteStoreLogo() {
        List<ImageEntity> images = imageRepository.findByEntityTypeAndEntityIdAndImageType("store", 1L, "LOGO");
        for (ImageEntity image : images) {
            deletePhysicalFile(image.getImageUrl());
            imageRepository.delete(image);
        }
    }

    @Override
    public void deleteStoreFavicon() {
        List<ImageEntity> images = imageRepository.findByEntityTypeAndEntityIdAndImageType("store", 1L, "FAVICON");
        for (ImageEntity image : images) {
            deletePhysicalFile(image.getImageUrl());
            imageRepository.delete(image);
        }
    }

    @Override
    public String getStoreLogoUrl() {
        List<ImageEntity> images = imageRepository.findByEntityTypeAndEntityIdAndImageType("store", 1L, "LOGO");
        if (!images.isEmpty()) {
            return images.get(0).getImageUrl();
        }
        return ImageConstants.DEFAULT_STORE_LOGO;
    }

    @Override
    public String getStoreFaviconUrl() {
        List<ImageEntity> images = imageRepository.findByEntityTypeAndEntityIdAndImageType("store", 1L, "FAVICON");
        if (!images.isEmpty()) {
            return images.get(0).getImageUrl();
        }
        return ImageConstants.DEFAULT_STORE_FAVICON;
    }

    // ========== GET METHODS ==========

    @Override
    public List<ImageResponse> getProductImages(Long productId) {
        return imageMapper.toResponseList(
            imageRepository.findByEntityTypeAndEntityIdOrderByDisplayOrderAsc("product", productId)
        );
    }

    @Override
    public List<ImageResponse> getImagesByEntity(String entityType, Long entityId) {
        return imageMapper.toResponseList(
            imageRepository.findByEntityTypeAndEntityIdOrderByDisplayOrderAsc(entityType, entityId)
        );
    }

    @Override
    public ImageResponse getPrimaryImage(String entityType, Long entityId) {
        return imageRepository.findPrimaryImage(entityType, entityId)
            .map(imageMapper::toResponse)
            .orElse(null);
    }

    // ========== DELETE METHODS ==========

    @Override
    public void deleteImage(Long imageId) {
        ImageEntity entity = imageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
        
        deletePhysicalFile(entity.getImageUrl());
        imageRepository.delete(entity);
        
        // Check if any images remain for this entity
        long count = imageRepository.countByEntityTypeAndEntityId(entity.getEntityType(), entity.getEntityId());
        
        // If no images remain, clear the imageUrl in the entity
        if (count == 0) {
            clearEntityImageUrl(entity.getEntityType(), entity.getEntityId());
        }
    }

    @Override
    public void deleteAllImages(String entityType, Long entityId) {
        List<ImageEntity> images = imageRepository.findByEntityTypeAndEntityIdOrderByDisplayOrderAsc(entityType, entityId);
        
        for (ImageEntity image : images) {
            deletePhysicalFile(image.getImageUrl());
        }
        
        imageRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
        
        // Clear the imageUrl in the entity
        clearEntityImageUrl(entityType, entityId);
    }

    // ========== UPDATE METHODS ==========

    @Override
    public void setPrimaryImage(String entityType, Long entityId, Long imageId) {
        ImageEntity entity = imageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
        
        if (!entity.getEntityType().equals(entityType) || !entity.getEntityId().equals(entityId)) {
            throw new RuntimeException("Image does not belong to this entity");
        }
        
        imageRepository.clearPrimaryImages(entityType, entityId);
        entity.setPrimary(true);
        imageRepository.save(entity);
    }

    // ========== USER AVATAR HELPER METHODS ==========

    @Override
    public String getUserAvatarUrl(Long userId) {
        List<ImageEntity> avatars = imageRepository.findByEntityTypeAndEntityIdAndImageType("user", userId, "AVATAR");
        if (!avatars.isEmpty()) {
            return avatars.get(0).getImageUrl();
        }
        return getDefaultImage("user");
    }

    @Override
    public void updateUserAvatar(Long userId, String imageUrl) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            user.setProfilePictureUrl(imageUrl);
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Failed to update user profile picture: " + e.getMessage());
        }
    }

    @Override
    public void clearUserAvatar(Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            user.setProfilePictureUrl(null);
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Failed to clear user profile picture: " + e.getMessage());
        }
    }

    // ========== ENTITY IMAGE URL UPDATE METHODS ==========

    private void updateCategoryImageUrl(Long categoryId, String imageUrl) {
        try {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            category.setImageUrl(imageUrl);
            categoryRepository.save(category);
        } catch (Exception e) {
            System.err.println("Failed to update category image URL: " + e.getMessage());
        }
    }

    private void updateVariantImageUrl(Long variantId, String imageUrl) {
        try {
            ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + variantId));
            variant.setImageUrl(imageUrl);
            productVariantRepository.save(variant);
        } catch (Exception e) {
            System.err.println("Failed to update variant image URL: " + e.getMessage());
        }
    }

    private void clearEntityImageUrl(String entityType, Long entityId) {
        try {
            switch (entityType.toLowerCase()) {
                case "category":
                    Category category = categoryRepository.findById(entityId).orElse(null);
                    if (category != null) {
                        category.setImageUrl(null);
                        categoryRepository.save(category);
                    }
                    break;
                case "subcategory":
                    Category subCategory = categoryRepository.findById(entityId).orElse(null);
                    if (subCategory != null) {
                        subCategory.setImageUrl(null);
                        categoryRepository.save(subCategory);
                    }
                    break;
                case "variant":
                    ProductVariant variant = productVariantRepository.findById(entityId).orElse(null);
                    if (variant != null) {
                        variant.setImageUrl(null);
                        productVariantRepository.save(variant);
                    }
                    break;
                case "user":
                    clearUserAvatar(entityId);
                    break;
                default:
                    // Do nothing for other entity types
                    break;
            }
        } catch (Exception e) {
            System.err.println("Failed to clear entity image URL: " + e.getMessage());
        }
    }

    // ========== HELPER METHODS ==========

    @Override
    public String getDefaultImage(String entityType) {
        switch (entityType.toLowerCase()) {
            case "product": return ImageConstants.DEFAULT_PRODUCT_IMAGE;
            case "user": return ImageConstants.DEFAULT_USER_AVATAR;
            case "category": return ImageConstants.DEFAULT_CATEGORY_IMAGE;
            case "subcategory": return ImageConstants.DEFAULT_SUBCATEGORY_IMAGE;
            case "variant": return ImageConstants.DEFAULT_VARIANT_IMAGE;
            case "store": return ImageConstants.DEFAULT_STORE_LOGO;
            default: return "/images/default.png";
        }
    }

    // ========== PRIVATE METHODS ==========

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > ImageConstants.MAX_FILE_SIZE) {
            throw new RuntimeException("File too large. Max size: 5MB");
        }
        
        String contentType = file.getContentType();
        boolean isValidType = false;
        for (String allowedType : ImageConstants.ALLOWED_MIME_TYPES) {
            if (allowedType.equals(contentType)) {
                isValidType = true;
                break;
            }
        }
        
        if (!isValidType) {
            throw new RuntimeException("File type not allowed: " + contentType + 
                                     ". Allowed types: " + String.join(", ", ImageConstants.ALLOWED_MIME_TYPES));
        }
    }

    private String getFolderPath(String entityType, Long entityId) {
        String folderPath = ImageConstants.BASE_PATH + File.separator + entityType + File.separator + entityId;
        File folder = new File(folderPath);
        
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        return folderPath;
    }

    private String getStoreFolderPath() {
        String folderPath = ImageConstants.BASE_PATH + File.separator + "store";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folderPath;
    }

    private String saveFile(MultipartFile file, String folderPath, String fileName) {
        try {
            String fullPath = folderPath + File.separator + fileName;
            Path path = Paths.get(fullPath);
            Files.write(path, file.getBytes());
            return fullPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    private String generateFileName(String entityType, Long entityId, String imageType, Integer displayOrder) {
        if ("product".equalsIgnoreCase(entityType)) {
            if ("MAIN".equalsIgnoreCase(imageType)) {
                return "main.jpg";
            } else if ("THUMBNAIL".equalsIgnoreCase(imageType)) {
                return "thumbnail.jpg";
            } else {
                int order = displayOrder != null ? displayOrder : 0;
                return "gallery_" + order + ".jpg";
            }
        }
        return "image.jpg";
    }

    private void deletePhysicalFile(String imageUrl) {
        try {
            String relativePath = imageUrl.replace(ImageConstants.IMAGE_URL_PREFIX, "");
            String fullPath = ImageConstants.BASE_PATH + File.separator + relativePath;
            
            File file = new File(fullPath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            System.err.println("Failed to delete physical file: " + e.getMessage());
        }
    }
}