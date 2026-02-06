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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                .pathMatchers("/api/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
                .build();
    }

    /**
     * Configura CORS para permitir peticiones desde el frontend Angular.
     * Esta es la configuración centralizada de CORS para toda la arquitectura de microservicios.
     *
     * @return configuración CORS aplicada a todas las rutas del gateway
     * @since 2026-02-06
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (Angular en desarrollo)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Headers permitidos en las peticiones
        configuration.setAllowedHeaders(List.of("*"));

        // Permitir credenciales (cookies, JWT en Authorization header)
        configuration.setAllowCredentials(true);

        // Headers expuestos en la respuesta que el cliente puede leer
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Total-Count"
        ));

        // Tiempo de caché para preflight requests (en segundos)
        configuration.setMaxAge(3600L);

        // Aplicar la configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}