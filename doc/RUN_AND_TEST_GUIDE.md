# MSA-Upgraded Project - Step-by-Step Run and Test Guide

## Overview
This guide provides detailed instructions for running and testing the MSA-Upgraded project with Spring Boot 3.2.5, modern dependencies, and Swagger documentation.

## Prerequisites

### System Requirements
- **Java 17+** (required for Spring Boot 3.x)
- **Gradle 8.0+** (for building the project, or use provided wrapper)
- **Git** (for version control)
- **Modern IDE** (IntelliJ IDEA, VS Code, or Eclipse)

### Environment Setup
```bash
# Verify Java version
java -version

# Verify Gradle version
./gradlew --version

# Verify Git version
git --version
```

### Port Availability Check
Ensure the following ports are **available** before starting:
| Port | Service | Purpose |
|-------|----------|---------|
| 8761 | Eureka Naming Server | Service Discovery |
| 8888 | Spring Cloud Config Server | Configuration Management |
| 8080 | Employee Service | Employee Management |
| 8081 | Role Service | Role Management |
| 8082 | Employee Payroll Service | Payroll Management |
| 9090 | Spring Cloud Gateway Server (Gateway) | API Gateway |

**Check port availability:**
```bash
# Windows
netstat -ano | findstr ":8761"
netstat -ano | findstr ":8888"
netstat -ano | findstr ":8080"
netstat -ano | findstr ":8081"
netstat -ano | findstr ":8082"
netstat -ano | findstr ":9090"

# Or use PowerShell
Get-NetTCPConnection -LocalPort 8761,8888,8080,8081,8082,9090
```

### Pre-Startup Validation Checklist

#### 1. Project Structure Verification
```bash
cd E:\learning\workspaces\workspaces-0\msa-upgraded
dir /B

# Expected structure:
# msa-upgraded/
# ├── pom.xml
# ├── employee-service/
# ├── employee-payroll-service/
# ├── role-service/
# ├── eureka-naming-server/
# ├── spring-cloud-config-server/
# ├── spring-cloud-gateway-server/
# └── micrometer-tracing/
```

#### 2. Gradle Dependencies Check
```bash
# Clean and compile the entire project
./gradlew clean compileJava

# Check for any compilation errors
./gradlew dependencies
```

#### 3. Configuration Files Validation
Verify all `application.yml` files exist:
```bash
dir /S application.yml
```

#### 4. Database Setup
The project uses **H2 in-memory databases** - no external database setup required.

## ⚠️ **IMPORTANT: Correct Startup Order**

The order of starting applications is **CRITICAL** for proper dependency resolution. Here's the **CORRECT** sequence:
<img width="1103" height="131" alt="image" src="https://github.com/user-attachments/assets/c52a9d19-bf12-47e7-8b31-51d78498d410" />


### **Phase 1: Infrastructure Services**
1. **Eureka Naming Server** (Port 8761) - *Foundation for service discovery*
2. **Spring Cloud Config Server** (Port 8888) - *Centralized configuration*

### **Phase 2: Optional Services**
3. **Zipkin Tracing** (Port 9411) - *Optional distributed tracing*
4. **Zuul Edge Server (Gateway)** (Port 9090) - *Routes to microservices*

### **Phase 3: Business Services**
5. **Employee Service** (Port 8080) - *Core business service*
6. **Role Service** (Port 8081) - *Role management service*
7. **Employee Payroll Service** (Port 8082) - *Orchestrates communication*

### **Why This Order Matters:**

```
Employee Payroll Service
├── Employee Service (via Feign client)
├── Role Service (via Feign client)  
├── Zuul Gateway (for routing)
├── Eureka Server (service discovery)
├── Config Server (configuration)
└── Zipkin (tracing - optional)
```

- **Eureka MUST be first** - No service can register without discovery server
- **Config Server MUST be second** - Services need configuration to start properly
- **Business Services CAN run in parallel** - After infrastructure is ready
- **Gateway MUST be after business services** - Routes to running services
- **Payroll Service MUST be last** - Depends on Employee + Role services

### **❌ INCORRECT Order (Common Mistakes):**
Starting Employee Payroll Service before Employee/Role services will cause:
- Feign client connection failures
- Service registration errors
- Application startup failures

### **✅ CORRECT Order (Recommended):**
1. Eureka → 2. Config → 3. Business Services → 4. Gateway

## Step-by-Step Startup Process

### Step 1: Start Eureka Naming Server (Service Discovery)

**Why First?** Eureka is the foundation for service discovery. All other services depend on it to register themselves and discover other services.

```bash
cd E:\learning\workspaces\workspaces-0\msa-upgraded\eureka-naming-server
../gradlew bootRun
```

**Expected Output:**
```
Tomcat started on port 8761 (http)
Started EurekaNamingServerApplication in X seconds
```

**Validation:**
```bash
# Access Eureka Dashboard
curl http://localhost:8761
# Or open in browser: http://localhost:8761
```

**Success Indicators:**
- ✅ Eureka dashboard loads successfully
- ✅ No startup errors in console
- ✅ Port 8761 is listening

---

### Step 2: Start Spring Cloud Config Server

**Why Second?** Configuration server provides centralized configuration. Services need it to load their properties.

```bash
cd E:\learning\workspaces\workspaces-0\msa-upgraded\spring-cloud-config-server
../gradlew bootRun
```

**Expected Output:**
```
Tomcat started on port 8888 (http)
Started SpringCloudConfigServerApplication in X seconds
```

**Validation:**
```bash
# Test config server health
curl http://localhost:8888/actuator/health
```

**Success Indicators:**
- ✅ Server starts on port 8888
- ✅ Actuator health endpoint returns UP
- ✅ No configuration errors

---

### Step 3: Start Employee Service

**Why Third?** Core business service that provides employee data to other services.

```bash
cd E:\learning\workspaces\workspaces-0\msa-upgraded\employee-service
../gradlew bootRun
```

**Expected Output:**
```
Tomcat started on port 8080 (http)
DiscoveryClient_EMPLOYEE-SERVICE - registration status: 204
Started EmployeeServiceApplication in X seconds
```

**Validation:**
```bash
# Check service registration in Eureka
curl http://localhost:8761/eureka/apps

# Test basic endpoint
curl http://localhost:8080/employee/1000
```

**Success Indicators:**
- ✅ Service registers with Eureka
- ✅ Swagger UI accessible at http://localhost:8080/swagger-ui.html
- ✅ Sample data loaded from data.sql

---

### Step 4: Start Role Service

**Why Fourth?** Provides role information used by payroll service.

```bash
cd E:\learning\workspaces\workspaces-0\msa-upgraded\role-service
../gradlew bootRun
```

**Expected Output:**
```
Tomcat started on port 8081 (http)
DiscoveryClient_ROLE-SERVICE - registration status: 204
Started RoleServiceApplication in X seconds
```

**Validation:**
```bash
# Test role endpoint
curl http://localhost:8081/role/Dev
```

**Success Indicators:**
- ✅ Service registers with Eureka
- ✅ Swagger UI accessible at http://localhost:8081/swagger-ui.html
- ✅ Role data returned correctly

---

### Step 5: Start Employee Payroll Service

**Why Fifth?** Orchestrates communication between employee and role services.

```bash
cd E:\learning\workspaces\workspaces-0\msa-upgraded\employee-payroll-service
../gradlew bootRun
```

**Expected Output:**
```
Tomcat started on port 8082 (http)
DiscoveryClient_EMPLOYEE-PAYROLL-SERVICE - registration status: 204
Started EmployeePayrollServiceApplication in X seconds
```

**Validation:**
```bash
# Test inter-service communication
curl -X POST http://localhost:8082/employee/1000/role/Dev
```

**Success Indicators:**
- ✅ Service registers with Eureka
- ✅ Feign clients connect to other services
- ✅ Swagger UI accessible at http://localhost:8082/swagger-ui.html

---

### Step 6: Start Zuul Edge Server (Spring Cloud Gateway)

**Why Last?** Gateway routes all external requests and provides single entry point.

```bash
cd E:\learning\workspaces\workspaces-0\msa-upgraded\spring-cloud-gateway-server
../gradlew bootRun
```

**Expected Output:**
```
Netty started on port 9090
DiscoveryClient_SPRING-CLOUD-GATEWAY-SERVER - registration status: 204
Started SpringCloudGatewayServerApplication in X seconds
```

**Validation:**
```bash
# Test gateway routing to employee service
curl http://localhost:9090/employee-service/employee/1000

# Test gateway routing to role service
curl http://localhost:9090/role-service/role/Dev
```

**Success Indicators:**
- ✅ Gateway registers with Eureka
- ✅ All service routes working through gateway
- ✅ Load balancing operational

## Testing with Swagger

### Accessing Swagger UI

#### 1. Employee Service
- **URL**: http://localhost:8080/swagger-ui.html
- **Available Endpoints**:
  - `GET /employee/{empId}` - Get employee by ID
  - `GET /employee/fault-tolerance` - Test circuit breaker

#### 2. Role Service
- **URL**: http://localhost:8081/swagger-ui.html
- **Available Endpoints**:
  - `GET /role/{roleName}` - Get role by name

#### 3. Employee Payroll Service
- **URL**: http://localhost:8082/swagger-ui.html
- **Available Endpoints**:
  - `POST /employee/{empId}/role/{roleName}` - Create payroll record

#### 4. Spring Cloud Gateway
- **URL**: http://localhost:9090/swagger-ui.html
- **Routes all services through gateway**

### Step-by-Step API Testing

#### Test 1: Employee Service Direct
1. Open http://localhost:8080/swagger-ui.html
2. Expand "employee-controller" section
3. Click "GET /employee/{empId}"
4. Click "Try it out"
5. Enter `1000` for empId
6. Click "Execute"
7. **Expected Response**: Employee data with firstName, lastName, empId, dateOfJoining

#### Test 2: Role Service Direct
1. Open http://localhost:8081/swagger-ui.html
2. Expand "role-controller" section
3. Click "GET /role/{roleName}"
4. Click "Try it out"
5. Enter `Dev` for roleName
6. Click "Execute"
7. **Expected Response**: Role data with roleId, roleName, description

#### Test 3: Employee Service Circuit Breaker
1. In employee-service Swagger UI
2. Click "GET /employee/fault-tolerance"
3. Click "Execute"
4. **Expected Response**: Fallback data with empId: 101
5. **Validates**: Resilience4j circuit breaker working

#### Test 4: Payroll Service Integration
1. Open http://localhost:8082/swagger-ui.html
2. Expand "employee-payroll-controller"
3. Click "POST /employee/{empId}/role/{roleName}"
4. Click "Try it out"
5. Enter `1000` for empId and `Dev` for roleName
6. Click "Execute"
7. **Expected Response**: Combined payroll data from employee and role services

#### Test 5: Gateway Routing
1. Open http://localhost:9090/swagger-ui.html
2. Test all endpoints through gateway:
   - `GET /employee-service/employee/1000`
   - `GET /role-service/role/Dev`
3. **Expected**: Same responses as direct service calls

## Troubleshooting Guide

### Common Issues and Solutions

#### 1. Port Already in Use
**Problem**: `Address already in use: bind` error
**Solution**:
```bash
# Find process using port
netstat -ano | findstr ":8080"

# Kill process (replace PID)
taskkill /PID <PID> /F

# Or change port in application.properties
server.port=8081
```

#### 2. Service Registration Fails
**Problem**: Service not appearing in Eureka dashboard
**Check**:
- Eureka server is running on 8761
- `eureka.client.service-url.default-zone` is correct
- Firewall not blocking connections

#### 3. Feign Client Connection Errors
**Problem**: 500 errors when calling other services
**Check**:
- Target service is running and registered
- Service names match exactly
- Load balancer configuration correct

#### 4. Database Errors
**Problem**: Table not found errors
**Solution**:
```properties
# Add to application.properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.defer-datasource-initialization=true
```

#### 5. Spring Cloud Configuration Issues
**Problem**: Config server connection errors
**Check**:
```properties
# Ensure this property exists
spring.config.import=optional:configserver:http://localhost:8888
```

## Production Considerations

### Security
- Add Spring Security for authentication
- Configure HTTPS
- Set up API key management
- Implement rate limiting in gateway

### Monitoring
- Enable Spring Boot Actuator endpoints
- Set up centralized logging
- Configure health checks
- Add metrics collection

### Database
- Replace H2 with PostgreSQL/MySQL
- Set up database connections pools
- Configure data migration scripts
- Add database backup strategies

### Deployment
- Containerize with Docker
- Set up Kubernetes deployment
- Configure environment-specific properties
- Implement blue-green deployment

## Quick Start Commands

### All Services (Sequential)
```bash
# Terminal 1 - Eureka
cd eureka-naming-server && ../gradlew bootRun

# Terminal 2 - Config Server
cd spring-cloud-config-server && ../gradlew bootRun

# Terminal 3 - Employee Service
cd employee-service && ../gradlew bootRun

# Terminal 4 - Role Service
cd role-service && ../gradlew bootRun

# Terminal 5 - Payroll Service
cd employee-payroll-service && ../gradlew bootRun

# Terminal 6 - Gateway
cd spring-cloud-gateway-server && ../gradlew bootRun
```

### Health Check Script
```bash
# Save as health-check.bat
@echo off
echo Checking service health...
echo.
echo Eureka Server:
curl -s http://localhost:8761 | findstr "Eureka" && echo OK || echo FAIL
echo.
echo Employee Service:
curl -s http://localhost:8080/employee/1000 | findstr "AAA1" && echo OK || echo FAIL
echo.
echo Role Service:
curl -s http://localhost:8081/role/Dev | findstr "Developer" && echo OK || echo FAIL
echo.
echo Gateway:
curl -s http://localhost:9090/employee-service/employee/1000 | findstr "AAA1" && echo OK || echo FAIL
```

This comprehensive guide ensures successful startup and testing of all microservices with proper validation at each step.
