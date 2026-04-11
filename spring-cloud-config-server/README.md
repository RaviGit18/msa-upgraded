# Spring Cloud Config Server

Centralized configuration management server for the MSA microservices ecosystem.

## Overview

The Spring Cloud Config Server provides a centralized location for managing configuration properties across all microservices. It supports multiple backend storage options and provides version-controlled configuration management.

## Features

- Centralized configuration management
- Multiple backend storage options (Git, SVN, Vault, JDBC)
- Environment-specific configurations
- Real-time configuration updates
- Configuration encryption and decryption
- Version control integration
- RESTful API for configuration access
- Integration with Spring Cloud Bus for dynamic updates

## Technology Stack

- **Spring Boot 1.5.2**
- **Spring Cloud Config Server** - Configuration server
- **Spring Cloud Bus** - Event propagation (optional)
- **Spring Boot Actuator** - Monitoring endpoints
- **Spring Boot DevTools** - Development tools

## Configuration

### Application Properties
```properties
server.port=8888
spring.application.name=spring-cloud-config-server

# Git Backend Configuration
spring.cloud.config.server.git.uri=https://github.com/your-repo/config-repo
spring.cloud.config.server.git.search-paths=config
spring.cloud.config.server.git.clone-on-start=true
spring.cloud.config.server.git.default-label=main

# Native Backend (File System)
# spring.cloud.config.server.native.search-locations=file:./config
```

### Git Repository Structure
```
config-repo/
├── application.yml              # Default configuration
├── application-dev.yml          # Development environment
├── application-prod.yml         # Production environment
├── employee-service.yml         # Service-specific config
├── employee-service-dev.yml     # Service dev config
├── employee-payroll-service.yml # Payroll service config
└── role-service.yml             # Role service config
```

## API Endpoints

### Configuration Access

#### Get Default Configuration
```http
GET http://localhost:8888/{application}/{profile}/{label}
```

Examples:
```http
GET http://localhost:8888/employee-service/default
GET http://localhost:8888/employee-service/dev
GET http://localhost:8888/employee-service/prod/main
```

#### Get Property File
```http
GET http://localhost:8888/{application}-{profile}.yml
```

#### Get Environment Information
```http
GET http://localhost:8888/{application}/{profile}
```

### Management Endpoints

#### Health Check
```http
GET http://localhost:8888/health
```

#### Application Info
```http
GET http://localhost:8888/info
```

#### Refresh Configuration
```http
POST http://localhost:8888/refresh
```

#### Environment Properties
```http
GET http://localhost:8888/env
```

## Configuration Examples

### Application Configuration (application.yml)
```yaml
# Default configuration for all services
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    
  h2:
    console:
      enabled: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,refresh
```

### Service-Specific Configuration (employee-service.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: employee-service
  jpa:
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    com.payroll.microservices.employee: DEBUG
    org.hibernate.SQL: DEBUG
```

### Environment-Specific Configuration (employee-service-dev.yml)
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    com.payroll.microservices.employee: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

management:
  endpoints:
    web:
      exposure:
        include: "*"
```

## Backend Storage Options

### Git Backend (Recommended)
```properties
spring.cloud.config.server.git.uri=https://github.com/config-repo
spring.cloud.config.server.git.search-paths=config
spring.cloud.config.server.git.username={username}
spring.cloud.config.server.git.password={password}
spring.cloud.config.server.git.clone-on-start=true
spring.cloud.config.server.git.timeout=10
```

### Native File System Backend
```properties
spring.profiles.active=native
spring.cloud.config.server.native.search-locations=file:./config
```

### JDBC Backend
```properties
spring.cloud.config.server.jdbc.sql=SELECT prop_key, prop_value FROM properties WHERE application=? AND profile=? AND label=?
spring.datasource.url=jdbc:mysql://localhost:3306/config_db
spring.datasource.username=config_user
spring.datasource.password=config_pass
```

### Vault Backend
```properties
spring.cloud.config.server.vault.host=vault-server
spring.cloud.config.server.vault.port=8200
spring.cloud.config.server.vault.scheme=https
spring.cloud.config.server.vault.token={vault-token}
```

## Running the Server

### Prerequisites
- Java 8+
- Maven 3.3+
- Git repository (or chosen backend)

### Development Mode
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package
java -jar target/spring-cloud-config-server-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/spring-cloud-config-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Client Configuration

### Bootstrap Properties (bootstrap.yml)
```yaml
spring:
  application:
    name: employee-service
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
```

### Maven Dependency
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

## Configuration Encryption

### Enable Encryption
```properties
encrypt.key=my-secret-key
```

### Encrypt Values
```bash
# Encrypt a value
curl -X POST http://localhost:8888/encrypt -d my-secret-value

# Decrypt a value
curl -X POST http://localhost:8888/decrypt -d {encrypted-value}
```

### Encrypted Configuration
```yaml
database:
  password: '{cipher}AQABCD1234567890...'
```

## Dynamic Configuration Updates

### Spring Cloud Bus Integration
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

### Refresh Configuration
```bash
# Refresh single instance
POST http://localhost:8080/refresh

# Refresh all instances via bus
POST http://localhost:8888/bus/refresh

# Refresh specific service
POST http://localhost:8888/bus/refresh?destination=employee-service:**
```

## Monitoring and Management

### Health Check Response
```json
{
  "status": "UP",
  "configServer": {
    "status": "UP",
    "repositories": {
      "config-repo": {
        "status": "UP",
        "sources": ["application.yml", "employee-service.yml"]
      }
    }
  }
}
```

### Environment Information
```bash
curl http://localhost:8888/env
```

### Configuration Properties
```bash
curl http://localhost:8888/env/configserver:configserver:health
```

## Best Practices

### Repository Organization
1. **Separate repositories** for different environments
2. **Version control** for all configuration changes
3. **Branch strategy** for environment management
4. **Access control** for configuration repository

### Configuration Structure
1. **Common properties** in `application.yml`
2. **Environment-specific** in `application-{profile}.yml`
3. **Service-specific** in `{service-name}.yml`
4. **Secrets** encrypted and properly managed

### Security Considerations
1. **Encrypt sensitive data**
2. **Use HTTPS** for production
3. **Implement access controls**
4. **Regular security audits**

## Troubleshooting

### Common Issues

1. **Configuration Not Found**
   ```bash
   # Check repository structure
   curl http://localhost:8888/employee-service/default
   
   # Verify file names and paths
   # Check Git repository access
   ```

2. **Client Connection Issues**
   ```bash
   # Test server connectivity
   curl http://localhost:8888/health
   
   # Check client bootstrap configuration
   # Verify network connectivity
   ```

3. **Encryption Issues**
   ```bash
   # Test encryption
   curl -X POST http://localhost:8888/encrypt -d test
   
   # Check key configuration
   # Verify cipher configuration
   ```

### Debug Logging
```properties
logging.level.org.springframework.cloud.config=DEBUG
logging.level.org.springframework.cloud.bus=DEBUG
```

## Performance Optimization

### Caching Configuration
```properties
spring.cloud.config.server.cache.enabled=true
spring.cloud.config.server.cache.ttl=60s
```

### Connection Pooling
```properties
spring.cloud.config.server.git.timeout=10
spring.cloud.config.server.git.force-pull=true
```

## High Availability

### Multiple Config Servers
```yaml
spring:
  cloud:
    config:
      uri: http://config-server1:8888,http://config-server2:8888
      retry:
        initial-interval: 1000
        max-attempts: 6
```

### Load Balancer Configuration
```nginx
upstream config-servers {
    server config-server1:8888;
    server config-server2:8888;
}

server {
    listen 8888;
    location / {
        proxy_pass http://config-servers;
    }
}
```

## Dependencies

Key dependencies and their versions:
- Spring Boot Starter Web
- Spring Boot Starter Actuator
- Spring Cloud Config Server
- Spring Cloud Starter Bus AMQP (optional)
- Spring Boot DevTools
