package com.payroll.microservices.employeepayrollservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.microservices.employeepayrollservice.model.EmployeePayroll;
import com.payroll.microservices.employeepayrollservice.repository.EmployeePayrollRepository;
import com.payroll.microservices.employeepayrollservice.service.EmployeeService;
import com.payroll.microservices.employeepayrollservice.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Employee Payroll Controller Tests")
class EmployeePayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeePayrollRepository employeePayrollRepository;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeePayroll testPayroll;
    private Employee testEmployee;
    private EmployeePayroll testRole;

    @BeforeEach
    void setUp() {
        // Setup test employee
        testEmployee = new EmployeePayroll();
        testEmployee.setEmpId(1000L);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDateOfJoining(new Date());
        testEmployee.setPort(8080);

        // Setup test role
        testRole = new EmployeePayroll();
        testRole.setRoleId(101L);
        testRole.setRoleName("Developer");
        testRole.setDescription("Software Developer");

        // Setup test payroll
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
    }

    @Test
    @DisplayName("POST /employee/{empId}/role/{roleName} - Success")
    void insertEmployeePayrollDetails_WhenValidData_thenReturnPayroll() throws Exception {
        // Given
        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName("Developer")).thenReturn(testRole);
        when(employeePayrollRepository.save(any(EmployeePayroll.class))).thenReturn(testPayroll);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/1000/role/Developer")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payrollId").value(1))
                .andExpect(jsonPath("$.empId").value(1000))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.roleId").value(101))
                .andExpect(jsonPath("$.roleName").value("Developer"))
                .andExpect(jsonPath("$.description").value("Software Developer"));

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName("Developer");
        verify(employeePayrollRepository, times(1)).save(any(EmployeePayroll.class));
    }

    @Test
    @DisplayName("POST /employee/{empId}/role/{roleName} - Employee Not Found")
    void insertEmployeePayrollDetails_WhenEmployeeNotFound_thenReturn500() throws Exception {
        // Given
        when(employeeService.getEmployeeDetails(999L))
                .thenThrow(new RuntimeException("Employee not found"));
        when(roleService.getRoleByRoleName("Developer")).thenReturn(testRole);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/999/role/Developer")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(employeeService, times(1)).getEmployeeDetails(999L);
        verify(roleService, never()).getRoleByRoleName(anyString());
        verify(employeePayrollRepository, never()).save(any(EmployeePayroll.class));
    }

    @Test
    @DisplayName("POST /employee/{empId}/role/{roleName} - Role Not Found")
    void insertEmployeePayrollDetails_WhenRoleNotFound_thenReturn500() throws Exception {
        // Given
        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName("NonExistent"))
                .thenThrow(new RuntimeException("Role not found"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/1000/role/NonExistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName("NonExistent");
        verify(employeePayrollRepository, never()).save(any(EmployeePayroll.class));
    }

    @Test
    @DisplayName("POST /employee/{empId}/role/{roleName} - Database Error")
    void insertEmployeePayrollDetails_WhenDatabaseError_thenReturn500() throws Exception {
        // Given
        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName("Developer")).thenReturn(testRole);
        when(employeePayrollRepository.save(any(EmployeePayroll.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/1000/role/Developer")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName("Developer");
        verify(employeePayrollRepository, times(1)).save(any(EmployeePayroll.class));
    }

    @Test
    @DisplayName("POST /employee/{empId}/role/{roleName} - Special Characters in Role Name")
    void insertEmployeePayrollDetails_WhenSpecialCharacters_thenReturnPayroll() throws Exception {
        // Given
        EmployeePayroll specialRole = new EmployeePayroll();
        specialRole.setRoleId(102L);
        specialRole.setRoleName("QA-Tester");
        specialRole.setDescription("Quality Assurance Tester");

        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName("QA-Tester")).thenReturn(specialRole);
        when(employeePayrollRepository.save(any(EmployeePayroll.class))).thenReturn(testPayroll);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/1000/role/QA-Tester")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName("QA-Tester");
        verify(employeePayrollRepository, times(1)).save(any(EmployeePayroll.class));
    }

    @Test
    @DisplayName("POST /employee/{empId}/role/{roleName} - Long Role Name")
    void insertEmployeePayrollDetails_WhenLongRoleName_thenReturnPayroll() throws Exception {
        // Given
        String longRoleName = "A".repeat(100); // Very long role name
        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName(longRoleName)).thenReturn(testRole);
        when(employeePayrollRepository.save(any(EmployeePayroll.class))).thenReturn(testPayroll);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/1000/role/" + longRoleName)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName(longRoleName);
        verify(employeePayrollRepository, times(1)).save(any(EmployeePayroll.class));
    }

    @Test
    @DisplayName("Employee Payroll Entity - Validation")
    void testEmployeePayrollEntityValidation() {
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
    }

    @Test
    @DisplayName("Service Integration - Employee Service Null")
    void testServiceIntegration_EmployeeServiceNull() {
        // Given
        when(employeeService.getEmployeeDetails(anyLong()))
                .thenThrow(new RuntimeException("Employee service unavailable"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/employee/1000/role/Developer")
                    .contentType(MediaType.APPLICATION_JSON));
        });

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
    }

    @Test
    @DisplayName("Service Integration - Role Service Null")
    void testServiceIntegration_RoleServiceNull() {
        // Given
        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName(anyString()))
                .thenThrow(new RuntimeException("Role service unavailable"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/employee/1000/role/Developer")
                    .contentType(MediaType.APPLICATION_JSON));
        });

        verify(employeeService, times(1)).getEmployeeDetails(1000L);
        verify(roleService, times(1)).getRoleByRoleName("Developer");
    }

    @Test
    @DisplayName("Repository Interaction - Save Verification")
    void testRepositorySaveInteraction() {
        // Given
        when(employeeService.getEmployeeDetails(1000L)).thenReturn(testEmployee);
        when(roleService.getRoleByRoleName("Developer")).thenReturn(testRole);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/1000/role/Developer")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        verify(employeePayrollRepository, times(1)).save(any(EmployeePayroll.class));
    }
}
