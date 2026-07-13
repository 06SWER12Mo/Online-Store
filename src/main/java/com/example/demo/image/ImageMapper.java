package com.example.demo.image;

import org.springframework.stereotype.Component;

import com.example.demo.image.dtos.ImageResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageMapper {

    public ImageResponse toResponse(ImageEntity entity) {
        if (entity == null) return null;

        return new ImageResponse(
            entity.getId(),
            entity.getImageUrl(),
            entity.getFileName(),
            entity.getFileType(),
            entity.getFileSize(),
            entity.getEntityType(),
            entity.getEntityId(),
            entity.getImageType(),
            entity.getDisplayOrder(),
            entity.getAltText(),
            entity.isPrimary(),
            entity.getCreatedAt()
        );
    }

    public List<ImageResponse> toResponseList(List<ImageEntity> entities) {
        return entities.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public ImageEntity toEntity(String imageUrl, String fileName, String fileType, long fileSize,
                                String entityType, Long entityId, String imageType,
                                Integer displayOrder, String altText, boolean primary) {
        ImageEntity entity = new ImageEntity();
        entity.setImageUrl(imageUrl);
        entity.setFileName(fileName);
        entity.setFileType(fileType);
        entity.setFileSize(fileSize);
        entity.setEntityType(entityType);
        entity.setEntityId(entityId);
        entity.setImageType(imageType);
        entity.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        entity.setAltText(altText);
        entity.setPrimary(primary);
        return entity;
    }
}