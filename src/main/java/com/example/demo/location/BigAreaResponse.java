package com.example.demo.location;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public BigAreaResponse(BigArea bigArea) {
        this.id = bigArea.getId();
        this.name = bigArea.getName();
        this.code = bigArea.getCode();
        this.description = bigArea.getDescription();
        this.active = bigArea.isActive();
        this.displayOrder = bigArea.getDisplayOrder();
        this.townCount = bigArea.getTowns().size();
        this.createdAt = bigArea.getCreatedAt();
        this.updatedAt = bigArea.getUpdatedAt();
        this.towns = bigArea.getTowns().stream()
                .map(TownResponse::new)
                .collect(Collectors.toList());
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