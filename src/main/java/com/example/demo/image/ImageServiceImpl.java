package com.example.demo.image;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.image.dtos.ImageResponse;
import com.example.demo.image.util.ImageConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    public ImageServiceImpl(ImageRepository imageRepository, ImageMapper imageMapper) {
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
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
        return imageMapper.toResponse(saved);
    }

    @Override
    public ImageResponse uploadCategoryImage(Long categoryId, MultipartFile file) {
        validateFile(file);
        
        String folderPath = getFolderPath("categories", categoryId);
        String fileName = "image.jpg";
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "categories/" + categoryId + "/" + fileName;
        
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
        return imageMapper.toResponse(saved);
    }

    @Override
    public ImageResponse uploadSubcategoryImage(Long subcategoryId, MultipartFile file) {
        validateFile(file);
        
        String folderPath = getFolderPath("subcategories", subcategoryId);
        String fileName = "image.jpg";
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "subcategories/" + subcategoryId + "/" + fileName;
        
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
        return imageMapper.toResponse(saved);
    }

    @Override
    public ImageResponse uploadUserAvatar(Long userId, MultipartFile file) {
        validateFile(file);
        
        String folderPath = getFolderPath("users", userId);
        String fileName = "avatar.jpg";
        
        String fullPath = saveFile(file, folderPath, fileName);
        String imageUrl = ImageConstants.IMAGE_URL_PREFIX + "users/" + userId + "/" + fileName;
        
        imageRepository.deleteByEntityTypeAndEntityId("user", userId);
        
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
        return imageMapper.toResponse(saved);
    }

    // ========== ✅ STORE IMAGES ==========

    @Override
    public ImageResponse uploadStoreLogo(MultipartFile file) {
        validateFile(file);
        
        // Delete old logo
        deleteStoreLogo();
        
        String folderPath = getStoreFolderPath();
        String fileName = ImageConstants.STORE_LOGO;
        
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
        
        // Delete old favicon
        deleteStoreFavicon();
        
        String folderPath = getStoreFolderPath();
        String fileName = ImageConstants.STORE_FAVICON;
        
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
    }

    @Override
    public void deleteAllImages(String entityType, Long entityId) {
        List<ImageEntity> images = imageRepository.findByEntityTypeAndEntityIdOrderByDisplayOrderAsc(entityType, entityId);
        
        for (ImageEntity image : images) {
            deletePhysicalFile(image.getImageUrl());
        }
        
        imageRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
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

    // ========== HELPER METHODS ==========

    @Override
    public String getDefaultImage(String entityType) {
        switch (entityType.toLowerCase()) {
            case "product": return ImageConstants.DEFAULT_PRODUCT_IMAGE;
            case "user": return ImageConstants.DEFAULT_USER_AVATAR;
            case "category": return ImageConstants.DEFAULT_CATEGORY_IMAGE;
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
        if (!Arrays.asList(ImageConstants.ALLOWED_MIME_TYPES).contains(contentType)) {
            throw new RuntimeException("File type not allowed: " + contentType);
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
            if (ImageConstants.IMAGE_TYPE_MAIN.equalsIgnoreCase(imageType)) {
                return "main.jpg";
            } else if (ImageConstants.IMAGE_TYPE_THUMBNAIL.equalsIgnoreCase(imageType)) {
                return "thumbnail.jpg";
            } else {
                int order = displayOrder != null ? displayOrder : 0;
                return "gallery_" + order + ".jpg";
            }
        }
        return imageType + ".jpg";
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