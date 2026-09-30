package org.example.courseservice.exceptions;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {
}
