# Eureka Naming Server

Service discovery and registration server for the MSA microservices ecosystem.

## Overview

The Eureka Naming Server provides service discovery capabilities, allowing microservices to register themselves and discover other services in the ecosystem. It's a critical component for building resilient and scalable microservices architectures.

## Features

- Service registration and discovery
- Health monitoring of registered services
- Automatic service instance removal on failure
- Dashboard for monitoring registered services
- Client-side load balancing integration
- Peer-to-peer replication support
- Self-preservation mode for network partitions

## Technology Stack

- **Spring Boot 1.5.2**
- **Spring Cloud Eureka Server** - Service discovery server
- **Spring Cloud Config** - External configuration
- **Spring Boot Actuator** - Monitoring endpoints
- **Spring Boot DevTools** - Development tools

## Configuration

### Application Properties
```properties
server.port=8761
spring.application.name=eureka-naming-server

# Eureka Server Configuration
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.server.enable-self-preservation=true
eureka.server.eviction-interval-timer-in-ms=5000

# Dashboard Configuration
eureka.dashboard.path=/dashboard
eureka.dashboard.enabled=true
```

### Server Configuration
- **Port**: 8761 (standard Eureka port)
- **Registration**: Disabled (server doesn't register with itself)
- **Fetch Registry**: Disabled (server doesn't fetch registry)
- **Self-Preservation**: Enabled (protects against network partitions)

## Access Points

### Eureka Dashboard
```http
http://localhost:8761/
```

### REST Endpoints

#### Get Registry
```http
GET http://localhost:8761/eureka/apps
```

#### Get Application by Name
```http
GET http://localhost:8761/eureka/apps/{application-name}
```

#### Get Instance by ID
```http
GET http://localhost:8761/eureka/apps/{application-name}/{instance-id}
```

#### Health Check
```http
GET http://localhost:8761/health
```

#### Application Info
```http
GET http://localhost:8761/info
```

## Running the Server

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
java -jar target/eureka-naming-server-0.0.1-SNAPSHOT.jar
```

## Dashboard Features

### Service Registry View
- Lists all registered applications
- Shows instance details (host, port, status)
- Real-time updates
- Service health indicators

### Instance Information
- Instance ID and hostname
- Port configuration
- Health check URL
- Status page URL
- Metadata information
- Registration and renewal timestamps

### System Status
- Server uptime
- Environment information
- Memory usage
- Thread information

## Service Registration Process

### Client Registration
1. Microservice starts up
2. Registers with Eureka Server
3. Sends periodic heartbeats (default: 30 seconds)
4. Eureka maintains registry of healthy instances

### Health Monitoring
- Heartbeat mechanism for health checking
- Automatic instance removal after timeout (default: 90 seconds)
- Self-preservation mode during network issues
- Custom health check endpoints

## Configuration Options

### Server Configuration
```properties
# Port Configuration
server.port=8761

# Eureka Server Settings
eureka.server.enable-self-preservation=true
eureka.server.eviction-interval-timer-in-ms=5000
eureka.server.renewal-percent-threshold=0.85
eureka.server.renewal-threshold-update-interval-ms=15000

# Response Cache
eureka.server.response-cache-auto-expiration-in-seconds=180
eureka.server.response-cache-update-interval-ms=30000

# Registry Sync
eureka.server.registry-sync-retries=0
eureka.server.registry-sync-retry-wait-ms=500
eureka.server.max-threads-for-status-replication=1
```

### Client Configuration (for services)
```properties
# Service Registration
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Health Check
eureka.client.healthcheck.enabled=true
eureka.instance.lease-renewal-interval-in-seconds=30
eureka.instance.lease-expiration-duration-in-seconds=90

# Instance Metadata
eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}
eureka.instance.prefer-ip-address=true
```

## High Availability Setup

### Peer-to-Peer Replication
```properties
# Node 1 Configuration
eureka.instance.hostname=peer1
eureka.client.service-url.defaultZone=http://peer2:8761/eureka/

# Node 2 Configuration
eureka.instance.hostname=peer2
eureka.client.service-url.defaultZone=http://peer1:8761/eureka/
```

### Load Balancer Configuration
```yaml
# docker-compose.yml example
version: '3'
services:
  eureka1:
    image: eureka-server
    ports:
      - "8761:8761"
    environment:
      - EUREKA_INSTANCE_HOSTNAME=eureka1
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka2:8761/eureka/
  
  eureka2:
    image: eureka-server
    ports:
      - "8762:8761"
    environment:
      - EUREKA_INSTANCE_HOSTNAME=eureka2
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka1:8761/eureka/
```

## Monitoring and Management

### Actuator Endpoints
```http
# Health Status
GET http://localhost:8761/health

# Application Info
GET http://localhost:8761/info

# Environment Properties
GET http://localhost:8761/env

# Metrics
GET http://localhost:8761/metrics

# Registry Dump
GET http://localhost:8761/eureka/registry
```

### Dashboard Monitoring
- Real-time service registration status
- Instance health indicators
- Network partition detection
- Self-preservation mode status

## Troubleshooting

### Common Issues

1. **Services Not Registering**
   ```bash
   # Check Eureka server is running
   curl http://localhost:8761/health
   
   # Verify client configuration
   # Check service-url.defaultZone property
   # Ensure network connectivity
   ```

2. **Instances Showing as DOWN**
   ```bash
   # Check service health endpoints
   curl http://service-host:port/health
   
   # Verify heartbeat configuration
   # Check lease-renewal-interval-in-seconds
   # Review firewall settings
   ```

3. **Self-Preservation Mode**
   ```bash
   # Monitor dashboard for self-pervation status
   # Check network connectivity between services
   # Review renewal threshold configuration
   ```

### Debug Logging
```properties
logging.level.com.netflix.eureka=DEBUG
logging.level.com.netflix.discovery=DEBUG
```

## Performance Tuning

### Memory Optimization
```properties
# JVM Settings
-Xms512m -Xmx1024m

# Eureka Cache Settings
eureka.server.response-cache-auto-expiration-in-seconds=180
eureka.server.use-read-only-response-cache=true
```

### Network Optimization
```properties
# Connection Pool Settings
eureka.client.eureka-server-connect-timeout-seconds=5
eureka.client.eureka-server-read-timeout-seconds=8
eureka.client.eureka-server-total-connections=200
eureka.client.eureka-server-total-connections-per-host=50
```

## Security Considerations

### Basic Authentication
```properties
# Enable security
spring.security.user.name=admin
spring.security.user.password=password

# Secure Eureka endpoints
eureka.client.service-url.defaultZone=http://admin:password@localhost:8761/eureka/
```

### Network Security
- Use HTTPS in production
- Implement firewall rules
- Configure proper access controls
- Monitor for unauthorized access

## Best Practices

1. **Production Deployment**
   - Use peer-to-peer replication for HA
   - Configure proper resource limits
   - Implement monitoring and alerting
   - Use load balancer for client access

2. **Configuration Management**
   - Externalize configuration
   - Use environment-specific settings
   - Implement configuration versioning
   - Monitor configuration changes

3. **Monitoring**
   - Track registry size and growth
   - Monitor heartbeat success rates
   - Alert on service unavailability
   - Log important events

## Integration Examples

### Service Registration Example
```java
@SpringBootApplication
@EnableEurekaClient
public class EmployeeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeServiceApplication.class, args);
    }
}
```

### Custom Health Check
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Custom health check logic
        return Health.up().build();
    }
}
```

## Dependencies

Key dependencies and their versions:
- Spring Boot Starter Web
- Spring Boot Starter Actuator
- Spring Cloud Starter Eureka Server
- Spring Cloud Starter Config
- Spring Boot DevTools
