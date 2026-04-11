# MSA Project - Microservices Flow Diagram

## Overview
This document illustrates the complete flow architecture and communication patterns between microservices in the upgraded MSA project.

## Architecture Diagram

```
                    ┌─────────────────────────────────────────────────────────────────────────┐
                    │                    EXTERNAL CLIENTS                           │
                    │                    (Web/Mobile/API)                          │
                    └─────────────────────┬───────────────────────────────────────┘
                                          │
                                          │ HTTP/REST
                                          ▼
                    ┌─────────────────────────────────────────────────────────────────┐
                    │              ZUUL EDGE SERVER (GATEWAY)            │
                    │                    Port: 9090                   │
                    │              ┌─────────────┴─────────────┐           │
                    │              │   ROUTING & LOAD BALANCING   │           │
                    │              └─────────────┬─────────────┘           │
                    │                             │                         │
                    ┌─────────────────────┬───────┴───────┬─────────────┐
                    │                     │                 │             │
                    ▼                     ▼                 ▼             ▼
              ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
              │ EMPLOYEE SERVICE │   │  ROLE SERVICE   │   │ EMPLOYEE PAYROLL │
              │    Port: 8080  │   │   Port: 8081  │   │   SERVICE         │
              │                 │   │                 │   │   Port: 8082    │
              └─────────┬───────┘   └─────────┬───────┘   └─────────┬───────┘
                        │                     │                 │             │
                        │ Feign Client        │ Feign Client  │             │
                        │ (Service Discovery)  │ (Service Discovery)  │             │
                        ▼                     ▼                 ▼             ▼
              ┌─────────────────────────────────────────────────────────────────────────┐
              │                    EUREKA NAMING SERVER                   │
              │                    (SERVICE DISCOVERY)                   │
              │                    Port: 8761                             │
              └─────────────────────────────────────────────────────────────────────────┘
                        ▲
                        │ Service Registration
                        ▼
              ┌─────────────────────────────────────────────────────────────────────────┐
              │                 SPRING CLOUD CONFIG SERVER               │
              │                 (CONFIGURATION MANAGEMENT)               │
              │                    Port: 8888                             │
              └─────────────────────────────────────────────────────────────────────────┘

                        ▲
                        │ Configuration Fetch
                        ▼
              ┌─────────────────────────────────────────────────────────────────────────┐
              │                     ZIPKIN TRACING                       │
              │                 (DISTRIBUTED TRACING)                   │
              │                    Port: 9411                             │
              └─────────────────────────────────────────────────────────────────────────┘
```

## Service Communication Flow

### 1. Client Request Flow
```
External Client → Zuul Gateway (Port 9090)
                ↓
           Route Request based on Path
                ↓
    ┌─────────────────────┬─────────────────────┐
    │                   │                   │
    ▼                   ▼                   ▼
Employee Service      Role Service        Employee Payroll Service
(Port 8080)        (Port 8081)        (Port 8082)
```

### 2. Service Registration Flow
```
All Services → Eureka Server (Port 8761)
    ↓
Service Registration & Discovery
    ↓
Health Checks & Load Balancing
```

### 3. Configuration Flow
```
All Services → Config Server (Port 8888)
    ↓
Fetch Configuration Properties
    ↓
Environment-Specific Settings
```

### 4. Tracing Flow
```
All Services → Zipkin (Port 9411)
    ↓
Distributed Tracing Data
    ↓
Request Correlation & Performance Monitoring
```

## Detailed Service Interactions

### Employee Service (Port 8080)
**Responsibilities:**
- Manage employee data and information
- Provide employee details to other services
- Handle circuit breaker patterns with Resilience4j

**API Endpoints:**
- `GET /employee/{empId}` - Retrieve employee by ID
- `GET /employee/fault-tolerance` - Test circuit breaker

**Dependencies:**
- Eureka Server (Service Discovery)
- Config Server (Configuration)
- H2 Database (In-Memory)

---

### Role Service (Port 8081)
**Responsibilities:**
- Manage role definitions and descriptions
- Provide role information to payroll service
- Support case-insensitive role lookup

**API Endpoints:**
- `GET /role/{roleName}` - Retrieve role by name

**Dependencies:**
- Eureka Server (Service Discovery)
- Config Server (Configuration)
- H2 Database (In-Memory)

---

### Employee Payroll Service (Port 8082)
**Responsibilities:**
- Orchestrate employee and role data
- Create comprehensive payroll records
- Handle inter-service communication via Feign clients

**API Endpoints:**
- `POST /employee/{empId}/role/{roleName}` - Create payroll record

**Dependencies:**
- Eureka Server (Service Discovery)
- Config Server (Configuration)
- Employee Service (Feign Client)
- Role Service (Feign Client)
- H2 Database (In-Memory)

---

### Zuul Edge Server - Spring Cloud Gateway (Port 9090)
**Responsibilities:**
- Single entry point for all external requests
- Route requests to appropriate microservices
- Handle load balancing and fault tolerance
- Provide API gateway with security potential

**Routing Rules:**
```
/employee-service/** → Employee Service (Port 8080)
/role-service/**    → Role Service (Port 8081)
/employee-payroll-service/** → Employee Payroll Service (Port 8082)
```

**Dependencies:**
- Eureka Server (Service Discovery)
- Config Server (Configuration)
- All Business Services (for routing)

---

### Eureka Naming Server (Port 8761)
**Responsibilities:**
- Service registration and discovery
- Health monitoring of registered services
- Load balancing coordination
- Service metadata management

**Features:**
- Service dashboard at `/`
- Registration status tracking
- Instance health monitoring
- Service metadata exchange

---

### Spring Cloud Config Server (Port 8888)
**Responsibilities:**
- Centralized configuration management
- Environment-specific property serving
- Configuration versioning
- Dynamic configuration updates

**Features:**
- Git-backed configuration repository
- Environment profile support
- Configuration encryption capability
- Health endpoints for monitoring

---

### Zipkin Tracing (Port 9411)
**Responsibilities:**
- Distributed tracing across all services
- Request correlation and tracking
- Performance monitoring and analysis
- Service dependency mapping

**Features:**
- Trace collection and storage
- Service map visualization
- Latency and error tracking
- Integration with multiple services

## Data Flow Examples

### Example 1: Employee Lookup
```
1. External Client → GET http://localhost:9090/employee-service/employee/1000
2. Gateway → Routes to Employee Service (Port 8080)
3. Employee Service → Query H2 Database for Employee ID 1000
4. Employee Service → Return Employee Data
5. Gateway → Return Response to External Client

Trace Flow: Client → Gateway → Employee Service → Database → Employee Service → Gateway → Client
```

### Example 2: Payroll Creation
```
1. External Client → POST http://localhost:9090/employee-payroll-service/employee/1000/role/Dev
2. Gateway → Routes to Employee Payroll Service (Port 8082)
3. Payroll Service → Feign Call to Employee Service (Port 8080)
4. Employee Service → Return Employee Data for ID 1000
5. Payroll Service → Feign Call to Role Service (Port 8081)
6. Role Service → Return Role Data for "Dev"
7. Payroll Service → Combine Data and Save to Database
8. Payroll Service → Return Payroll Record
9. Gateway → Return Response to External Client

Trace Flow: Client → Gateway → Payroll → Employee Service → Database
                          ↓
                          ↓
                          → Role Service → Database
```

## Technology Stack

### Core Technologies
- **Spring Boot 3.2.5** - Application framework
- **Spring Cloud 2023.0.1** - Microservices framework
- **Java 17** - Programming language
- **Maven** - Build tool
- **H2 Database** - In-memory data storage
- **SpringDoc OpenAPI** - API documentation

### Communication Technologies
- **Spring Cloud Gateway** - API gateway and routing
- **Netflix Eureka** - Service discovery
- **Spring Cloud OpenFeign** - Service-to-service communication
- **Spring Cloud LoadBalancer** - Client-side load balancing
- **Resilience4j** - Circuit breaker patterns

### Observability Technologies
- **Spring Cloud Config** - Configuration management
- **Micrometer Tracing** - Distributed tracing
- **Zipkin** - Trace collection and analysis
- **Spring Boot Actuator** - Application monitoring

## Deployment Patterns

### Service Registration Pattern
```
1. Service starts → Reads configuration from Config Server
2. Service registers with Eureka → Service discovery enabled
3. Eureka updates service registry → Other services can discover
4. Health checks performed → Service availability monitoring
5. Load balancer distributes requests → Multiple instances support
```

### Configuration Management Pattern
```
1. Config Server starts → Loads configuration from Git repository
2. Services request configuration → On startup and refresh
3. Environment-specific configs applied → dev/prod/test profiles
4. Dynamic updates possible → Configuration changes without restart
```

### API Gateway Pattern
```
1. Single entry point → All external requests through Gateway
2. Path-based routing → /service-name/** routes to appropriate service
3. Load balancing → Multiple service instances supported
4. Cross-cutting concerns → Security, logging, monitoring at gateway
```

## Scaling Considerations

### Horizontal Scaling
- **Stateless Services** - All services designed for horizontal scaling
- **Load Balancer Ready** - Multiple instances can be registered
- **Database Independence** - Each service has own database
- **Configuration Externalized** - Environment-specific scaling

### Vertical Scaling
- **Resource Allocation** - Memory and CPU per service
- **Database Scaling** - Separate databases per service
- **Configuration Tuning** - Service-specific optimization

## Security Architecture

### Current State (Development)
- **No Authentication** - All endpoints publicly accessible
- **No Authorization** - No role-based access control
- **HTTPS Not Configured** - HTTP only in development
- **CORS Not Restricted** - Cross-origin requests allowed

### Production Security Recommendations
- **Spring Security Integration** - JWT/OAuth2 implementation
- **API Gateway Security** - Centralized security policies
- **Service-to-Service Security** - Mutual TLS authentication
- **Network Security** - VPC, security groups, firewalls

## Monitoring & Observability

### Health Monitoring
```
Eureka Dashboard: http://localhost:8761/
Service Health: /actuator/health
Application Metrics: /actuator/metrics
Environment Info: /actuator/info
```

### Distributed Tracing
```
Zipkin Dashboard: http://localhost:9411/
Trace Search: Request correlation across services
Service Map: Visual representation of service dependencies
Performance Analysis: Latency and error rate tracking
```

### Logging Strategy
```
Structured Logging: JSON format with correlation IDs
Log Aggregation: Centralized log collection
Log Levels: Environment-specific (DEBUG/INFO/ERROR)
Audit Logging: Security and business events
```

## Failure Scenarios & Recovery

### Circuit Breaker Pattern
```
Employee Service:
- @CircuitBreaker on fault-tolerance endpoint
- Fallback method returns default data
- Automatic recovery when service recovers

Employee Payroll Service:
- Feign clients with built-in circuit breaking
- Graceful degradation when dependencies fail
- Retry patterns with backoff strategy
```

### Service Discovery Failure
```
If Eureka Unavailable:
- Services use cached configurations
- Local fallback configurations available
- Manual service URL configuration possible
- Graceful degradation with limited functionality
```

### Database Failure
```
H2 Database Issues:
- Connection pool management
- Retry mechanisms with exponential backoff
- Circuit breaker for database operations
- Health checks and automatic recovery
```

## Evolution Path

### Current Architecture (Spring Boot 3.x)
```
✅ Modern Spring Boot 3.2.5
✅ Jakarta EE instead of javax
✅ Reactive patterns with Spring Cloud Gateway
✅ Modern circuit breaker (Resilience4j)
✅ Enhanced observability (Micrometer)
✅ Native compilation support
✅ Improved security model
```

### Migration Benefits
```
🔄 Performance Improvements
🔄 Enhanced Security Features
🔄 Better Observability
🔄 Cloud-Native Support
🔄 Modern Dependency Management
🔄 Improved Developer Experience
```

This flow diagram provides a comprehensive view of the microservices architecture, communication patterns, and operational considerations for the upgraded MSA project.
