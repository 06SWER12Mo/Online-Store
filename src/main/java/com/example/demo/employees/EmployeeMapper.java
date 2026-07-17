package com.example.demo.employees;

import com.example.demo.employees.dtos.EmployeeRequest;
import com.example.demo.employees.dtos.EmployeeResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setPassportNumber(request.getPassportNumber());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setEmail(request.getEmail());
        employee.setSalary(request.getSalary());
        employee.setRole(request.getRole());
        employee.setActive(request.getIsActive() != null ? request.getIsActive() : true);
        employee.setDaysOfWork(request.getDaysOfWork());
        employee.setHoursOfWork(request.getHoursOfWork());
        employee.setEmployeeSince(request.getEmployeeSince());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setAddress(request.getAddress());
        employee.setEmergencyContact(request.getEmergencyContact());
        employee.setEmergencyPhone(request.getEmergencyPhone());
        employee.setNotes(request.getNotes());
        return employee;
    }

    public void updateEntity(Employee employee, EmployeeRequest request) {
        if (request.getName() != null) {
            employee.setName(request.getName());
        }
        if (request.getPassportNumber() != null) {
            employee.setPassportNumber(request.getPassportNumber());
        }
        if (request.getPhoneNumber() != null) {
            employee.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            employee.setEmail(request.getEmail());
        }
        if (request.getSalary() != null) {
            employee.setSalary(request.getSalary());
        }
        if (request.getRole() != null) {
            employee.setRole(request.getRole());
        }
        if (request.getIsActive() != null) {
            employee.setActive(request.getIsActive());
        }
        if (request.getDaysOfWork() != null) {
            employee.setDaysOfWork(request.getDaysOfWork());
        }
        if (request.getHoursOfWork() != null) {
            employee.setHoursOfWork(request.getHoursOfWork());
        }
        if (request.getEmployeeSince() != null) {
            employee.setEmployeeSince(request.getEmployeeSince());
        }
        if (request.getDateOfBirth() != null) {
            employee.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAddress() != null) {
            employee.setAddress(request.getAddress());
        }
        if (request.getEmergencyContact() != null) {
            employee.setEmergencyContact(request.getEmergencyContact());
        }
        if (request.getEmergencyPhone() != null) {
            employee.setEmergencyPhone(request.getEmergencyPhone());
        }
        if (request.getNotes() != null) {
            employee.setNotes(request.getNotes());
        }
    }

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(employee);
    }

    public List<EmployeeResponse> toResponseList(List<Employee> employees) {
        return employees.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}