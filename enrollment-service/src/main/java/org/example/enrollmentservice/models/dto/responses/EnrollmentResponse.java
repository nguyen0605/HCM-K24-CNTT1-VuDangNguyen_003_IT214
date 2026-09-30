package org.example.enrollmentservice.models.dto.responses;

import org.example.enrollmentservice.models.constants.EnrollmentStatus;

import java.util.List;

public record EnrollmentResponse(
        Long id,
        String studentName,
        String studentEmail,
        Double totalFee,
        EnrollmentStatus status,
        List<EnrollmentDetailResponse> items
) {
}
