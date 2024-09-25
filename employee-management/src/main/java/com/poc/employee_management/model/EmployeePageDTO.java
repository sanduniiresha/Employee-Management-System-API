package com.poc.employee_management.model;

import lombok.*;

import java.util.List;

/**
 * Employee page model
 */
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePageDTO {

    private List<EmployeeDTO> employees;

}
