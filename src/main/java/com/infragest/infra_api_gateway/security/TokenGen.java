package com.infragest.infra_api_gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

public class TokenGen {
    public static void main(String[] args) throws IOException {
        // Carga el properties (ajusta la ruta si tu properties está en otro sitio)
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            props.load(fis);
        }

        // Lee el secret
        String secret = props.getProperty("jwt.secret");
        if (secret == null) {
            System.err.println("No se encontró jwt.secret en el properties");
            return;
        }

        // Decodifica base64
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        SecretKeySpec key = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());

        String token = Jwts.builder()
                .setSubject("usuario-prueba")
                .claim("role", "user")
                .setIssuer("api-gateway")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        System.out.println(token);
    }
}