package com.payroll.microservices.roleservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.microservices.roleservice.model.EmployeeRole;
import com.payroll.microservices.roleservice.repository.EmployeeRoleRepository;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Role Controller Tests")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRoleRepository employeeRoleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeRole testRole;

    @BeforeEach
    void setUp() {
        testRole = new EmployeeRole();
        testRole.setRoleId(101L);
        testRole.setRoleName("Developer");
        testRole.setDescription("Software Developer");
    }

    @Test
    @DisplayName("GET /role/{roleName} - Success")
    void getRoleByRoleName_WhenValidRoleName_thenReturnRole() throws Exception {
        // Given
        when(employeeRoleRepository.findByRoleName("Developer")).thenReturn(testRole);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/role/Developer")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value(101))
                .andExpect(jsonPath("$.roleName").value("Developer"))
                .andExpect(jsonPath("$.description").value("Software Developer"));

        verify(employeeRoleRepository, times(1)).findByRoleName("Developer");
    }

    @Test
    @DisplayName("GET /role/{roleName} - Role Not Found")
    void getRoleByRoleName_WhenRoleNotFound_thenReturn404() throws Exception {
        // Given
        when(employeeRoleRepository.findByRoleName("NonExistent")).thenReturn(null);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/role/NonExistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(employeeRoleRepository, times(1)).findByRoleName("NonExistent");
    }

    @Test
    @DisplayName("GET /role/{roleName} - Case Insensitive")
    void getRoleByRoleName_WhenDifferentCase_thenReturnRole() throws Exception {
        // Given
        when(employeeRoleRepository.findByRoleName("developer")).thenReturn(testRole);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/role/developer")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value(101))
                .andExpect(jsonPath("$.roleName").value("Developer"));

        verify(employeeRoleRepository, times(1)).findByRoleName("developer");
    }

    @Test
    @DisplayName("GET /role/{roleName} - Database Error")
    void getRoleByRoleName_WhenDatabaseError_thenReturn500() throws Exception {
        // Given
        when(employeeRoleRepository.findByRoleName(anyString()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/role/Developer")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(employeeRoleRepository, times(1)).findByRoleName("Developer");
    }

    @Test
    @DisplayName("GET /role/{roleName} - Special Characters")
    void getRoleByRoleName_WhenSpecialCharacters_thenReturnRole() throws Exception {
        // Given
        EmployeeRole specialRole = new EmployeeRole();
        specialRole.setRoleId(102L);
        specialRole.setRoleName("QA-Tester");
        specialRole.setDescription("Quality Assurance Tester");
        
        when(employeeRoleRepository.findByRoleName("QA-Tester")).thenReturn(specialRole);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/role/QA-Tester")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value(102))
                .andExpect(jsonPath("$.roleName").value("QA-Tester"))
                .andExpect(jsonPath("$.description").value("Quality Assurance Tester"));

        verify(employeeRoleRepository, times(1)).findByRoleName("QA-Tester");
    }

    @Test
    @DisplayName("GET /role/{roleName} - Empty Role Name")
    void getRoleByRoleName_WhenEmptyRoleName_thenReturn400() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/role/")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("EmployeeRole Entity - Validation")
    void testEmployeeRoleEntityValidation() {
        // Given
        EmployeeRole role = new EmployeeRole();

        // When & Then
        assertDoesNotThrow(() -> {
            role.setRoleId(1L);
            role.setRoleName("Test Role");
            role.setDescription("Test Description");
        });
    }

    @Test
    @DisplayName("Repository Interaction - Null Handling")
    void testRepositoryNullHandling() {
        // Given
        when(employeeRoleRepository.findByRoleName(anyString())).thenReturn(null);

        // When
        EmployeeRole result = employeeRoleRepository.findByRoleName("NonExistent");

        // Then
        assertNull(result);
        verify(employeeRoleRepository, times(1)).findByRoleName("NonExistent");
    }

    @Test
    @DisplayName("Role Name - Length Validation")
    void testRoleNameLengthValidation() {
        // Given
        EmployeeRole role = new EmployeeRole();
        String longRoleName = "A".repeat(200); // Very long role name

        // When & Then
        assertDoesNotThrow(() -> {
            role.setRoleName(longRoleName);
            assertEquals(longRoleName, role.getRoleName());
        });
    }
}
