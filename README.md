# Microservices Architecture (MSA) Project

A comprehensive microservices architecture implementation using Spring Boot, Spring Cloud, and related technologies.

## Overview

This project demonstrates a complete microservices ecosystem with service discovery, configuration management, API gateway, distributed tracing, and security features.

## Architecture Components

### Core Services
- **Employee Service** - Manages employee information and operations
- **Employee Payroll Service** - Handles payroll calculations and management
- **Role Service** - Manages user roles and permissions

### Infrastructure Services
- **Eureka Naming Server** - Service discovery and registration
- **Spring Cloud Config Server** - Centralized configuration management
- **Zuul Edge Server** - API gateway for routing and load balancing
- **Zipkin Tracing** - Distributed tracing and monitoring

### Security Services
- **Spring Security App** - Basic authentication and authorization
- **Spring Security LDAP App** - LDAP-based authentication

## Technology Stack

- **Java 8**
- **Spring Boot 1.5.2.RELEASE**
- **Spring Cloud Dalston.RELEASE**
- **Maven** - Build tool and dependency management
- **H2 Database** - In-memory database for development
- **Eureka** - Service discovery
- **Zuul** - API gateway
- **Zipkin** - Distributed tracing
- **Spring Security** - Authentication and authorization

## Project Structure

```
msa/
├── pom.xml                           # Parent POM with common configuration
├── README.md                         # This file
├── employee-payroll-service/         # Payroll management service
├── employee-service/                 # Employee management service
├── eureka-naming-server/             # Service discovery server
├── role-service/                     # Role management service
├── spring-cloud-config-server/       # Configuration server
├── spring-security-app/              # Basic security implementation
├── spring-security-ldap-app/         # LDAP security implementation
├── zipkin-tracing/                   # Distributed tracing server
└── zuul-edge-server/                 # API gateway
```

## Quick Start

### Prerequisites
- Java 8 or higher
- Maven 3.3 or higher

### Building the Project

```bash
# Build all modules
mvn clean install

# Build specific module
mvn clean install -pl employee-service

# Skip tests during build
mvn clean install -DskipTests
```

### Running Services

1. **Start Infrastructure Services** (in order):
   ```bash
   # Start Eureka Server
   cd eureka-naming-server
   mvn spring-boot:run
   
   # Start Config Server
   cd ../spring-cloud-config-server
   mvn spring-boot:run
   
   # Start Zipkin (optional)
   cd ../zipkin-tracing
   mvn spring-boot:run
   ```

2. **Start Business Services**:
   ```bash
   # Start Employee Service
   cd ../employee-service
   mvn spring-boot:run
   
   # Start Payroll Service
   cd ../employee-payroll-service
   mvn spring-boot:run
   
   # Start Role Service
   cd ../role-service
   mvn spring-boot:run
   ```

3. **Start Gateway**:
   ```bash
   cd ../zuul-edge-server
   mvn spring-boot:run
   ```

### Service Endpoints

Once all services are running, you can access:

- **Eureka Dashboard**: http://localhost:8761
- **Zuul Gateway**: http://localhost:8765
- **Zipkin Dashboard**: http://localhost:9411
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
mvn test

# Run tests for specific module
mvn test -pl employee-service

# Run integration tests
mvn verify
```

## Monitoring and Tracing

- **Zipkin** provides distributed tracing across microservices
- **Spring Boot Actuator** endpoints expose health and metrics information
- **Eureka** provides service discovery and health monitoring

## Security

- **Spring Security App** demonstrates basic form-based authentication
- **Spring Security LDAP App** shows LDAP integration for enterprise authentication
- All services are configured with basic security where applicable

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
