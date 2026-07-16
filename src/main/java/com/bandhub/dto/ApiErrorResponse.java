package com.bandhub.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp,
    Map<String, String> errors
) {

}
