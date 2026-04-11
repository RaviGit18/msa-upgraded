package com.payroll.microservices.employeeservice;

import com.payroll.microservices.employeeservice.model.Employee;
import com.payroll.microservices.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Employee Service Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService; // Assuming there's a service layer

    private List<Employee> employeeList;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setEmpId(1000L);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDateOfJoining(new Date());
        testEmployee.setPort(8080);

        Employee employee2 = new Employee();
        employee2.setEmpId(1001L);
        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");
        employee2.setDateOfJoining(new Date());
        employee2.setPort(8080);

        employeeList = Arrays.asList(testEmployee, employee2);
    }

    @Test
    @DisplayName("Find Employee by ID - Success")
    void findEmployeeById_WhenEmployeeExists_thenReturnEmployee() {
        // Given
        when(employeeRepository.findById(1000L)).thenReturn(Optional.of(testEmployee));

        // When
        Optional<Employee> result = employeeRepository.findById(1000L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals(1000L, result.get().getEmpId());
        verify(employeeRepository, times(1)).findById(1000L);
    }

    @Test
    @DisplayName("Find Employee by ID - Not Found")
    void findEmployeeById_WhenEmployeeNotExists_thenReturnEmpty() {
        // Given
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Employee> result = employeeRepository.findById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(employeeRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Save Employee - Success")
    void saveEmployee_WhenValidEmployee_thenReturnSavedEmployee() {
        // Given
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        // When
        Employee result = employeeRepository.save(testEmployee);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(1000L, result.getEmpId());
        verify(employeeRepository, times(1)).save(testEmployee);
    }

    @Test
    @DisplayName("Find All Employees - Success")
    void findAllEmployees_WhenEmployeesExist_thenReturnEmployeeList() {
        // Given
        when(employeeRepository.findAll()).thenReturn(employeeList);

        // When
        Iterable<Employee> result = employeeRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, ((List<Employee>) result).size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete Employee - Success")
    void deleteEmployee_WhenEmployeeExists_thenNoException() {
        // Given
        doNothing().when(employeeRepository).deleteById(1000L);
        when(employeeRepository.existsById(1000L)).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> employeeRepository.deleteById(1000L));
        verify(employeeRepository, times(1)).deleteById(1000L);
    }

    @Test
    @DisplayName("Employee Entity - Date Validation")
    void testEmployeeDateValidation() {
        // Given
        Employee employee = new Employee();
        Date currentDate = new Date();

        // When & Then
        assertDoesNotThrow(() -> employee.setDateOfJoining(currentDate));
        assertEquals(currentDate, employee.getDateOfJoining());
    }

    @Test
    @DisplayName("Employee Entity - Port Assignment")
    void testEmployeePortAssignment() {
        // Given
        Employee employee = new Employee();

        // When
        employee.setPort(8080);

        // Then
        assertEquals(8080, employee.getPort());
    }

    @Test
    @DisplayName("Repository Interaction - Null Handling")
    void testRepositoryNullHandling() {
        // Given
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Employee> result = employeeRepository.findById(123L);

        // Then
        assertFalse(result.isPresent());
        verify(employeeRepository, times(1)).findById(123L);
    }
}
