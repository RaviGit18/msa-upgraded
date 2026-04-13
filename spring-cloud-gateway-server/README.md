# Spring Cloud Gateway Server

API Gateway server providing routing, load balancing, and cross-cutting concerns for the MSA-Upgraded microservices ecosystem.

## Overview

The Spring Cloud Gateway Server serves as the single entry point for all client requests, providing intelligent routing, load balancing, security, and monitoring capabilities across the microservices architecture.

## Features

- Dynamic routing and load balancing
- Request filtering and transformation
- API versioning support
- Rate limiting and throttling
- Authentication and authorization
- Request/response logging
- Circuit breaker integration
- Service discovery integration
- Cross-origin resource sharing (CORS)
- Request aggregation

## Technology Stack

- **Spring Boot 3.2.5**
- **Spring Cloud Gateway** - API Gateway
- **Spring Cloud Eureka** - Service discovery
- **Micrometer Tracing** - Distributed tracing
- **Spring Boot Actuator** - Monitoring endpoints
- **Spring Boot DevTools** - Development tools

## Configuration

### Application Properties
```properties
server.port=8765
spring.application.name=spring-cloud-gateway-server

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Zuul Configuration
zuul.ignored-services=*
zuul.prefix=/api
zuul.strip-prefix=true
zuul.add-host-header=true
zuul.sensitive-headers=Cookie,Set-Cookie

# Routes Configuration
zuul.routes.employee-service.path=/employees/**
zuul.routes.employee-service.service-id=employee-service
zuul.routes.employee-service.url=http://localhost:8080

zuul.routes.payroll-service.path=/payroll/**
zuul.routes.payroll-service.service-id=employee-payroll-service
zuul.routes.payroll-service.url=http://localhost:8081

zuul.routes.role-service.path=/roles/**
zuul.routes.role-service.service-id=role-service
zuul.routes.role-service.url=http://localhost:8082

# Hystrix Configuration
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=5000
```

## API Endpoints

### Service Routes

#### Employee Service Routes
```http
# Get all employees
GET http://localhost:8765/api/employees

# Get specific employee
GET http://localhost:8765/api/employees/{id}

# Create employee
POST http://localhost:8765/api/employees
```

#### Payroll Service Routes
```http
# Get payroll records
GET http://localhost:8765/api/payroll

# Calculate payroll
GET http://localhost:8765/api/payroll/calculate/{employeeId}
```

#### Role Service Routes
```http
# Get roles
GET http://localhost:8765/api/roles

# Get user permissions
GET http://localhost:8765/api/users/{userId}/permissions
```

### Management Endpoints

#### Health Check
```http
GET http://localhost:8765/health
```

#### Application Info
```http
GET http://localhost:8765/info
```

#### Routes Information
```http
GET http://localhost:8765/routes
```

#### Hystrix Dashboard
```http
GET http://localhost:8765/hystrix
```

#### Metrics
```http
GET http://localhost:8765/metrics
```

## Custom Filters

### Pre-Filter Example
```java
@Component
public class AuthenticationFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "pre";
    }
    
    @Override
    public int filterOrder() {
        return 1;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        
        // Add authentication header
        ctx.addZuulRequestHeader("X-User-ID", getUserIdFromToken(request));
        
        // Log request
        logger.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        
        return null;
    }
}
```

### Post-Filter Example
```java
@Component
public class ResponseFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "post";
    }
    
    @Override
    public int filterOrder() {
        return 1000;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        
        // Add custom headers
        response.setHeader("X-Gateway", "Zuul");
        response.setHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()));
        
        // Log response
        logger.info("Response Status: {}", response.getStatus());
        
        return null;
    }
}
```

### Error Filter Example
```java
@Component
public class ErrorFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "error";
    }
    
    @Override
    public int filterOrder() {
        return -1;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable throwable = ctx.getThrowable();
        
        logger.error("Zuul error occurred", throwable);
        
        // Custom error response
        ctx.setResponseStatusCode(500);
        ctx.setResponseBody("{\"error\": \"Internal server error\"}");
        ctx.getResponse().setContentType("application/json");
        
        return null;
    }
}
```

## Advanced Routing Configuration

### Dynamic Routing with Ribbon
```properties
# Ribbon Configuration for Employee Service
employee-service.ribbon.listOfServers=localhost:8080,localhost:8081
employee-service.ribbon.ServerListRefreshInterval=15000
employee-service.ribbon.ConnectTimeout=3000
employee-service.ribbon.ReadTimeout=5000
employee-service.ribbon.MaxTotalHttpConnections=200
employee-service.ribbon.MaxConnectionsPerHost=50
```

### Route-Specific Configuration
```properties
# Employee Service Route
zuul.routes.employee-service.path=/employees/**
zuul.routes.employee-service.service-id=employee-service
zuul.routes.employee-service.strip-prefix=true
zuul.routes.employee-service.sensitive-headers=Cookie,Authorization

# Custom Retry Configuration
zuul.routes.employee-service.retryable=true
zuul.routes.employee-service.max-retries=3
```

### CORS Configuration
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

## Rate Limiting

### Simple Rate Limiting Filter
```java
@Component
public class RateLimitingFilter extends ZuulFilter {
    
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastResetTime = new ConcurrentHashMap<>();
    
    private static final int RATE_LIMIT = 100; // requests per minute
    private static final long WINDOW_SIZE_MS = 60000; // 1 minute
    
    @Override
    public String filterType() {
        return "pre";
    }
    
    @Override
    public int filterOrder() {
        return 2;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String clientIp = getClientIp(request);
        
        long currentTime = System.currentTimeMillis();
        Long lastReset = lastResetTime.getOrDefault(clientIp, 0L);
        
        if (currentTime - lastReset > WINDOW_SIZE_MS) {
            requestCounts.put(clientIp, new AtomicInteger(0));
            lastResetTime.put(clientIp, currentTime);
        }
        
        AtomicInteger count = requestCounts.getOrDefault(clientIp, new AtomicInteger(0));
        if (count.incrementAndGet() > RATE_LIMIT) {
            ctx.setResponseStatusCode(429); // Too Many Requests
            ctx.setResponseBody("{\"error\": \"Rate limit exceeded\"}");
            ctx.setSendZuulResponse(false);
        }
        
        return null;
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

## Circuit Breaker Integration

### Hystrix Configuration
```properties
# Global Hystrix Settings
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=5000
hystrix.command.default.circuitBreaker.requestVolumeThreshold=20
hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds=5000
hystrix.command.default.circuitBreaker.errorThresholdPercentage=50

# Service-Specific Settings
hystrix.command.employee-service.execution.isolation.thread.timeoutInMilliseconds=3000
hystrix.command.employee-service.circuitBreaker.requestVolumeThreshold=10
```

### Fallback Implementation
```java
@Component
public class EmployeeServiceFallback implements FallbackProvider {
    
    @Override
    public String getRoute() {
        return "employee-service";
    }
    
    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }
            
            @Override
            public int getRawStatusCode() throws IOException {
                return 503;
            }
            
            @Override
            public String getStatusText() throws IOException {
                return "Service Unavailable";
            }
            
            @Override
            public void close() {
                // No cleanup needed
            }
            
            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(
                    "{\"error\": \"Employee service is currently unavailable\"}".getBytes()
                );
            }
            
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
```

## Running the Server

### Prerequisites
- Java 8+
- Maven 3.3+
- Eureka Server running
- Target services running

### Development Mode
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package
java -jar target/zuul-edge-server-0.0.1-SNAPSHOT.jar
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Testing with MockMvc
```java
@SpringBootTest
@AutoConfigureMockMvc
public class ZuulGatewayTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testEmployeeRouting() throws Exception {
        mockMvc.perform(get("/api/employees"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Gateway", "Zuul"));
    }
    
    @Test
    public void testRateLimiting() throws Exception {
        // Make multiple requests to test rate limiting
        for (int i = 0; i < 150; i++) {
            mockMvc.perform(get("/api/employees"));
        }
        
        mockMvc.perform(get("/api/employees"))
            .andExpect(status().is(429));
    }
}
```

### Load Testing with cURL
```bash
# Test routing
curl -v http://localhost:8765/api/employees

# Test with authentication
curl -H "Authorization: Bearer token" http://localhost:8765/api/employees

# Test rate limiting
for i in {1..110}; do curl http://localhost:8765/api/employees; done
```

## Monitoring and Metrics

### Custom Metrics
```java
@Component
public class GatewayMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;
    private final Timer requestTimer;
    
    public GatewayMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestCounter = Counter.builder("gateway.requests.total")
            .description("Total number of requests")
            .register(meterRegistry);
        this.requestTimer = Timer.builder("gateway.request.duration")
            .description("Request duration")
            .register(meterRegistry);
    }
    
    @EventListener
    public void handleRequest(ZuulFilterEvent event) {
        if ("pre".equals(event.getFilterType())) {
            requestCounter.increment();
        }
    }
}
```

### Health Check Enhancement
```java
@Component
public class GatewayHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Override
    public Health health() {
        List<String> services = discoveryClient.getServices();
        
        Health.Builder builder = new Health.Builder();
        if (services.isEmpty()) {
            builder.down();
        } else {
            builder.up();
        }
        
        builder.withDetail("services", services);
        builder.withDetail("serviceCount", services.size());
        
        return builder.build();
    }
}
```

## Best Practices

### Security
1. **Implement authentication** at gateway level
2. **Use HTTPS** in production
3. **Validate input** and sanitize requests
4. **Implement rate limiting** to prevent abuse
5. **Log security events** appropriately

### Performance
1. **Optimize routing rules** for minimal overhead
2. **Use connection pooling** for backend services
3. **Implement caching** for frequently accessed data
4. **Monitor latency** and optimize bottlenecks
5. **Use circuit breakers** to prevent cascading failures

### Reliability
1. **Implement fallback mechanisms** for service failures
2. **Use health checks** for backend services
3. **Configure timeouts** appropriately
4. **Implement retry logic** with exponential backoff
5. **Monitor circuit breaker status**

## Troubleshooting

### Common Issues

1. **Routing Not Working**
   ```bash
   # Check routes configuration
   curl http://localhost:8765/routes
   
   # Verify service discovery
   curl http://localhost:8761/eureka/apps
   
   # Check service availability
   curl http://localhost:8080/health
   ```

2. **High Latency**
   ```bash
   # Monitor request times
   curl http://localhost:8765/metrics
   
   # Check Hystrix circuit status
   curl http://localhost:8765/hystrix.stream
   ```

3. **Service Unavailable**
   ```bash
   # Check service registration
   curl http://localhost:8761/eureka/apps
   
   # Verify service health
   curl http://localhost:8765/health
   ```

### Debug Logging
```properties
logging.level.org.springframework.cloud.netflix.zuul=DEBUG
logging.level.com.netflix.zuul=DEBUG
logging.level.org.springframework.cloud.netflix.ribbon=DEBUG
```

## Dependencies

Key dependencies and their versions:
- Spring Boot Starter Web
- Spring Boot Starter Actuator
- Spring Cloud Starter Zuul
- Spring Cloud Starter Eureka
- Spring Cloud Starter Sleuth
- Spring Cloud Starter Hystrix
- Spring Boot DevTools
