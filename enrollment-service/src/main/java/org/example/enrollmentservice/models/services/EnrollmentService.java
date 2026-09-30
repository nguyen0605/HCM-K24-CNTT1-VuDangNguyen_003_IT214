package org.example.enrollmentservice.models.services;

import org.example.enrollmentservice.models.dto.requests.CreateEnrollmentRequest;
import org.example.enrollmentservice.models.dto.responses.EnrollmentResponse;

public interface EnrollmentService {

    EnrollmentResponse createEnrollment(CreateEnrollmentRequest request);
}
