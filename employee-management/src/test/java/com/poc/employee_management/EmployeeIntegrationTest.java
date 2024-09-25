package com.poc.employee_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.employee_management.exception.EmployeeNotFoundException;
import com.poc.employee_management.model.EmployeeDTO;
import com.poc.employee_management.model.EmployeePageDTO;
import com.poc.employee_management.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the EmployeeController, verifying the functionality
 * of employee-related endpoints, including creation, retrieval, update,
 * and deletion of employee records.
 */
@WebMvcTest
class EmployeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateEmployee() throws Exception {

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName("Menara");
        employeeDTO.setLastName("Maybank");
        employeeDTO.setEmail("maybank@gmail.com");
        employeeDTO.setDepartment("IT");
        employeeDTO.setSalary(50000.0);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Created Employee Successfully"));
    }

    @Test
    void testGetEmployeeById() throws Exception {

        Long employeeId = 1L;
        EmployeeDTO employeeDTO = new EmployeeDTO(
                "Menara", "Mayabank", "maybank@gmail.com", "IT", 50000.0);

        Mockito.when(employeeService.getEmployeeByEmployeeId(employeeId)).thenReturn(employeeDTO);

        mockMvc.perform(get("/api/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Menara"))
                .andExpect(jsonPath("$.lastName").value("Mayabank"))
                .andExpect(jsonPath("$.email").value("maybank@gmail.com"));
    }

    @Test
    void testGetEmployeeByIdNotFound() throws Exception {

        Long invalidEmployeeId = 999L;

        Mockito.when(employeeService.getEmployeeByEmployeeId(invalidEmployeeId))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/{id}", invalidEmployeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }



    @Test
    void testUpdateEmployee() throws Exception {

        Long employeeId = 1L;
        EmployeeDTO updatedEmployee = new EmployeeDTO(
                "Menara", "Maybank", "maybank@.com", "IT", 60000.0);

        Mockito.when(employeeService.updateEmployee(Mockito.any(EmployeeDTO.class), Mockito.eq(employeeId)))
                .thenReturn(updatedEmployee);

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salary").value(60000.0));
    }

    @Test
    void testUpdateEmployeeNotFound() throws Exception {

        Long invalidEmployeeId = 999L;
        String employeeJson = """
        {
            "firstName": "Menara",
            "lastName": "Maybank",
            "email": "maybank@gmail.com",
            "department": "IT",
            "salary": 60000.0
        }
    """;

        Mockito.when(employeeService.updateEmployee(Mockito.any(), Mockito.eq(invalidEmployeeId)))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(put("/api/employees/{id}", invalidEmployeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }


    @Test
    void testGetEmployees() throws Exception {

        EmployeeDTO employeeDTO = new EmployeeDTO(
                "Menara", "Maybank", "maybank@gmail.com", "IT", 50000.0);
        EmployeePageDTO employeePageDTO = new EmployeePageDTO(Collections.singletonList(employeeDTO));

        Pageable pageable = PageRequest.of(1, 10);
        Page<EmployeePageDTO> page = new PageImpl<>(Collections.singletonList(employeePageDTO), pageable, 1);

        Mockito.when(employeeService.getEmployees(1, 10)).thenReturn(page);

        mockMvc.perform(get("/api/employees?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].employees[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].employees[0].lastName").value("Doe"));
    }

    @Test
    void testDeleteEmployee() throws Exception {

        Long employeeId = 1L;
        Mockito.doNothing().when(employeeService).deleteEmployee(employeeId);

        mockMvc.perform(delete("/api/employees/{id}", employeeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEmployeeNotFound() throws Exception {

        Long invalidEmployeeId = 999L;

        Mockito.doThrow(new EmployeeNotFoundException("Employee not found"))
                .when(employeeService).deleteEmployee(invalidEmployeeId);

        mockMvc.perform(delete("/api/employees/{id}", invalidEmployeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Ensure 404 status code
                .andExpect(jsonPath("$.message").value("Employee not found")); // Ensure the error message is returned
    }

}
