package com.example.demo.image.dtos;

import java.time.LocalDateTime;

public class ImageResponse {

    private Long id;
    private String imageUrl;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String entityType;
    private Long entityId;
    private String imageType;
    private Integer displayOrder;
    private String altText;
    private boolean primary;
    private LocalDateTime createdAt;

    // Constructors
    public ImageResponse() {}

    public ImageResponse(Long id, String imageUrl, String fileName, String fileType,
                         long fileSize, String entityType, Long entityId,
                         String imageType, Integer displayOrder, String altText,
                         boolean primary, LocalDateTime createdAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.entityType = entityType;
        this.entityId = entityId;
        this.imageType = imageType;
        this.displayOrder = displayOrder;
        this.altText = altText;
        this.primary = primary;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}