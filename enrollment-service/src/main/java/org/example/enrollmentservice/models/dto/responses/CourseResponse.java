package org.example.enrollmentservice.models.dto.responses;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CourseResponse(
        @JsonAlias("id") Long courseId,
        String courseName,
        Double courseFee
) {
}
