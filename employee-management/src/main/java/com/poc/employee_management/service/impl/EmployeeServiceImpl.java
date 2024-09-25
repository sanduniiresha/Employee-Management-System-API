package com.poc.employee_management.service.impl;

import com.poc.employee_management.entity.Employee;
import com.poc.employee_management.exception.EmployeeNotFoundException;
import com.poc.employee_management.model.EmployeeDTO;
import com.poc.employee_management.model.EmployeePageDTO;
import com.poc.employee_management.repository.EmployeeRepository;
import com.poc.employee_management.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * EmployeeServiceImpl class handling business logic for Employee operations,
 * including saving, updating, retrieving, and deleting employees.
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    ModelMapper modelMapper;

    public void saveEmployee(EmployeeDTO employeeDTO) {

        Employee existingEmployee = employeeRepository.findByEmail(employeeDTO.getEmail());

        if (existingEmployee != null) {
            throw new IllegalArgumentException("Employee already exists");
        }
        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        employeeRepository.save(employee);
    }

    public EmployeeDTO getEmployeeByEmployeeId(Long id) {

        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return modelMapper.map(employee.get(), EmployeeDTO.class);
        } else {
            throw new EmployeeNotFoundException("Employee not found");
        }
    }

    public EmployeeDTO updateEmployee(EmployeeDTO updatedEmployeeRequest, Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        modelMapper.map(updatedEmployeeRequest, employee);
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    public Page<EmployeePageDTO> getEmployees(int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        Page<EmployeePageDTO> employeePageDTO = employeePage.map(employee -> {
            List<EmployeeDTO> employeeDTOs = List.of(modelMapper.map(employee, EmployeeDTO.class));
            return new EmployeePageDTO(employeeDTOs);
        });

        return employeePageDTO;
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

}
