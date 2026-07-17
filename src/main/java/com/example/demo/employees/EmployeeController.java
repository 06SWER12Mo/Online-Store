package com.example.demo.employees;

import com.example.demo.common.dtos.ApiResponse;
import com.example.demo.employees.dtos.EmployeeRequest;
import com.example.demo.employees.dtos.EmployeeResponse;
import com.example.demo.employees.dtos.EmployeeStatsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Management", description = "Endpoints for managing employees. All endpoints require ADMIN or MANAGER role.")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ========== CREATE ==========

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new employee", description = "Creates a new employee record. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Employee created successfully",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or duplicate data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Employee created successfully", response));
    }

    // ========== UPDATE ==========

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update an employee", description = "Updates an existing employee record. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee updated successfully",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or duplicate data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }

    // ========== DELETE ==========

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete an employee", description = "Permanently deletes an employee record. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // ========== TOGGLE ACTIVE ==========

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Toggle employee active status", description = "Activates or deactivates an employee. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee status toggled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<ApiResponse<Void>> toggleEmployeeActive(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id) {
        employeeService.toggleEmployeeActive(id);
        return ResponseEntity.ok(ApiResponse.success("Employee status toggled successfully"));
    }

    // ========== GET BY ID ==========

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employee by ID", description = "Retrieves a specific employee by their ID. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee retrieved successfully",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== GET BY PASSPORT ==========

    @GetMapping("/passport/{passportNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employee by passport number", description = "Retrieves an employee by their passport number. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee retrieved successfully",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Employee not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByPassportNumber(
            @Parameter(description = "Passport number", required = true, example = "AB123456")
            @PathVariable String passportNumber) {
        EmployeeResponse response = employeeService.getEmployeeByPassportNumber(passportNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== GET BY EMAIL ==========

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employee by email", description = "Retrieves an employee by their email address. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee retrieved successfully",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Employee not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmail(
            @Parameter(description = "Email address", required = true, example = "john.doe@example.com")
            @PathVariable String email) {
        EmployeeResponse response = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== GET ALL EMPLOYEES (Paginated) ==========

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all employees", description = "Retrieves a paginated list of all employees. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== GET ACTIVE EMPLOYEES ==========

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get active employees", description = "Retrieves a paginated list of active employees. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active employees retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getActiveEmployees(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.getActiveEmployees(pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== GET INACTIVE EMPLOYEES ==========

    @GetMapping("/inactive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get inactive employees", description = "Retrieves a paginated list of inactive employees. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inactive employees retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getInactiveEmployees(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.getInactiveEmployees(pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== SEARCH BY NAME ==========

    @GetMapping("/search/name")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search employees by name", description = "Searches employees by full name (partial match). Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> searchEmployeesByName(
            @Parameter(description = "Name to search for", required = true, example = "John")
            @RequestParam String name,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.searchEmployeesByName(name, pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== SEARCH BY ROLE ==========

    @GetMapping("/search/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search employees by role", description = "Searches employees by exact role match. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> searchEmployeesByRole(
            @Parameter(description = "Role to search for", required = true, example = "MANAGER")
            @RequestParam String role,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.searchEmployeesByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== SEARCH BY NAME AND ROLE ==========

    @GetMapping("/search/name-role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search employees by name and role", description = "Searches employees by name (partial) and role (exact). Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> searchEmployeesByNameAndRole(
            @Parameter(description = "Name to search for (partial match)", example = "John")
            @RequestParam(required = false) String name,
            @Parameter(description = "Role to search for (exact match)", example = "MANAGER")
            @RequestParam(required = false) String role,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.searchEmployeesByNameAndRole(name, role, pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== GET EMPLOYEES BY ROLE (Non-paginated) ==========

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employees by role (list)", description = "Retrieves all employees with a specific role as a list. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByRoleList(
            @Parameter(description = "Role name", required = true, example = "MANAGER")
            @PathVariable String role) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByRole(role);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== GET EMPLOYEES BY ROLE (Paginated) ==========

    @GetMapping("/role/{role}/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employees by role (paginated)", description = "Retrieves a paginated list of employees with a specific role. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getEmployeesByRolePaginated(
            @Parameter(description = "Role name", required = true, example = "MANAGER")
            @PathVariable String role,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.getEmployeesByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== GET ACTIVE EMPLOYEES BY ROLE ==========

    @GetMapping("/role/{role}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get active employees by role", description = "Retrieves active employees with a specific role. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active employees retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getActiveEmployeesByRole(
            @Parameter(description = "Role name", required = true, example = "MANAGER")
            @PathVariable String role) {
        List<EmployeeResponse> employees = employeeService.getActiveEmployeesByRole(role);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== GENERAL SEARCH (All Fields) ==========

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search employees (all fields)", description = "Searches employees by name, passport number, email, or phone number. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> searchEmployees(
            @Parameter(description = "Search keyword", required = true, example = "John")
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.searchEmployees(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    // ========== STATISTICS ==========

    @GetMapping("/stats/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employee counts", description = "Retrieves total, active, and role-based employee counts. Requires ADMIN or MANAGER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Counts retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponse<EmployeeStatsResponse>> getEmployeeStats() {
        EmployeeStatsResponse stats = new EmployeeStatsResponse();
        stats.setTotalEmployees(employeeService.countTotalEmployees());
        stats.setActiveEmployees(employeeService.countActiveEmployees());
        stats.setInactiveEmployees(employeeService.countTotalEmployees() - employeeService.countActiveEmployees());
        
        stats.setAdminCount(employeeService.countEmployeesByRole("ADMIN"));
        stats.setManagerCount(employeeService.countEmployeesByRole("MANAGER"));
        stats.setEmployeeCount(employeeService.countEmployeesByRole("EMPLOYEE"));
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}