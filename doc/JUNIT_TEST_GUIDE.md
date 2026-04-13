# JUnit Test Cases with Mockito - Complete Guide

## Overview
This document provides comprehensive JUnit 5 test cases with Mockito for all microservices in the MSA-Upgraded project. Tests cover controllers, services, repositories, and integration scenarios.

## Test Structure

### Directory Organization
```
src/test/java/com/payroll/microservices/
├── employeeservice/
│   ├── EmployeeControllerTest.java
│   ├── EmployeeServiceTest.java
│   └── EmployeeRepositoryTest.java
├── roleservice/
│   ├── RoleControllerTest.java
│   ├── RoleServiceTest.java
│   └── EmployeeRoleRepositoryTest.java
└── employeepayrollservice/
    ├── EmployeePayrollControllerTest.java
    ├── EmployeePayrollServiceTest.java
    └── EmployeePayrollRepositoryTest.java
```

## Test Dependencies

### Maven Test Dependencies
```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Employee Service Tests

### EmployeeControllerTest.java

#### Test Coverage:
- ✅ **GET /employee/{empId}** - Success scenario
- ✅ **GET /employee/{empId}** - Not found scenario  
- ✅ **GET /employee/fault-tolerance** - Circuit breaker fallback
- ✅ **GET /employee/{empId}** - Database error handling
- ✅ **Entity validation** - Field constraints
- ✅ **Circuit breaker annotation** - Configuration validation

#### Key Test Methods:
```java
@Test
@DisplayName("GET /employee/{empId} - Success")
void getEmployeeDetails_WhenValidEmpId_thenReturnEmployee()

@Test
@DisplayName("GET /employee/fault-tolerance - Circuit Breaker Fallback")
void getEmployeeDetailsFaultTolerance_WhenException_thenReturnFallbackResponse()
```

#### Mock Strategies:
- **@MockBean EmployeeRepository** - Mock database layer
- **@MockBean EmployeeConfiguration** - Mock configuration properties
- **MockMvc** - Test REST endpoints
- **ObjectMapper** - JSON serialization testing

### EmployeeServiceTest.java

#### Test Coverage:
- ✅ **Repository operations** - CRUD operations
- ✅ **Business logic** - Service layer methods
- ✅ **Data validation** - Entity constraints
- ✅ **Null handling** - Error scenarios
- ✅ **Date operations** - Temporal data handling

#### Key Test Methods:
```java
@Test
@DisplayName("Find Employee by ID - Success")
void findEmployeeById_WhenEmployeeExists_thenReturnEmployee()

@Test
@DisplayName("Save Employee - Success")
void saveEmployee_WhenValidEmployee_thenReturnSavedEmployee()
```

## Role Service Tests

### RoleControllerTest.java

#### Test Coverage:
- ✅ **GET /role/{roleName}** - Success scenario
- ✅ **GET /role/{roleName}** - Not found scenario
- ✅ **GET /role/{roleName}** - Case insensitive search
- ✅ **GET /role/{roleName}** - Database error handling
- ✅ **GET /role/{roleName}** - Special characters handling
- ✅ **GET /role/{roleName}** - Empty role name validation
- ✅ **Entity validation** - Field constraints
- ✅ **Repository null handling** - Error scenarios
- ✅ **Role name length** - Input validation

#### Key Test Methods:
```java
@Test
@DisplayName("GET /role/{roleName} - Success")
void getRoleByRoleName_WhenValidRoleName_thenReturnRole()

@Test
@DisplayName("GET /role/{roleName} - Case Insensitive")
void getRoleByRoleName_WhenDifferentCase_thenReturnRole()
```

#### Advanced Test Scenarios:
- **Special Characters**: Tests with hyphens, underscores
- **Case Sensitivity**: Verifies case-insensitive search
- **Input Validation**: Empty strings, null values
- **Length Validation**: Very long role names

## Employee Payroll Service Tests

### EmployeePayrollControllerTest.java

#### Test Coverage:
- ✅ **POST /employee/{empId}/role/{roleName}** - Success scenario
- ✅ **POST /employee/{empId}/role/{roleName}** - Employee not found
- ✅ **POST /employee/{empId}/role/{roleName}** - Role not found
- ✅ **POST /employee/{empId}/role/{roleName}** - Database error
- ✅ **POST /employee/{empId}/role/{roleName}** - Special characters
- ✅ **POST /employee/{empId}/role/{roleName}** - Long role names
- ✅ **Entity validation** - Field constraints
- ✅ **Service integration** - Employee service failures
- ✅ **Service integration** - Role service failures
- ✅ **Repository interaction** - Save verification

#### Key Test Methods:
```java
@Test
@DisplayName("POST /employee/{empId}/role/{roleName} - Success")
void insertEmployeePayrollDetails_WhenValidData_thenReturnPayroll()

@Test
@DisplayName("Service Integration - Employee Service Null")
void testServiceIntegration_EmployeeServiceNull()
```

#### Integration Testing:
- **Feign Client Mocking**: Mock external service calls
- **Service Orchestration**: Test inter-service communication
- **Error Propagation**: Verify proper error handling
- **Data Combination**: Test employee + role data merging

### EmployeePayrollServiceTest.java

#### Test Coverage:
- ✅ **CRUD operations** - Create, read, update, delete
- ✅ **Service integration** - Employee and role service calls
- ✅ **Error handling** - Service failure scenarios
- ✅ **Data validation** - Entity constraints
- ✅ **Repository interaction** - Database operations
- ✅ **Business logic** - Payroll creation logic

#### Key Test Methods:
```java
@Test
@DisplayName("Create Payroll with Employee and Role Data")
void createPayroll_WhenEmployeeAndRoleData_thenReturnCombinedPayroll()

@Test
@DisplayName("Service Integration - Both Services Success")
void testServiceIntegration_BothServicesAvailable()
```

## Running Tests

### Command Line
```bash
# Run all tests for entire project
mvn test

# Run tests for specific module
cd employee-service && mvn test

# Run specific test class
mvn test -Dtest=EmployeeControllerTest

# Run specific test method
mvn test -Dtest=EmployeeControllerTest#getEmployeeDetails_WhenValidEmpId_thenReturnEmployee
```

### IDE Integration
- **IntelliJ IDEA**: Right-click test class → Run
- **VS Code**: Test Explorer → Run Test
- **Eclipse**: Right-click → Run As → JUnit Test

## Test Reports

### Maven Surefire Reports
```bash
# Generate test reports
mvn surefire-report:report

# View reports
open target/site/surefire-report.html
```

### Coverage Reports
```bash
# Generate JaCoCo coverage
mvn jacoco:report

# View coverage
open target/site/jacoco/index.html
```

## Mocking Strategies

### Repository Mocking
```java
@Mock
private EmployeeRepository employeeRepository;

// Setup mock behavior
when(employeeRepository.findById(1000L))
    .thenReturn(Optional.of(testEmployee));

// Verify interactions
verify(employeeRepository, times(1)).findById(1000L);
```

### Service Mocking
```java
@MockBean
private EmployeeService employeeService;

// Setup mock behavior
when(employeeService.getEmployeeDetails(1000L))
    .thenReturn(testEmployee);

// Verify service calls
verify(employeeService, times(1)).getEmployeeDetails(1000L);
```

### Configuration Mocking
```java
@MockBean
private EmployeeConfiguration employeeConfiguration;

// Setup mock configuration
when(employeeConfiguration.getDefaultFirstName())
    .thenReturn("DefaultFirstName");
```

## Assertion Strategies

### JUnit 5 Assertions
```java
import static org.junit.jupiter.api.Assertions.*;

// Basic assertions
assertEquals(expected, actual);
assertNotEquals(unexpected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(object);
assertNotNull(object);

// Exception testing
assertThrows(ExpectedException.class, () -> {
    // Code that should throw exception
});

assertDoesNotThrow(() -> {
    // Code that should not throw exception
});
```

### JSON Path Assertions
```java
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// JSON response validation
.andExpect(jsonPath("$.field").value("expectedValue"))
.andExpect(jsonPath("$.nested.field").exists())
.andExpect(jsonPath("$.array[0].property").value("firstItem"))
```

## Test Data Management

### Test Data Builders
```java
public class EmployeeTestDataBuilder {
    public static Employee buildValidEmployee() {
        Employee employee = new Employee();
        employee.setEmpId(1000L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDateOfJoining(new Date());
        employee.setPort(8080);
        return employee;
    }
    
    public static Employee buildEmployeeWithNullFirstName() {
        Employee employee = buildValidEmployee();
        employee.setFirstName(null);
        return employee;
    }
}
```

### Test Fixtures
```java
private Employee testEmployee;
private List<Employee> employeeList;

@BeforeEach
void setUp() {
    testEmployee = EmployeeTestDataBuilder.buildValidEmployee();
    employeeList = Arrays.asList(testEmployee);
}
```

## Advanced Testing Patterns

### Parameterized Tests
```java
@ParameterizedTest
@ValueSource(strings = {"Developer", "QA", "PM", "HR"})
void testRoleNames(String roleName) {
    // Test with multiple role names
}
```

### Exception Testing
```java
@Test
@DisplayName("Database Error Handling")
void testDatabaseError() {
    // Given
    when(employeeRepository.findById(anyLong()))
        .thenThrow(new RuntimeException("Database connection failed"));
    
    // When & Then
    assertThrows(RuntimeException.class, () -> {
        employeeService.findById(1L);
    });
}
```

### Integration Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false"
})
class IntegrationTest {
    // Test with real Spring context
}
```

## Best Practices

### Test Naming Conventions
```java
// Method names should describe the scenario
@Test
@DisplayName("GET /employee/{empId} - When Employee Exists - Then Return Employee")
void getEmployeeById_WhenEmployeeExists_thenReturnEmployee()

// Use Given-When-Then pattern in test documentation
```

### Test Organization
- **Arrange**: Setup test data and mocks
- **Act**: Execute the method under test
- **Assert**: Verify the results
- **Cleanup**: Reset mocks if needed

### Mock Verification
```java
// Verify exact number of calls
verify(employeeRepository, times(1)).save(any(Employee.class));

// Verify no additional interactions
verifyNoMoreInteractions(employeeRepository);

// Verify specific arguments
verify(employeeService).getEmployeeDetails(1000L);
```

## Continuous Integration

### GitHub Actions
```yaml
name: Run Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run tests
      run: mvn test
```

### Test Coverage Goals
- **Line Coverage**: > 80%
- **Branch Coverage**: > 75%
- **Method Coverage**: > 90%

## Running Tests with Different Profiles

### Test Profile Configuration
```properties
# src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.jpa.hibernate.ddl-auto=create-drop
spring.profiles.active=test
```

### Profile-Specific Tests
```java
@ActiveProfiles("test")
@SpringBootTest
class ProfileSpecificTest {
    // Tests with test profile
}
```

## Performance Testing

### Load Testing with JUnit
```java
@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)
void performanceTest() {
    // Test should complete within 5 seconds
}
```

### Concurrent Testing
```java
@Test
void concurrentAccessTest() throws InterruptedException {
    int threadCount = 10;
    CountDownLatch latch = new CountDownLatch(threadCount);
    
    for (int i = 0; i < threadCount; i++) {
        new Thread(() -> {
            // Execute test in parallel
            latch.countDown();
        }).start();
    }
    
    latch.await(10, TimeUnit.SECONDS);
}
```

## Test Utilities

### Custom Assertions
```java
public class CustomAssertions {
    public static EmployeeAssert assertThat(Employee actual) {
        return new EmployeeAssert(actual);
    }
}

public class EmployeeAssert {
    private final Employee actual;
    
    public EmployeeAssert(Employee actual) {
        this.actual = actual;
    }
    
    public EmployeeAssert hasFirstName(String expected) {
        assertEquals(expected, actual.getFirstName());
        return this;
    }
    
    public EmployeeAssert hasLastName(String expected) {
        assertEquals(expected, actual.getLastName());
        return this;
    }
}
```

### Test Helpers
```java
public class TestHelper {
    public static String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

## Summary

This comprehensive test suite provides:

### ✅ **Complete Coverage**
- Controller layer tests with MockMvc
- Service layer tests with Mockito
- Repository layer tests with mocks
- Integration tests with Spring context

### ✅ **Modern Testing**
- JUnit 5 annotations and assertions
- Mockito 4.x mocking framework
- Spring Boot Test annotations
- JSON Path testing for REST APIs

### ✅ **Quality Assurance**
- Positive and negative test cases
- Error handling and exception scenarios
- Input validation and boundary testing
- Service integration testing

### ✅ **Maintainability**
- Clear test naming conventions
- Comprehensive documentation
- Reusable test data builders
- Organized test structure

These tests ensure the reliability and correctness of all microservices in the MSA-Upgraded project.
