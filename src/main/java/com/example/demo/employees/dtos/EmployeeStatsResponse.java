package com.example.demo.employees.dtos;

public class EmployeeStatsResponse {

    private Long totalEmployees;
    private Long activeEmployees;
    private Long inactiveEmployees;
    private Long adminCount;
    private Long managerCount;
    private Long employeeCount;

    // Getters and Setters
    public Long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(Long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public Long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(Long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public Long getInactiveEmployees() {
        return inactiveEmployees;
    }

    public void setInactiveEmployees(Long inactiveEmployees) {
        this.inactiveEmployees = inactiveEmployees;
    }

    public Long getAdminCount() {
        return adminCount;
    }

    public void setAdminCount(Long adminCount) {
        this.adminCount = adminCount;
    }

    public Long getManagerCount() {
        return managerCount;
    }

    public void setManagerCount(Long managerCount) {
        this.managerCount = managerCount;
    }

    public Long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Long employeeCount) {
        this.employeeCount = employeeCount;
    }
}