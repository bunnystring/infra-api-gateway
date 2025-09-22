package com.infragest.infra_api_gateway.exception;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infragest.infra_api_gateway.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        String routeId = (String) exchange.getAttribute("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRoute");
        String service = (routeId != null) ? routeId : "api-gateway";

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();
        String type = null;

        if (ex instanceof InvalidJwtAuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage();
            type = ((InvalidJwtAuthenticationException) ex).getType().name();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                message,
                status.value(),
                service,
                type
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            bytes = ("{\"timestamp\":\"" + Instant.now() + "\",\"message\":\"Error serializing error response\",\"status\":500,\"service\":\"api-gateway\"}").getBytes();
        }

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}