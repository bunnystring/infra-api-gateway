package com.infragest.infra_api_gateway.security;

import com.infragest.infra_api_gateway.exception.InvalidJwtAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements WebFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // SOLO estas rutas son públicas
       /* if (path.equals("/actuator/health")
                || path.equals("/employees/login")
                || path.equals("/employees/register")) {
            return chain.filter(exchange);
        }*/

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidJwtAuthenticationException(
                    "Authorization header missing or invalid",
                    InvalidJwtAuthenticationException.Type.AUTHORIZATION_HEADER_MISSING
            );
        }

        String token = authHeader.substring(7);
        try {
            if (!jwtUtil.isTokenValid(token)) {
                throw new InvalidJwtAuthenticationException(
                        "Invalid JWT token",
                        InvalidJwtAuthenticationException.Type.INVALID_TOKEN
                );
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new InvalidJwtAuthenticationException(
                    "Expired JWT token",
                    InvalidJwtAuthenticationException.Type.EXPIRED_TOKEN
            );
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new InvalidJwtAuthenticationException(
                    "Malformed JWT token",
                    InvalidJwtAuthenticationException.Type.MALFORMED_TOKEN
            );
        } catch (io.jsonwebtoken.SignatureException e) {
            throw new InvalidJwtAuthenticationException(
                    "JWT signature does not match",
                    InvalidJwtAuthenticationException.Type.SIGNATURE_ERROR
            );
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            throw new InvalidJwtAuthenticationException(
                    "Unsupported JWT token",
                    InvalidJwtAuthenticationException.Type.UNSUPPORTED_TOKEN
            );
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException(
                    "JWT authentication error",
                    InvalidJwtAuthenticationException.Type.INVALID_TOKEN
            );
        }

        return chain.filter(exchange);
    }
}