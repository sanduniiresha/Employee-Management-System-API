package com.poc.employee_management.service;

import com.poc.employee_management.model.EmployeeDTO;
import com.poc.employee_management.model.EmployeePageDTO;
import org.springframework.data.domain.Page;

/**
 * EmployeeService interface for managing employee-related operations
 */
public interface EmployeeService {

    void saveEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO getEmployeeByEmployeeId(Long id);

    EmployeeDTO updateEmployee(EmployeeDTO updatedEmployeeRequest, Long id);

    Page<EmployeePageDTO> getEmployees(int page, int size);

    void deleteEmployee(Long id);

}
