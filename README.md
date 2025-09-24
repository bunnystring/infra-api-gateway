# Infra API Gateway

Infra API Gateway is the entry point and central routing service for your microservices architecture.  
It handles request forwarding, authentication, error handling, and integration with service discovery (Eureka).

---

## Features

- **Routing:** Forwards requests to the correct backend services based on path.
- **Authentication:** JWT validation and security filters.
- **Error Handling:** Centralized error responses for clients.
- **Service Discovery:** Integrates with Eureka for dynamic routing.
- **Configurable:** Properties managed via `application.properties` or remote Config Server.
- **Health Monitoring:** Exposes health endpoints for observability.

---

## Getting Started

### Prerequisites

- **Java 17** (required)
- **Maven** (for building)
- (Optional) **Eureka Server** for service discovery

---

## Configuration Reference

Below is an example of a typical `application.properties` configuration for Infra API Gateway:

```properties
# Application Name
spring.application.name=infra-api-gateway

# Microservice Port (API Gateway)
server.port=8080

# Eureka Service Discovery Configuration
eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/
eureka.instance.prefer-ip-address=true

# Route Configuration for Microservices
spring.cloud.gateway.routes[0].id=notifications-service
spring.cloud.gateway.routes[0].uri=lb://notifications-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/notifications/**

spring.cloud.gateway.routes[1].id=orders-service
spring.cloud.gateway.routes[1].uri=lb://orders-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/orders/**

spring.cloud.gateway.routes[2].id=groups-service
spring.cloud.gateway.routes[2].uri=lb://groups-service
spring.cloud.gateway.routes[2].predicates[0]=Path=/groups/**

spring.cloud.gateway.routes[3].id=employees-service
spring.cloud.gateway.routes[3].uri=lb://employees-service
spring.cloud.gateway.routes[3].predicates[0]=Path=/employees/**

# JWT Secret Key (HS256)
jwt.secret=nE1eKf/1G7A3WjYzQw1g4QwqFvJKk6o5Wfjh9n6N7sM=

# Disable Spring Cloud Config
spring.cloud.config.enabled=false

# Global Timeout for Routes
spring.cloud.gateway.httpclient.connect-timeout=5000
spring.cloud.gateway.httpclient.response-timeout=5s

# Health Endpoint Details
management.endpoint.health.show-details=always
```

---

## Build and Run

To build the project:

```bash
mvn clean package
```

To run the gateway:

```bash
java -jar target/infra_api-gateway-0.0.1-SNAPSHOT.jar
```

---

## Contributing

Feel free to open issues or pull requests for improvements or bug fixes.  
See the [Pull Request template](.github/PULL_REQUEST_TEMPLATE.md) for guidance.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Contact

For questions or support, open an issue or contact [bunnystring](https://github.com/bunnystring).
