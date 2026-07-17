package com.example.demo.employees;

import com.example.demo.employees.dtos.EmployeeRequest;
import com.example.demo.employees.dtos.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, 
                               EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByPassportNumber(request.getPassportNumber())) {
            throw new RuntimeException("Employee with passport number '" + 
                request.getPassportNumber() + "' already exists");
        }

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Employee with email '" + 
                request.getEmail() + "' already exists");
        }

        if (employeeRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Employee with phone number '" + 
                request.getPhoneNumber() + "' already exists");
        }

        Employee employee = employeeMapper.toEntity(request);
        
        if (request.getEmployeeSince() == null) {
            employee.setEmployeeSince(LocalDate.now());
        }
        if (request.getIsActive() == null) {
            employee.setActive(true);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeById(id);

        if (!employee.getPassportNumber().equals(request.getPassportNumber()) &&
            employeeRepository.existsByPassportNumberAndIdNot(request.getPassportNumber(), id)) {
            throw new RuntimeException("Employee with passport number '" + 
                request.getPassportNumber() + "' already exists");
        }

        if (!employee.getEmail().equals(request.getEmail()) &&
            employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new RuntimeException("Employee with email '" + 
                request.getEmail() + "' already exists");
        }

        if (!employee.getPhoneNumber().equals(request.getPhoneNumber()) &&
            employeeRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), id)) {
            throw new RuntimeException("Employee with phone number '" + 
                request.getPhoneNumber() + "' already exists");
        }

        employeeMapper.updateEntity(employee, request);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeById(id);
        employeeRepository.delete(employee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findEmployeeById(id);
        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeByPassportNumber(String passportNumber) {
        Employee employee = employeeRepository.findByPassportNumber(passportNumber)
            .orElseThrow(() -> new RuntimeException("Employee not found with passport number: " + passportNumber));
        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found with email: " + email));
        return employeeMapper.toResponse(employee);
    }

    @Override
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable)
            .map(employeeMapper::toResponse);
    }

    @Override
    public Page<EmployeeResponse> getActiveEmployees(Pageable pageable) {
        return employeeRepository.findByIsActiveTrue(pageable)
            .map(employeeMapper::toResponse);
    }

    @Override
    public Page<EmployeeResponse> getInactiveEmployees(Pageable pageable) {
        return employeeRepository.findByIsActiveFalse(pageable)
            .map(employeeMapper::toResponse);
    }

    @Override
    public List<EmployeeResponse> getEmployeesByRole(String role) {
        return employeeRepository.findByRole(role).stream()
            .map(employeeMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeResponse> getEmployeesByRole(String role, Pageable pageable) {
        return employeeRepository.findByRole(role, pageable)
            .map(employeeMapper::toResponse);
    }

    @Override
    public List<EmployeeResponse> getActiveEmployeesByRole(String role) {
        return employeeRepository.findByRoleAndIsActiveTrue(role).stream()
            .map(employeeMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void toggleEmployeeActive(Long id) {
        Employee employee = findEmployeeById(id);
        employee.setActive(!employee.isActive());
        employeeRepository.save(employee);
    }

    // ========== SEARCH METHODS ==========

    @Override
    public Page<EmployeeResponse> searchEmployeesByName(String name, Pageable pageable) {
        return employeeRepository.findByNameContainingIgnoreCase(name, pageable)
            .map(employeeMapper::toResponse);
    }

    @Override
    public Page<EmployeeResponse> searchEmployeesByRole(String role, Pageable pageable) {
        return employeeRepository.findByRoleIgnoreCase(role, pageable)
            .map(employeeMapper::toResponse);
    }

    @Override
    public Page<EmployeeResponse> searchEmployeesByNameAndRole(String name, String role, Pageable pageable) {
        // ✅ FIXED: Use repository method that handles both name and role
        if (name != null && !name.trim().isEmpty() && role != null && !role.trim().isEmpty()) {
            // Both name and role provided
            return employeeRepository.findByNameContainingIgnoreCaseAndRoleIgnoreCase(name, role, pageable)
                .map(employeeMapper::toResponse);
        } else if (name != null && !name.trim().isEmpty()) {
            // Only name provided
            return searchEmployeesByName(name, pageable);
        } else if (role != null && !role.trim().isEmpty()) {
            // Only role provided
            return searchEmployeesByRole(role, pageable);
        } else {
            // Neither provided - return all
            return getAllEmployees(pageable);
        }
    }

    @Override
    public Page<EmployeeResponse> searchEmployees(String keyword, Pageable pageable) {
        return employeeRepository.searchEmployees(keyword, pageable)
            .map(employeeMapper::toResponse);
    }

    @Override
    public List<EmployeeResponse> searchEmployees(String keyword) {
        return employeeRepository.searchEmployees(keyword).stream()
            .map(employeeMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Long countTotalEmployees() {
        return employeeRepository.count();
    }

    @Override
    public Long countActiveEmployees() {
        return employeeRepository.countActiveEmployees();
    }

    @Override
    public Long countEmployeesByRole(String role) {
        return employeeRepository.countByRole(role);
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
}