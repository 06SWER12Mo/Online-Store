package com.example.demo.shipping.dtos;

import com.example.demo.shipping.Bus;

public class BusResponse {
    
    private Long id;
    private String plateNumber;
    private String driverName;
    private Integer capacity;
    private Boolean isActive;
    private Long bigAreaId;
    private String bigAreaName;
    private Boolean isAssigned;
    private Long assignedBatchId;
    private String assignedBatchStatus;
    
    public BusResponse() {}
    
    public BusResponse(Bus bus) {
        this.id = bus.getId();
        this.plateNumber = bus.getPlateNumber();
        this.driverName = bus.getDriverName();
        this.capacity = bus.getCapacity();
        this.isActive = bus.getIsActive();
        if (bus.getBigArea() != null) {
            this.bigAreaId = bus.getBigArea().getId();
            this.bigAreaName = bus.getBigArea().getName();
        }
        this.isAssigned = false;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Long getBigAreaId() { return bigAreaId; }
    public void setBigAreaId(Long bigAreaId) { this.bigAreaId = bigAreaId; }
    
    public String getBigAreaName() { return bigAreaName; }
    public void setBigAreaName(String bigAreaName) { this.bigAreaName = bigAreaName; }
    
    public Boolean getIsAssigned() { return isAssigned; }
    public void setIsAssigned(Boolean isAssigned) { this.isAssigned = isAssigned; }
    
    public Long getAssignedBatchId() { return assignedBatchId; }
    public void setAssignedBatchId(Long assignedBatchId) { this.assignedBatchId = assignedBatchId; }
    
    public String getAssignedBatchStatus() { return assignedBatchStatus; }
    public void setAssignedBatchStatus(String assignedBatchStatus) { this.assignedBatchStatus = assignedBatchStatus; }
}