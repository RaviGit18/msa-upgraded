# Employee Service

A microservice for managing employee information and operations within the MSA ecosystem.

## Overview

The Employee Service provides CRUD operations for employee data and integrates with other microservices for payroll and role management.

## Features

- Employee CRUD operations (Create, Read, Update, Delete)
- Integration with Eureka for service discovery
- Configuration management via Spring Cloud Config
- Distributed tracing with Spring Cloud Sleuth
- H2 in-memory database with web console
- RESTful API endpoints

## Technology Stack

- **Spring Boot 1.5.2**
- **Spring Data JPA** - Database operations
- **Spring Web** - REST API framework
- **Spring Cloud Config** - External configuration
- **Spring Cloud Eureka** - Service discovery
- **Spring Cloud Sleuth** - Distributed tracing
- **H2 Database** - In-memory database
- **Lombok** - Code generation
- **Spring Boot DevTools** - Development tools

## Configuration

### Application Properties
```properties
server.port=8080
spring.application.name=employee-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Database Configuration
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)
- **Console**: http://localhost:8080/h2-console

## API Endpoints

### Employee Operations

#### Get All Employees
```http
GET http://localhost:8080/employees
```

#### Get Employee by ID
```http
GET http://localhost:8080/employees/{id}
```

#### Create Employee
```http
POST http://localhost:8080/employees
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "IT"
}
```

#### Update Employee
```http
PUT http://localhost:8080/employees/{id}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "department": "Finance"
}
```

#### Delete Employee
```http
DELETE http://localhost:8080/employees/{id}
```

### Health and Monitoring

#### Health Check
```http
GET http://localhost:8080/health
```

#### Application Info
```http
GET http://localhost:8080/info
```

#### Metrics
```http
GET http://localhost:8080/metrics
```

## Running the Service

### Prerequisites
- Java 8+
- Maven 3.3+

### Development Mode
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package
java -jar target/employee-service-0.0.1-SNAPSHOT.jar
```

## Database Schema

### Employee Entity
```sql
CREATE TABLE employee (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  department VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## Integration Points

### Service Discovery
- Registers with Eureka Server at startup
- Enables other services to discover and communicate

### Configuration Management
- Fetches configuration from Spring Cloud Config Server
- Supports environment-specific configurations

### Distributed Tracing
- Generates trace IDs for request tracking
- Integrates with Zipkin for visualization

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
# Create employee
curl -X POST http://localhost:8080/employees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Doe","email":"jane@example.com","department":"HR"}'

# Get all employees
curl http://localhost:8080/employees

# Get specific employee
curl http://localhost:8080/employees/1
```

## Development Notes

### Hot Reload
The service includes Spring Boot DevTools for automatic restart on code changes.

### Logging
Logs are configured to show trace IDs for distributed tracing:
```
2023-01-01 10:00:00.000 INFO [employee-service,trace-id,span-id] --- ...
```

### Error Handling
- Global exception handling for consistent error responses
- Validation of input data
- Proper HTTP status codes

## Troubleshooting

### Common Issues

1. **Service Registration Failed**
   - Ensure Eureka server is running on port 8761
   - Check network connectivity

2. **Database Connection Issues**
   - Verify H2 console is accessible at http://localhost:8080/h2-console
   - Check database configuration

3. **Configuration Not Loading**
   - Ensure Config Server is running on port 8888
   - Verify configuration file names and locations

## Monitoring

### Actuator Endpoints
- `/health` - Service health status
- `/info` - Application information
- `/metrics` - Performance metrics
- `/trace` - Request traces
- `/dump` - Thread dump

### Distributed Tracing
- Trace IDs are automatically added to logs
- Integration with Zipkin for detailed tracing visualization

## Dependencies

Key dependencies and their versions:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Actuator
- Spring Cloud Starter Config
- Spring Cloud Starter Eureka
- Spring Cloud Starter Sleuth
- H2 Database
- Lombok
- Spring Boot DevTools
