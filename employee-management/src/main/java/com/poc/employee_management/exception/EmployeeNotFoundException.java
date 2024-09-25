package com.poc.employee_management.exception;

/**
 * Employee not found exception
 */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }

}
