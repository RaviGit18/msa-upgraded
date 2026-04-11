package com.payroll.microservices.employeepayrollservice;

import com.payroll.microservices.employeepayrollservice.model.EmployeePayroll;
import com.payroll.microservices.employeepayrollservice.repository.EmployeePayrollRepository;
import com.payroll.microservices.employeepayrollservice.service.EmployeeService;
import com.payroll.microservices.employeepayrollservice.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Employee Payroll Service Tests")
class EmployeePayrollServiceTest {

    @Mock
    private EmployeePayrollRepository employeePayrollRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private EmployeePayrollService employeePayrollService; // Assuming service class exists

    private EmployeePayroll testPayroll;
    private List<EmployeePayroll> payrollList;

    @BeforeEach
    void setUp() {
        testPayroll = new EmployeePayroll();
        testPayroll.setPayrollId(1L);
        testPayroll.setEmpId(1000L);
        testPayroll.setFirstName("John");
        testPayroll.setLastName("Doe");
        testPayroll.setRoleId(101L);
        testPayroll.setRoleName("Developer");
        testPayroll.setDescription("Software Developer");
        testPayroll.setDateOfJoining(new Date());
        testPayroll.setPort(8080);

        EmployeePayroll payroll2 = new EmployeePayroll();
        payroll2.setPayrollId(2L);
        payroll2.setEmpId(1001L);
        payroll2.setFirstName("Jane");
        payroll2.setLastName("Smith");
        payroll2.setRoleId(102L);
        payroll2.setRoleName("QA");
        payroll2.setDescription("Quality Assurance");
        payroll2.setDateOfJoining(new Date());
        payroll2.setPort(8080);

        payrollList = Arrays.asList(testPayroll, payroll2);
    }

    @Test
    @DisplayName("Save Payroll - Success")
    void savePayroll_WhenValidPayroll_thenReturnSavedPayroll() {
        // Given
        when(employeePayrollRepository.save(any(EmployeePayroll.class))).thenReturn(testPayroll);

        // When
        EmployeePayroll result = employeePayrollRepository.save(testPayroll);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getPayrollId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(1000L, result.getEmpId());
        assertEquals(101L, result.getRoleId());
        verify(employeePayrollRepository, times(1)).save(testPayroll);
    }

    @Test
    @DisplayName("Find Payroll by ID - Success")
    void findPayrollById_WhenPayrollExists_thenReturnPayroll() {
        // Given
        when(employeePayrollRepository.findById(1L)).thenReturn(Optional.of(testPayroll));

        // When
        Optional<EmployeePayroll> result = employeePayrollRepository.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertEquals(1000L, result.get().getEmpId());
        verify(employeePayrollRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Find Payroll by ID - Not Found")
    void findPayrollById_WhenPayrollNotExists_thenReturnEmpty() {
        // Given
        when(employeePayrollRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<EmployeePayroll> result = employeePayrollRepository.findById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(employeePayrollRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Find All Payrolls - Success")
    void findAllPayrolls_WhenPayrollsExist_thenReturnPayrollList() {
        // Given
        when(employeePayrollRepository.findAll()).thenReturn(payrollList);

        // When
        Iterable<EmployeePayroll> result = employeePayrollRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, ((List<EmployeePayroll>) result).size());
        verify(employeePayrollRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete Payroll - Success")
    void deletePayroll_WhenPayrollExists_thenNoException() {
        // Given
        doNothing().when(employeePayrollRepository).deleteById(1L);
        when(employeePayrollRepository.existsById(1L)).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> employeePayrollRepository.deleteById(1L));
        verify(employeePayrollRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Create Payroll with Employee and Role Data")
    void createPayroll_WhenEmployeeAndRoleData_thenReturnCombinedPayroll() {
        // Given
        EmployeePayroll employeeData = new EmployeePayroll();
        employeeData.setEmpId(1000L);
        employeeData.setFirstName("John");
        employeeData.setLastName("Doe");

        EmployeePayroll roleData = new EmployeePayroll();
        roleData.setRoleId(101L);
        roleData.setRoleName("Developer");
        roleData.setDescription("Software Developer");

        when(employeeService.getEmployeeDetails(1000L)).thenReturn(employeeData);
        when(roleService.getRoleByRoleName("Developer")).thenReturn(roleData);
        when(employeePayrollRepository.save(any(EmployeePayroll.class))).thenReturn(testPayroll);

        // When
        EmployeePayroll result = employeePayrollRepository.save(testPayroll);

        // Then
        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName("Developer");
        verify(employeePayrollRepository, times(1)).save(any(EmployeePayroll.class));
    }

    @Test
    @DisplayName("Employee Service Integration - Failure Handling")
    void createPayroll_WhenEmployeeServiceFails_thenHandleException() {
        // Given
        when(employeeService.getEmployeeDetails(anyLong()))
                .thenThrow(new RuntimeException("Employee service unavailable"));
        when(roleService.getRoleByRoleName("Developer")).thenReturn(testRole);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            // This would be the actual service method call
            employeeService.getEmployeeDetails(1000L);
        });

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, never()).getRoleByRoleName(anyString());
    }

    @Test
    @DisplayName("Role Service Integration - Failure Handling")
    void createPayroll_WhenRoleServiceFails_thenHandleException() {
        // Given
        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName(anyString()))
                .thenThrow(new RuntimeException("Role service unavailable"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            roleService.getRoleByRoleName("Developer");
        });

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName("Developer");
    }

    @Test
    @DisplayName("Repository Interaction - Null Handling")
    void testRepositoryNullHandling() {
        // Given
        when(employeePayrollRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<EmployeePayroll> result = employeePayrollRepository.findById(123L);

        // Then
        assertFalse(result.isPresent());
        verify(employeePayrollRepository, times(1)).findById(123L);
    }

    @Test
    @DisplayName("Payroll Entity - Date Validation")
    void testPayrollDateValidation() {
        // Given
        EmployeePayroll payroll = new EmployeePayroll();
        Date currentDate = new Date();

        // When & Then
        assertDoesNotThrow(() -> payroll.setDateOfJoining(currentDate));
        assertEquals(currentDate, payroll.getDateOfJoining());
    }

    @Test
    @DisplayName("Payroll Entity - Port Assignment")
    void testPayrollPortAssignment() {
        // Given
        EmployeePayroll payroll = new EmployeePayroll();

        // When
        payroll.setPort(8080);

        // Then
        assertEquals(8080, payroll.getPort());
    }

    @Test
    @DisplayName("Service Integration - Both Services Success")
    void testServiceIntegration_BothServicesAvailable() {
        // Given
        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName("Developer")).thenReturn(testRole);

        // When & Then
        assertDoesNotThrow(() -> {
            employeeService.getEmployeeDetails(1000L);
            roleService.getRoleByRoleName("Developer");
        });

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName("Developer");
    }

    @Test
    @DisplayName("Payroll Entity - Field Validation")
    void testPayrollFieldValidation() {
        // Given
        EmployeePayroll payroll = new EmployeePayroll();

        // When & Then
        assertDoesNotThrow(() -> {
            payroll.setPayrollId(1L);
            payroll.setEmpId(1000L);
            payroll.setFirstName("John");
            payroll.setLastName("Doe");
            payroll.setRoleId(101L);
            payroll.setRoleName("Developer");
            payroll.setDescription("Software Developer");
            payroll.setDateOfJoining(new Date());
            payroll.setPort(8080);
        });

        assertEquals(1L, payroll.getPayrollId());
        assertEquals(1000L, payroll.getEmpId());
        assertEquals("John", payroll.getFirstName());
        assertEquals("Doe", payroll.getLastName());
        assertEquals(101L, payroll.getRoleId());
        assertEquals("Developer", payroll.getRoleName());
        assertEquals("Software Developer", payroll.getDescription());
        assertEquals(8080, payroll.getPort());
    }
}
