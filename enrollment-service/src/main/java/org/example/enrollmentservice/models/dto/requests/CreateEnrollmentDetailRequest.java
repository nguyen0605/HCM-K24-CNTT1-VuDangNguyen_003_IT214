package org.example.enrollmentservice.models.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentDetailRequest(
        @NotNull(message = "Course id is required")
        @Min(value = 1, message = "Course id must be greater than 0")
        Long courseId
) {
}
