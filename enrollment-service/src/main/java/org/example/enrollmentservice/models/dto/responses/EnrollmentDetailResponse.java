package org.example.enrollmentservice.models.dto.responses;

public record EnrollmentDetailResponse(
        Long id,
        Long courseId,
        String courseName,
        Double courseFee,
        Double lineTotal
) {
}
