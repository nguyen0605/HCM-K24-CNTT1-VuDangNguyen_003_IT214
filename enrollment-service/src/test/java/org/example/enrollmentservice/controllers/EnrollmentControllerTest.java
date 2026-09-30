package org.example.enrollmentservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.enrollmentservice.exceptions.CourseNotFoundException;
import org.example.enrollmentservice.exceptions.CourseServiceException;
import org.example.enrollmentservice.exceptions.DuplicateCourseException;
import org.example.enrollmentservice.exceptions.GlobalExceptionHandler;
import org.example.enrollmentservice.models.constants.EnrollmentStatus;
import org.example.enrollmentservice.models.dto.requests.CreateEnrollmentDetailRequest;
import org.example.enrollmentservice.models.dto.requests.CreateEnrollmentRequest;
import org.example.enrollmentservice.models.dto.responses.EnrollmentDetailResponse;
import org.example.enrollmentservice.models.dto.responses.EnrollmentResponse;
import org.example.enrollmentservice.models.services.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnrollmentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        enrollmentService = mock(EnrollmentService.class);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new EnrollmentController(enrollmentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createEnrollmentReturns201WhenValid() throws Exception {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                "Nguyen Van A",
                "nguyenvana@example.com",
                List.of(new CreateEnrollmentDetailRequest(1L))
        );

        EnrollmentResponse response = new EnrollmentResponse(
                1L,
                "Nguyen Van A",
                "nguyenvana@example.com",
                1500000.0,
                EnrollmentStatus.PENDING,
                List.of(new EnrollmentDetailResponse(1L, 1L, "Course 1", 1500000.0, 1500000.0))
        );

        when(enrollmentService.createEnrollment(any(CreateEnrollmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.studentName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.studentEmail").value("nguyenvana@example.com"))
                .andExpect(jsonPath("$.totalFee").value(1500000.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createEnrollmentReturns400WhenInvalid() throws Exception {
        CreateEnrollmentRequest invalidRequest = new CreateEnrollmentRequest(
                "",
                "invalid-email",
                List.of()
        );

        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"));
    }

    @Test
    void createEnrollmentReturns404WhenCourseNotFound() throws Exception {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                "Nguyen Van A",
                "nguyenvana@example.com",
                List.of(new CreateEnrollmentDetailRequest(999L))
        );

        when(enrollmentService.createEnrollment(any(CreateEnrollmentRequest.class)))
                .thenThrow(new CourseNotFoundException(999L));

        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Course not found"));
    }

    @Test
    void createEnrollmentReturns502WhenCourseServiceFails() throws Exception {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                "Nguyen Van A",
                "nguyenvana@example.com",
                List.of(new CreateEnrollmentDetailRequest(1L))
        );

        when(enrollmentService.createEnrollment(any(CreateEnrollmentRequest.class)))
                .thenThrow(new CourseServiceException("Course service is currently unavailable"));

        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.title").value("Course service error"));
    }

    @Test
    void createEnrollmentReturns400WhenDuplicateCourse() throws Exception {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                "Nguyen Van A",
                "nguyenvana@example.com",
                List.of(
                        new CreateEnrollmentDetailRequest(1L),
                        new CreateEnrollmentDetailRequest(1L)
                )
        );

        when(enrollmentService.createEnrollment(any(CreateEnrollmentRequest.class)))
                .thenThrow(new DuplicateCourseException());

        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Duplicate course"));
    }
}
