package com.infragest.infra_api_gateway.exception;

public class InvalidJwtAuthenticationException extends RuntimeException {

    public enum Type {
        INVALID_TOKEN,
        EXPIRED_TOKEN,
        UNSUPPORTED_TOKEN,
        MALFORMED_TOKEN,
        SIGNATURE_ERROR,
        AUTHORIZATION_HEADER_MISSING
    }

    private final Type type;

    public InvalidJwtAuthenticationException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}