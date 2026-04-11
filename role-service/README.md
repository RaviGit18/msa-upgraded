# Role Service

A microservice for managing user roles and permissions within the MSA ecosystem.

## Overview

The Role Service provides comprehensive role-based access control (RBAC) functionality, managing user roles, permissions, and access control policies across the microservices architecture.

## Features

- Role management (CRUD operations)
- Permission management
- User-role assignment
- Role-permission mapping
- Integration with Eureka for service discovery
- Configuration management via Spring Cloud Config
- Distributed tracing with Spring Cloud Sleuth
- H2 in-memory database with web console
- RESTful API endpoints
- Access control validation

## Technology Stack

- **Spring Boot 1.5.2**
- **Spring Data JPA** - Database operations
- **Spring Web** - REST API framework
- **Spring Cloud Config** - External configuration
- **Spring Cloud Eureka** - Service discovery
- **Spring Cloud Sleuth** - Distributed tracing
- **H2 Database** - In-memory database
- **Spring Boot DevTools** - Development tools

## Configuration

### Application Properties
```properties
server.port=8082
spring.application.name=role-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Database Configuration
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)
- **Console**: http://localhost:8082/h2-console

## API Endpoints

### Role Management

#### Get All Roles
```http
GET http://localhost:8082/roles
```

#### Get Role by ID
```http
GET http://localhost:8082/roles/{id}
```

#### Create Role
```http
POST http://localhost:8082/roles
Content-Type: application/json

{
  "name": "ADMIN",
  "description": "Administrator role with full access",
  "active": true
}
```

#### Update Role
```http
PUT http://localhost:8082/roles/{id}
Content-Type: application/json

{
  "name": "ADMIN",
  "description": "Updated administrator role description",
  "active": true
}
```

#### Delete Role
```http
DELETE http://localhost:8082/roles/{id}
```

### Permission Management

#### Get All Permissions
```http
GET http://localhost:8082/permissions
```

#### Get Permission by ID
```http
GET http://localhost:8082/permissions/{id}
```

#### Create Permission
```http
POST http://localhost:8082/permissions
Content-Type: application/json

{
  "name": "USER_READ",
  "description": "Read access to user data",
  "resource": "USER",
  "action": "READ"
}
```

### User-Role Assignment

#### Assign Role to User
```http
POST http://localhost:8082/users/{userId}/roles
Content-Type: application/json

{
  "roleId": 1
}
```

#### Get User Roles
```http
GET http://localhost:8082/users/{userId}/roles
```

#### Remove Role from User
```http
DELETE http://localhost:8082/users/{userId}/roles/{roleId}
```

### Role-Permission Assignment

#### Assign Permission to Role
```http
POST http://localhost:8082/roles/{roleId}/permissions
Content-Type: application/json

{
  "permissionId": 1
}
```

#### Get Role Permissions
```http
GET http://localhost:8082/roles/{roleId}/permissions
```

### Access Control

#### Check User Permission
```http
GET http://localhost:8082/users/{userId}/permissions/{permissionName}
```

#### Get User Permissions
```http
GET http://localhost:8082/users/{userId}/permissions
```

### Health and Monitoring

#### Health Check
```http
GET http://localhost:8082/health
```

#### Application Info
```http
GET http://localhost:8082/info
```

#### Metrics
```http
GET http://localhost:8082/metrics
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
java -jar target/role-service-0.0.1-SNAPSHOT.jar
```

## Database Schema

### Role Entity
```sql
CREATE TABLE role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  description VARCHAR(255),
  active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Permission Entity
```sql
CREATE TABLE permission (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  description VARCHAR(255),
  resource VARCHAR(100) NOT NULL,
  action VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### User Role Entity
```sql
CREATE TABLE user_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  assigned_by VARCHAR(100),
  UNIQUE KEY unique_user_role (user_id, role_id),
  FOREIGN KEY (role_id) REFERENCES role(id)
);
```

### Role Permission Entity
```sql
CREATE TABLE role_permission (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  granted_by VARCHAR(100),
  UNIQUE KEY unique_role_permission (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES role(id),
  FOREIGN KEY (permission_id) REFERENCES permission(id)
);
```

## Integration Points

### Service Discovery
- Registers with Eureka Server
- Enables other services to discover and communicate
- Supports load balancing across multiple instances

### Configuration Management
- Fetches configuration from Spring Cloud Config Server
- Supports environment-specific configurations
- Dynamic configuration updates

### Distributed Tracing
- Generates trace IDs for request tracking
- Integrates with Zipkin for visualization
- Cross-service request correlation

## Access Control Logic

### Permission Check Example
```java
public boolean hasPermission(Long userId, String permissionName) {
    List<String> userPermissions = getUserPermissions(userId);
    return userPermissions.contains(permissionName);
}

public List<String> getUserPermissions(Long userId) {
    return roleRepository.findPermissionsByUserId(userId)
        .stream()
        .map(Permission::getName)
        .collect(Collectors.toList());
}
```

### Role Assignment Logic
```java
public void assignRoleToUser(Long userId, Long roleId, String assignedBy) {
    // Validate role exists and is active
    Role role = roleRepository.findByIdAndActive(roleId, true)
        .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    
    // Check if user already has this role
    if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
        throw new RoleAlreadyAssignedException("User already has this role");
    }
    
    // Assign role
    UserRole userRole = new UserRole();
    userRole.setUserId(userId);
    userRole.setRoleId(roleId);
    userRole.setAssignedBy(assignedBy);
    userRoleRepository.save(userRole);
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
# Create role
curl -X POST http://localhost:8082/roles \
  -H "Content-Type: application/json" \
  -d '{"name":"USER","description":"Regular user role","active":true}'

# Create permission
curl -X POST http://localhost:8082/permissions \
  -H "Content-Type: application/json" \
  -d '{"name":"USER_READ","description":"Read user data","resource":"USER","action":"READ"}'

# Assign permission to role
curl -X POST http://localhost:8082/roles/1/permissions \
  -H "Content-Type: application/json" \
  -d '{"permissionId":1}'

# Check user permission
curl http://localhost:8082/users/123/permissions/USER_READ
```

## Development Notes

### Error Handling
- Global exception handling for consistent error responses
- Validation of role and permission data
- Proper HTTP status codes
- Detailed error messages for debugging

### Logging
```properties
# Enable debug logging for role service
logging.level.com.payroll.microservices.role.service=DEBUG

# Enable SQL logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Security Considerations
- Input validation for all API endpoints
- SQL injection prevention
- Authorization checks for role modifications
- Audit logging for role assignments

## Troubleshooting

### Common Issues

1. **Role Assignment Conflicts**
   - Check for duplicate user-role assignments
   - Verify role exists and is active
   - Review database constraints

2. **Permission Check Failures**
   - Verify user has required roles
   - Check role-permission mappings
   - Validate permission names

3. **Database Issues**
   - Access H2 console at http://localhost:8082/h2-console
   - Verify database schema and data
   - Check foreign key constraints

## Monitoring

### Actuator Endpoints
- `/health` - Service health status
- `/info` - Application information
- `/metrics` - Performance metrics
- `/trace` - Request traces
- `/env` - Environment properties

### Business Metrics
- Role assignment success rates
- Permission check performance
- User role distribution
- API response times

## Performance Optimization

### Database Optimization
```sql
-- Indexes for performance
CREATE INDEX idx_user_role_user_id ON user_role(user_id);
CREATE INDEX idx_role_permission_role_id ON role_permission(role_id);
CREATE INDEX idx_permission_name ON permission(name);
```

### Caching Strategy
```java
@Service
public class RoleService {
    
    @Cacheable(value = "userPermissions", key = "#userId")
    public List<String> getUserPermissions(Long userId) {
        // Implementation
    }
    
    @CacheEvict(value = "userPermissions", key = "#userId")
    public void assignRoleToUser(Long userId, Long roleId, String assignedBy) {
        // Implementation
    }
}
```

## Best Practices

1. **Role Design**
   - Follow principle of least privilege
   - Use descriptive role names
   - Keep role hierarchy simple
   - Regular role reviews

2. **Permission Management**
   - Granular permission design
   - Consistent naming conventions
   - Resource-action based permissions
   - Regular permission audits

3. **Security**
   - Secure role assignment endpoints
   - Audit all role changes
   - Implement role expiration
   - Regular security reviews

## Dependencies

Key dependencies and their versions:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Actuator
- Spring Cloud Starter Config
- Spring Cloud Starter Eureka
- Spring Cloud Starter Sleuth
- H2 Database
- Spring Boot DevTools
