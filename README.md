# Microservices Architecture (MSA-Upgraded) Project

A comprehensive microservices architecture implementation using Spring Boot, Spring Cloud, and related technologies.

## Overview

This project demonstrates a complete microservices ecosystem with service discovery, configuration management, API gateway, distributed tracing, and security features.

## Architecture Components

### Microservices Architecture Flow Diagram

<img width="1702" height="932" alt="image" src="https://github.com/user-attachments/assets/e31a213f-c1b4-4d0a-aa23-aa597f62221e" />

### System Architecture Overview
<img width="1005" height="441" alt="image" src="https://github.com/user-attachments/assets/afc9978d-9a04-40d4-beaf-917964aa3564" />

### Core Services
- **Employee Service** - Manages employee information and operations
- **Employee Payroll Service** - Handles payroll calculations and management
- **Role Service** - Manages user roles and permissions

### Infrastructure Services
- **Eureka Naming Server** - Service discovery and registration
- **Spring Cloud Config Server** - Centralized configuration management
- **Spring Cloud Gateway Server** - API gateway for routing and load balancing
- **Micrometer Tracing** - Distributed tracing and monitoring

## Technology Stack

- **Java 8**
- **Spring Boot 1.5.2.RELEASE**
- **Spring Cloud Dalston.RELEASE**
- **Gradle** - Build tool and dependency management
- **H2 Database** - In-memory database for development
- **Eureka** - Service discovery
- **Spring Cloud Gateway** - API gateway
- **Micrometer Tracing** - Distributed tracing
- **Spring Security** - Authentication and authorization

### Technology Stack Diagram

<img width="1011" height="490" alt="image" src="https://github.com/user-attachments/assets/4bd2d5ef-6161-493b-a286-dbec1c99dea0" />


## Project Structure

```
msa-upgraded/
├── pom.xml                           # Parent POM with common configuration
├── README.md                         # This file
├── employee-payroll-service/         # Payroll management service
├── employee-service/                 # Employee management service
├── eureka-naming-server/             # Service discovery server
├── role-service/                     # Role management service
├── spring-cloud-config-server/       # Configuration server
├── micrometer-tracing/               # Distributed tracing server
└── spring-cloud-gateway-server/      # API gateway
```

<img width="1745" height="731" alt="image" src="https://github.com/user-attachments/assets/f33adf97-aa58-4f69-b7c4-6e467ff3cc42" />


### Data Flow Diagram

<img width="921" height="910" alt="image" src="https://github.com/user-attachments/assets/6499adb7-7cf2-458e-968e-1fef1606b944" />

<img width="1123" height="883" alt="image" src="https://github.com/user-attachments/assets/056fb7de-c115-4050-aed6-da84cc0594a5" />


## Quick Start

### Prerequisites
- Java 17 or higher
- Gradle 8.0 or higher (or use provided wrapper)

### Building the Project

```bash
# Build all modules
./gradlew clean build

# Build specific module
./gradlew :employee-service:clean :employee-service:build

# Skip tests during build
./gradlew clean build -x test
```

### Gradle Build System Architecture
<img width="1856" height="571" alt="image" src="https://github.com/user-attachments/assets/8b919851-6b0d-4d0c-8353-b5788e3163ca" />

### Running Services
<img width="212" height="695" alt="image" src="https://github.com/user-attachments/assets/5421bb44-52f6-416f-b092-3ceaa9ae1986" />


1. **Start Infrastructure Services** (in order):
   ```bash
   # Start Eureka Server
   cd eureka-naming-server
   ../gradlew bootRun
   
   # Start Config Server
   cd ../spring-cloud-config-server
   ../gradlew bootRun
   
   # Start Tracing (optional)
   cd ../micrometer-tracing
   ../gradlew bootRun
   ```

2. **Start Business Services**:
   ```bash
   # Start Employee Service
   cd ../employee-service
   ../gradlew bootRun
   
   # Start Payroll Service
   cd ../employee-payroll-service
   ../gradlew bootRun
   
   # Start Role Service
   cd ../role-service
   ../gradlew bootRun
   ```

3. **Start Gateway**:
   ```bash
   cd ../spring-cloud-gateway-server
   ../gradlew bootRun
   ```

### Service Endpoints

Once all services are running, you can access:

- **Eureka Dashboard**: http://localhost:8761
- **Spring Cloud Gateway**: http://localhost:8765
- **Micrometer Tracing Dashboard**: http://localhost:9411
- **Config Server**: http://localhost:8888

## Development

### Configuration

All services use Spring Cloud Config for centralized configuration. Configuration files are located in the `spring-cloud-config-server` module.

### Database

Services use H2 in-memory database by default. The database console is available at:
- Employee Service: http://localhost:8080/h2-console
- Payroll Service: http://localhost:8081/h2-console
- Role Service: http://localhost:8082/h2-console

Default credentials:
- URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

### Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :employee-service:test

# Run integration tests
./gradlew check
```

## Monitoring and Tracing

- **Micrometer Tracing** provides distributed tracing across microservices
- **Spring Boot Actuator** endpoints expose health and metrics information
- **Eureka** provides service discovery and health monitoring

<img width="989" height="876" alt="image" src="https://github.com/user-attachments/assets/0171f37d-d6c2-4d9f-87ae-22aa47886794" />

## API Documentation

Each service exposes RESTful APIs. You can explore the APIs using:
- Swagger UI (if configured)
- Postman collections
- Direct HTTP requests

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## License

This project is for educational and demonstration purposes.

## Support

For questions or issues, please refer to the individual service documentation or create an issue in the repository.
