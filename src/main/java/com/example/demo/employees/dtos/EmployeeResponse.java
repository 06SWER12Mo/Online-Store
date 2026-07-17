package com.example.demo.employees.dtos;

import com.example.demo.employees.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponse {

    private Long id;
    private String name;
    private String passportNumber;
    private String phoneNumber;
    private String email;
    private BigDecimal salary;
    private String role;
    private boolean isActive;
    private Integer daysOfWork;
    private Double hoursOfWork;
    private LocalDate employeeSince;
    private LocalDate dateOfBirth;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeResponse() {}

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.name = employee.getName();
        this.passportNumber = employee.getPassportNumber();
        this.phoneNumber = employee.getPhoneNumber();
        this.email = employee.getEmail();
        this.salary = employee.getSalary();
        this.role = employee.getRole();
        this.isActive = employee.isActive();
        this.daysOfWork = employee.getDaysOfWork();
        this.hoursOfWork = employee.getHoursOfWork();
        this.employeeSince = employee.getEmployeeSince();
        this.dateOfBirth = employee.getDateOfBirth();
        this.address = employee.getAddress();
        this.emergencyContact = employee.getEmergencyContact();
        this.emergencyPhone = employee.getEmergencyPhone();
        this.notes = employee.getNotes();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
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

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getDaysOfWork() {
        return daysOfWork;
    }

    public void setDaysOfWork(Integer daysOfWork) {
        this.daysOfWork = daysOfWork;
    }

    public Double getHoursOfWork() {
        return hoursOfWork;
    }

    public void setHoursOfWork(Double hoursOfWork) {
        this.hoursOfWork = hoursOfWork;
    }

    public LocalDate getEmployeeSince() {
        return employeeSince;
    }

    public void setEmployeeSince(LocalDate employeeSince) {
        this.employeeSince = employeeSince;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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