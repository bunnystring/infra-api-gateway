package com.infragest.infra_api_gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private String message;
    private int status;
    private String service;
    private String type;
}
