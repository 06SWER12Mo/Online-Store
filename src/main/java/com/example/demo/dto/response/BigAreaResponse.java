package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.BigArea;

public class BigAreaResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private Integer displayOrder;
    private Integer townCount;
    private List<TownResponse> towns;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public BigAreaResponse() {}

    // Lightweight constructor used for list endpoints (avoids loading lazy towns/addresses)
    public BigAreaResponse(Long id, String name, String code, String description,
                            boolean active, Integer displayOrder,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.active = active;
        this.displayOrder = displayOrder;
        this.townCount = 0;
        this.towns = new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Full constructor for detail views (includes towns)
    public BigAreaResponse(BigArea bigArea) {
        this.id = bigArea.getId();
        this.name = bigArea.getName();
        this.code = bigArea.getCode();
        this.description = bigArea.getDescription();
        this.active = bigArea.isActive();
        this.displayOrder = bigArea.getDisplayOrder();
        this.createdAt = bigArea.getCreatedAt();
        this.updatedAt = bigArea.getUpdatedAt();
        this.townCount = 0;
        this.towns = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getTownCount() {
        return townCount;
    }

    public void setTownCount(Integer townCount) {
        this.townCount = townCount;
    }

    public List<TownResponse> getTowns() {
        return towns;
    }

    public void setTowns(List<TownResponse> towns) {
        this.towns = towns;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}