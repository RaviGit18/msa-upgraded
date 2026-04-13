# Employee Payroll Service

A microservice for managing payroll calculations and employee compensation within the MSA-Upgraded ecosystem.

## Overview

The Employee Payroll Service handles payroll processing, salary calculations, and compensation management for employees. It integrates with the Employee Service for employee data and provides comprehensive payroll functionality.

## Features

- Payroll calculation and processing
- Salary management and updates
- Integration with Employee Service
- Service discovery via Eureka
- Configuration management via Spring Cloud Config
- Distributed tracing with Spring Cloud Sleuth
- H2 in-memory database with web console
- RESTful API endpoints
- Feign client for inter-service communication

## Technology Stack

- **Spring Boot 1.5.2**
- **Spring Data JPA** - Database operations
- **Spring Web** - REST API framework
- **Spring Cloud Config** - External configuration
- **Spring Cloud Eureka** - Service discovery
- **Spring Cloud Feign** - Declarative REST client
- **Spring Cloud Ribbon** - Client-side load balancing
- **Spring Cloud Sleuth** - Distributed tracing
- **H2 Database** - In-memory database
- **Spring Boot DevTools** - Development tools

## Configuration

### Application Properties
```properties
server.port=8081
spring.application.name=employee-payroll-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Database Configuration
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)
- **Console**: http://localhost:8081/h2-console

## API Endpoints

### Payroll Operations

#### Get All Payroll Records
```http
GET http://localhost:8081/payrolls
```

#### Get Payroll by Employee ID
```http
GET http://localhost:8081/payrolls/employee/{employeeId}
```

#### Get Payroll by ID
```http
GET http://localhost:8081/payrolls/{id}
```

#### Create/Update Payroll
```http
POST http://localhost:8081/payrolls
Content-Type: application/json

{
  "employeeId": 1,
  "baseSalary": 75000.00,
  "bonus": 5000.00,
  "deductions": 2000.00,
  "effectiveDate": "2023-01-01"
}
```

#### Calculate Monthly Payroll
```http
GET http://localhost:8081/payrolls/calculate/{employeeId}?month=1&year=2023
```

#### Update Salary
```http
PUT http://localhost:8081/payrolls/{employeeId}/salary
Content-Type: application/json

{
  "newSalary": 80000.00,
  "effectiveDate": "2023-02-01",
  "reason": "Promotion"
}
```

### Health and Monitoring

#### Health Check
```http
GET http://localhost:8081/health
```

#### Application Info
```http
GET http://localhost:8081/info
```

#### Metrics
```http
GET http://localhost:8081/metrics
```

## Running the Service

### Prerequisites
- Java 8+
- Maven 3.3+
- Employee Service running (for integration)

### Development Mode
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package
java -jar target/employee-payroll-service-0.0.1-SNAPSHOT.jar
```

## Database Schema

### Payroll Entity
```sql
CREATE TABLE payroll (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  base_salary DECIMAL(10,2) NOT NULL,
  bonus DECIMAL(10,2) DEFAULT 0.00,
  deductions DECIMAL(10,2) DEFAULT 0.00,
  effective_date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (employee_id) REFERENCES employee(id)
);
```

### Salary History Entity
```sql
CREATE TABLE salary_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  old_salary DECIMAL(10,2),
  new_salary DECIMAL(10,2) NOT NULL,
  change_date DATE NOT NULL,
  reason VARCHAR(255),
  FOREIGN KEY (employee_id) REFERENCES employee(id)
);
```

## Integration Points

### Employee Service Integration
```java
@FeignClient(name = "employee-service")
public interface EmployeeServiceClient {
    @GetMapping("/employees/{id}")
    Employee getEmployeeById(@PathVariable("id") Long id);
}
```

### Service Discovery
- Registers with Eureka Server
- Discovers Employee Service dynamically
- Load balancing via Ribbon

### Configuration Management
- Fetches configuration from Spring Cloud Config Server
- Supports environment-specific configurations

### Distributed Tracing
- Generates trace IDs for request tracking
- Integrates with Zipkin for visualization

## Payroll Calculation Logic

### Monthly Salary Calculation
```java
public BigDecimal calculateMonthlySalary(Long employeeId, int month, int year) {
    Payroll payroll = payrollRepository.findByEmployeeId(employeeId);
    BigDecimal monthlySalary = payroll.getBaseSalary().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
    BigDecimal monthlyBonus = payroll.getBonus().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
    BigDecimal monthlyDeductions = payroll.getDeductions().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
    
    return monthlySalary.add(monthlyBonus).subtract(monthlyDeductions);
}
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### API Testing Examples

#### Test with cURL
```bash
# Create payroll record
curl -X POST http://localhost:8081/payrolls \
  -H "Content-Type: application/json" \
  -d '{"employeeId":1,"baseSalary":75000.00,"bonus":5000.00,"deductions":2000.00,"effectiveDate":"2023-01-01"}'

# Get payroll by employee
curl http://localhost:8081/payrolls/employee/1

# Calculate monthly payroll
curl http://localhost:8081/payrolls/calculate/1?month=1&year=2023
```

## Development Notes

### Inter-Service Communication
- Uses Feign client for type-safe REST calls
- Automatic retries and circuit breaker patterns
- Load balancing across multiple Employee Service instances

### Error Handling
- Global exception handling for consistent error responses
- Validation of payroll data
- Proper HTTP status codes
- Handling of Employee Service unavailability

### Logging
```bash
# Enable debug logging for Feign client
logging.level.com.payroll.microservices.employee.payroll.service.client.EmployeeServiceClient=DEBUG
```

## Troubleshooting

### Common Issues

1. **Employee Service Unavailable**
   - Check if Employee Service is registered in Eureka
   - Verify network connectivity between services
   - Check Feign client configuration

2. **Payroll Calculation Errors**
   - Validate salary values are positive
   - Check effective date format
   - Verify employee exists in Employee Service

3. **Database Issues**
   - Access H2 console at http://localhost:8081/h2-console
   - Verify database schema and data

## Monitoring

### Actuator Endpoints
- `/health` - Service health status
- `/info` - Application information
- `/metrics` - Performance metrics
- `/trace` - Request traces
- `/env` - Environment properties

### Business Metrics
- Payroll processing time
- Salary calculation accuracy
- Inter-service call success rates

## Security Considerations

- Sensitive payroll data encryption
- Access control for payroll operations
- Audit logging for salary changes
- Secure inter-service communication

## Performance Optimization

- Database indexing for employee_id
- Caching of frequently accessed payroll data
- Batch processing for bulk payroll calculations
- Connection pooling for database operations

## Dependencies

Key dependencies and their versions:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Actuator
- Spring Cloud Starter Config
- Spring Cloud Starter Eureka
- Spring Cloud Starter Feign
- Spring Cloud Starter Ribbon
- Spring Cloud Starter Sleuth
- H2 Database
- Spring Boot DevTools
