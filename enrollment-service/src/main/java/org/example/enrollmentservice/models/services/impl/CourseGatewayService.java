package org.example.enrollmentservice.models.services.impl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enrollmentservice.clients.CourseClient;
import org.example.enrollmentservice.exceptions.CourseNotFoundException;
import org.example.enrollmentservice.exceptions.CourseServiceException;
import org.example.enrollmentservice.models.dto.responses.CourseResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseGatewayService {

    private final CourseClient courseClient;

    @CircuitBreaker(name = "courseService", fallbackMethod = "getCourseByIdFallback")
    public CourseResponse getCourseById(Long courseId) {
        try {
            CourseResponse response = courseClient.getCourseById(courseId);
            if (response == null || response.courseId() == null) {
                throw new CourseNotFoundException(courseId);
            }
            if (response.courseFee() == null) {
                throw new CourseServiceException("Course fee is missing for course id: " + courseId);
            }
            return response;
        } catch (FeignException fe) {
            if (fe.status() == 404) {
                throw new CourseNotFoundException(courseId);
            }
            throw fe;
        }
    }

    public CourseResponse getCourseByIdFallback(Long courseId, Throwable throwable) {
        if (throwable instanceof CourseNotFoundException cnfe) {
            throw cnfe;
        }
        if (throwable instanceof FeignException fe && fe.status() == 404) {
            throw new CourseNotFoundException(courseId);
        }
        if (throwable instanceof CourseServiceException cse) {
            throw cse;
        }
        log.error("Course service call failed for courseId: {}, error: {}", courseId, throwable.getMessage());
        throw new CourseServiceException("Course service is currently unavailable", throwable);
    }

}
