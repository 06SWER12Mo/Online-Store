package com.example.demo.image;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.image.dtos.ImageResponse;

import java.util.List;

public interface ImageService {

    // ========== UPLOAD METHODS ==========

    ImageResponse uploadProductImage(Long productId, MultipartFile file, String imageType, Integer displayOrder, String altText);

    ImageResponse uploadVariantImage(Long variantId, MultipartFile file);

    ImageResponse uploadCategoryImage(Long categoryId, MultipartFile file);

    ImageResponse uploadSubcategoryImage(Long subcategoryId, MultipartFile file);

    ImageResponse uploadUserAvatar(Long userId, MultipartFile file);

    // ========== ✅ STORE IMAGES ==========
    
    ImageResponse uploadStoreLogo(MultipartFile file);
    
    ImageResponse uploadStoreFavicon(MultipartFile file);
    
    void deleteStoreLogo();
    
    void deleteStoreFavicon();
    
    String getStoreLogoUrl();
    
    String getStoreFaviconUrl();

    // ========== GET METHODS ==========

    List<ImageResponse> getProductImages(Long productId);

    List<ImageResponse> getImagesByEntity(String entityType, Long entityId);

    ImageResponse getPrimaryImage(String entityType, Long entityId);

    // ========== DELETE METHODS ==========

    void deleteImage(Long imageId);

    void deleteAllImages(String entityType, Long entityId);

    // ========== UPDATE METHODS ==========

    void setPrimaryImage(String entityType, Long entityId, Long imageId);

    // ========== HELPER METHODS ==========

    String getDefaultImage(String entityType);
}