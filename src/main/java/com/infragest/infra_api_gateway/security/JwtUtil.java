package com.infragest.infra_api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private byte[] secretBytes;

    @PostConstruct
    public void init() {
        // Decodifica el secret una vez al inicializar el bean
        secretBytes = Base64.getDecoder().decode(jwtSecret);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretBytes) // <-- Corrige aquí
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Token inválido", e);
            return false;
        }
    }

    // EXTRA: obtener claims del token
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretBytes) // <-- Corrige aquí también
                .parseClaimsJws(token)
                .getBody();
    }
}