package com.example.demo.employees;

import com.example.demo.employees.dtos.EmployeeRequest;
import com.example.demo.employees.dtos.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    void deleteEmployee(Long id);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse getEmployeeByPassportNumber(String passportNumber);

    EmployeeResponse getEmployeeByEmail(String email);

    Page<EmployeeResponse> getAllEmployees(Pageable pageable);

    Page<EmployeeResponse> getActiveEmployees(Pageable pageable);

    Page<EmployeeResponse> getInactiveEmployees(Pageable pageable);

    List<EmployeeResponse> getEmployeesByRole(String role);

    Page<EmployeeResponse> getEmployeesByRole(String role, Pageable pageable);

    List<EmployeeResponse> getActiveEmployeesByRole(String role);

    void toggleEmployeeActive(Long id);

    Page<EmployeeResponse> searchEmployeesByName(String name, Pageable pageable);

    Page<EmployeeResponse> searchEmployeesByRole(String role, Pageable pageable);

    Page<EmployeeResponse> searchEmployeesByNameAndRole(String name, String role, Pageable pageable);

    Page<EmployeeResponse> searchEmployees(String keyword, Pageable pageable);

    List<EmployeeResponse> searchEmployees(String keyword);

    Long countTotalEmployees();

    Long countActiveEmployees();

    Long countEmployeesByRole(String role);
}