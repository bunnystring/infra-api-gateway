package com.infragest.infra_api_gateway.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Configuración de seguridad para el API Gateway.
 *
 * Esta clase define la configuración de seguridad usando Spring Security y WebFlux.
 * - Permite el acceso público a las rutas bajo /auth/**
 * - Requiere autenticación JWT en todas las demás rutas
 * - Deshabilita CSRF
 *
 * Con esta configuración, toda la validación del JWT es gestionada automáticamente por Spring Security.
 *
 * @author bunnyString
 * @since 2025-11-02
 * @version 1.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret}")
    private String jwtSecret;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        byte[] secretBytes = Base64.getDecoder().decode(jwtSecret);
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Define la cadena de filtros de seguridad para el API Gateway.
     *
     * @param http Objeto de configuración de seguridad reactiva.
     * @return Cadena de filtros de seguridad configurada.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        System.out.println("Inicializando SecurityWebFilterChain para API Gateway...");
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                .pathMatchers("/api/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
                .build();
    }
}