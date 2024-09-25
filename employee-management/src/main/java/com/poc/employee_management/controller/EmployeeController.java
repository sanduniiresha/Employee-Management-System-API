package com.poc.employee_management.controller;

import com.poc.employee_management.model.EmployeeDTO;
import com.poc.employee_management.model.EmployeePageDTO;
import com.poc.employee_management.service.impl.EmployeeServiceImpl;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * EmployeeController provides method for Employee related operations
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeService;

    /**
     * Create Employee for given details
     * @param employeeDTO employeeDTO with new employee details
     * @return newly created employee message
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createEmployee(@Validated @RequestBody(required = true) EmployeeDTO employeeDTO) {
        employeeService.saveEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created Employee Successfully");
    }

    /**
     * Retrieve an Employee data
     * @param id employee_id
     * @return employee data
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDTO> getEmployeeByEmployeeId(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.getEmployeeByEmployeeId(id);
        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }

    /**
     * Update employee details for given employee_id
     * @param id employee_id
     * @param updatedEmployeeRequest employeeDTO with updated values
     * @return UpdatedEmployeeDTO
     */
    @PutMapping("/{id}")
    ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long id,
            @RequestBody @Validated EmployeeDTO updatedEmployeeRequest) {
        EmployeeDTO updatedEmployee = employeeService.updateEmployee(updatedEmployeeRequest, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedEmployee);
    }

    /**
     * Retrieves a paginated list of employees.
     *
     * @param page the page number, default is 1.
     * @param size the number of employees per page, default is 10, max is 100.
     * @return ResponseEntity with the employee page or no content if empty.
     */
    @GetMapping
    public ResponseEntity<Page<EmployeePageDTO>> getEmployees(
            @RequestParam(defaultValue = "1") @Positive int page,
            @RequestParam(defaultValue = "10") @Positive @Max(value = 100) int size) {
        Page<EmployeePageDTO> employeePage = employeeService.getEmployees(page, size);
        if (employeePage.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(employeePage);
        }
    }

    /**
     * Delete employee by given employee_id
     * @param id employee_id
     * @return ResponseEntity status
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
