package org.example.enrollmentservice.models.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import java.util.List;

public record CreateEnrollmentRequest(
        @NotBlank(message = "Student name is required")
        @Size(max = 255, message = "Student name must not exceed 255 characters")
        String studentName,

        @NotBlank(message = "Student email is required")
        @Email(message = "Student email must be valid")
        String studentEmail,

        @NotEmpty(message = "Enrollment must contain at least one item")
        List<@Valid CreateEnrollmentDetailRequest> items
) {
}
