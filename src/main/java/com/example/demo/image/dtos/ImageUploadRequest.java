package com.example.demo.image.dtos;

import org.springframework.web.multipart.MultipartFile;

public class ImageUploadRequest {

    private String entityType;  // PRODUCT, VARIANT, CATEGORY, SUBCATEGORY, USER
    private Long entityId;
    private MultipartFile file;
    private String imageType;   // MAIN, GALLERY, THUMBNAIL, AVATAR, VARIANT
    private Integer displayOrder;
    private String altText;

    // Constructors
    public ImageUploadRequest() {}

    // Getters and Setters
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }
}