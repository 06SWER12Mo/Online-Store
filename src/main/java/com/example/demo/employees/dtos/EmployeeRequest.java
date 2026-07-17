package com.example.demo.employees.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Passport number is required")
    @Size(min = 5, max = 50, message = "Passport number must be between 5 and 50 characters")
    private String passportNumber;

    @NotBlank(message = "Phone number is required")
    @Size(min = 8, max = 20, message = "Phone number must be between 8 and 20 characters")
    @Pattern(regexp = "^[0-9+\\-() ]+$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    @NotBlank(message = "Role is required")
    @Size(max = 50, message = "Role must not exceed 50 characters")
    private String role;

    private Boolean isActive = true;

    @Min(value = 1, message = "Days of work must be at least 1")
    @Max(value = 7, message = "Days of work cannot exceed 7")
    private Integer daysOfWork;

    @Min(value = 1, message = "Hours of work must be at least 1")
    @Max(value = 168, message = "Hours of work cannot exceed 168")
    private Double hoursOfWork;

    @PastOrPresent(message = "Employee since cannot be in the future")
    private LocalDate employeeSince;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 50, message = "Emergency contact must not exceed 50 characters")
    private String emergencyContact;

    @Size(max = 20, message = "Emergency phone must not exceed 20 characters")
    private String emergencyPhone;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    // Getters and Setters
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
}