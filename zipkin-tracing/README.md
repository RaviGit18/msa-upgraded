# Zipkin Tracing

Distributed tracing server for monitoring and troubleshooting microservices in the MSA ecosystem.

## Overview

The Zipkin Tracing service provides distributed tracing capabilities, allowing you to track requests as they flow through various microservices. It helps identify performance bottlenecks, debug issues, and understand service dependencies.

## Features

- Distributed request tracing
- Service dependency visualization
- Performance monitoring
- Latency analysis
- Error tracking
- Trace search and filtering
- Service topology visualization
- Integration with Spring Cloud Sleuth
- Real-time trace collection
- Historical trace analysis

## Technology Stack

- **Spring Boot 1.5.2**
- **Zipkin Server** - Distributed tracing server
- **Zipkin Autoconfigure UI** - Web interface
- **Spring Cloud Config** - External configuration
- **Spring Cloud Sleuth** - Trace generation
- **Spring Boot Actuator** - Monitoring endpoints
- **Spring Boot DevTools** - Development tools

## Configuration

### Application Properties
```properties
server.port=9411
spring.application.name=zipkin-tracing

# Zipkin Server Configuration
zipkin.storage.type=mem
zipkin.collector.http.enabled=true
zipkin.collector.rabbitmq.enabled=false
zipkin.collector.kafka.enabled=false

# UI Configuration
zipkin.ui.query-limit=10
zipkin.ui.default-lookback=86400000
zipkin.ui.supports-flags=true

# Self-Tracing
spring.sleuth.enabled=true
spring.sleuth.sampler.percentage=1.0
```

## Access Points

### Zipkin UI
```http
http://localhost:9411/
```

### API Endpoints

#### Health Check
```http
GET http://localhost:9411/health
```

#### Application Info
```http
GET http://localhost:9411/info
```

#### Metrics
```http
GET http://localhost:9411/metrics
```

#### Zipkin API
```http
GET http://localhost:9411/api/v2/services
GET http://localhost:9411/api/v2/spans?serviceName=employee-service
GET http://localhost:9411/api/v2/trace/{traceId}
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
java -jar target/zipkin-tracing-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```bash
# Using official Zipkin image
docker run -d -p 9411:9411 openzipkin/zipkin

# Custom build
docker build -t zipkin-tracing .
docker run -d -p 9411:9411 zipkin-tracing
```

## Zipkin UI Features

### Trace Search
- Search traces by service name
- Filter by time range
- Search by trace ID
- Filter by annotation tags
- Sort by duration or timestamp

### Trace Details
- Request timeline visualization
- Service call hierarchy
- Span duration analysis
- Error and exception tracking
- Custom tags and annotations

### Service Dependencies
- Service dependency graph
- Request flow visualization
- Service interaction patterns
- Critical path analysis

## Client Integration

### Spring Cloud Sleuth Configuration
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

### Client Properties
```properties
# Zipkin Reporter Configuration
spring.zipkin.base-url=http://localhost:9411
spring.zipkin.sender.type=web
spring.zipkin.discovery-client-enabled=true

# Sleuth Configuration
spring.sleuth.sampler.probability=1.0
spring.sleuth.trace-id128=true
spring.sleuth.baggage-keys=user-id,session-id

# Service Name
spring.application.name=employee-service
```

## Trace Generation

### Automatic Trace Generation
Spring Cloud Sleuth automatically generates traces for:
- HTTP requests (incoming and outgoing)
- Database operations
- Message queue operations
- Scheduled tasks
- Custom spans

### Manual Span Creation
```java
@RestController
public class EmployeeController {
    
    @Autowired
    private Tracer tracer;
    
    @GetMapping("/employees/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        Span span = tracer.nextSpan().name("employee-lookup").start();
        
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            span.tag("employee.id", String.valueOf(id));
            span.tag("operation", "lookup");
            
            // Business logic
            Employee employee = employeeService.findById(id);
            
            span.tag("employee.found", employee != null ? "true" : "false");
            return employee;
        } finally {
            span.end();
        }
    }
}
```

### Custom Annotations
```java
@Service
public class EmployeeService {
    
    @NewSpan("calculate-salary")
    public BigDecimal calculateSalary(@SpanTag("employee.id") Long employeeId) {
        Span span = tracer.currentSpan();
        
        // Add custom tags
        span.tag("service", "employee-service");
        span.tag("operation", "salary-calculation");
        
        // Business logic
        return performSalaryCalculation(employeeId);
    }
    
    @ContinueSpan
    public void updateEmployee(@SpanTag("employee.id") Long employeeId, 
                              @SpanTag("update.type") String updateType) {
        // Update logic
    }
}
```

## Trace Analysis

### Performance Metrics
- **Trace Duration**: Total request time
- **Span Duration**: Individual operation time
- **Service Latency**: Service-specific response times
- **Error Rates**: Failed requests percentage
- **Throughput**: Requests per second

### Common Patterns
1. **Sequential Calls**: Service A → Service B → Service C
2. **Parallel Calls**: Service A → (Service B, Service C)
3. **Async Processing**: Service A → Message Queue → Service B
4. **Error Propagation**: Exception tracking across services

### Troubleshooting Examples

#### High Latency Identification
1. Search for traces with long duration
2. Identify slow spans in trace timeline
3. Analyze service dependencies
4. Pinpoint performance bottlenecks

#### Error Tracking
1. Filter traces with error annotations
2. Examine exception stack traces
3. Identify error propagation patterns
4. Track error rates over time

#### Service Dependency Analysis
1. View service dependency graph
2. Identify critical service paths
3. Analyze service coupling
4. Optimize service interactions

## Advanced Configuration

### Custom Samplers
```java
@Configuration
public class TracingConfig {
    
    @Bean
    public Sampler defaultSampler() {
        return Sampler.create(1.0f); // 100% sampling
    }
    
    @Bean
    public Sampler productionSampler() {
        return Sampler.create(0.1f); // 10% sampling for production
    }
}
```

### Custom Span Handlers
```java
@Component
public class CustomSpanHandler implements SpanHandler {
    
    @Override
    public boolean begin(TraceContext traceContext, Span span, TraceContext parent) {
        // Custom logic when span begins
        return true;
    }
    
    @Override
    public boolean end(TraceContext traceContext, Span span, SpanHandler.Cause cause) {
        // Custom logic when span ends
        return true;
    }
}
```

### Integration with Other Systems

#### Elasticsearch Backend
```properties
zipkin.storage.type=elasticsearch
zipkin.storage.elasticsearch.hosts=localhost:9200
zipkin.storage.elasticsearch.index=zipkin
zipkin.storage.elasticsearch.timeout=10000
```

#### MySQL Backend
```properties
zipkin.storage.type=mysql
spring.datasource.url=jdbc:mysql://localhost:3306/zipkin
spring.datasource.username=zipkin
spring.datasource.password=zipkin
```

#### Cassandra Backend
```properties
zipkin.storage.type=cassandra
spring.data.cassandra.contact-points=localhost:9042
spring.data.cassandra.keyspace-name=zipkin
```

## Monitoring and Alerting

### Health Monitoring
```java
@RestController
public class ZipkinHealthController {
    
    @Autowired
    private ZipkinQueryApiV2 zipkinQueryApi;
    
    @GetMapping("/health/tracing")
    public ResponseEntity<Map<String, Object>> tracingHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            List<String> services = zipkinQueryApi.getServiceNames();
            health.put("status", "UP");
            health.put("services", services.size());
            health.put("last_check", Instant.now());
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(health);
    }
}
```

### Metrics Collection
```java
@Component
public class TracingMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter traceCounter;
    private final Timer traceTimer;
    
    public TracingMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.traceCounter = Counter.builder("traces.total")
            .description("Total number of traces")
            .register(meterRegistry);
        this.traceTimer = Timer.builder("trace.duration")
            .description("Trace duration")
            .register(meterRegistry);
    }
    
    @EventListener
    public void handleTraceReceived(TraceReceivedEvent event) {
        traceCounter.increment();
        traceTimer.record(event.getTraceDuration(), TimeUnit.MILLISECONDS);
    }
}
```

## Best Practices

### Trace Naming
1. **Use descriptive names** for spans
2. **Follow naming conventions** (service-operation)
3. **Include operation type** in span names
4. **Avoid sensitive information** in names

### Tag Usage
1. **Add relevant context** with tags
2. **Use consistent tag naming**
3. **Include request parameters** safely
4. **Add business context** tags

### Sampling Strategy
1. **High sampling in development** (100%)
2. **Lower sampling in production** (1-10%)
3. **Adaptive sampling** based on load
4. **Service-specific sampling** rules

## Performance Considerations

### Sampling Impact
- **100% sampling**: Maximum visibility, highest overhead
- **10% sampling**: Good balance for production
- **1% sampling**: Minimal overhead, limited visibility

### Storage Optimization
```properties
# Trace retention
zipkin.storage.strict-trace-id=true
zipkin.storage.search-enabled=true
zipkin.storage.autocomplete-keys=service.name,http.method
```

### Network Optimization
```properties
# Reporter configuration
spring.zipkin.base-url=http://zipkin-server:9411
spring.zipkin.compression.enabled=true
spring.zipkin.message-timeout=5
```

## Troubleshooting

### Common Issues

1. **Traces Not Appearing**
   ```bash
   # Check Zipkin server is running
   curl http://localhost:9411/health
   
   # Verify client configuration
   # Check network connectivity
   # Validate sampling configuration
   ```

2. **High Memory Usage**
   ```bash
   # Monitor JVM metrics
   curl http://localhost:9411/metrics
   
   # Adjust storage configuration
   # Implement trace retention policies
   ```

3. **Performance Impact**
   ```bash
   # Reduce sampling rate
   spring.sleuth.sampler.probability=0.1
   
   # Optimize span creation
   # Use async reporting
   ```

### Debug Logging
```properties
logging.level.zipkin=DEBUG
logging.level.zipkin2=DEBUG
logging.level.org.springframework.cloud.sleuth=DEBUG
```

## Dependencies

Key dependencies and their versions:
- Spring Boot Starter Web
- Spring Boot Starter Actuator
- Spring Cloud Starter Config
- Spring Cloud Starter Sleuth
- Zipkin Server
- Zipkin Autoconfigure UI
- Spring Boot DevTools
