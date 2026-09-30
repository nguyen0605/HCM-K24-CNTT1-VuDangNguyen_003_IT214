package org.example.enrollmentservice.models.services;

import org.example.enrollmentservice.exceptions.CourseNotFoundException;
import org.example.enrollmentservice.exceptions.CourseServiceException;
import org.example.enrollmentservice.exceptions.DuplicateCourseException;
import org.example.enrollmentservice.models.constants.EnrollmentStatus;
import org.example.enrollmentservice.models.dto.requests.CreateEnrollmentDetailRequest;
import org.example.enrollmentservice.models.dto.requests.CreateEnrollmentRequest;
import org.example.enrollmentservice.models.dto.responses.CourseResponse;
import org.example.enrollmentservice.models.dto.responses.EnrollmentResponse;
import org.example.enrollmentservice.models.entities.Enrollment;
import org.example.enrollmentservice.models.entities.EnrollmentDetail;
import org.example.enrollmentservice.models.repositories.EnrollmentDetailRepository;
import org.example.enrollmentservice.models.repositories.EnrollmentRepository;
import org.example.enrollmentservice.models.services.impl.CourseGatewayService;
import org.example.enrollmentservice.models.services.impl.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EnrollmentServiceImplTest {

    private EnrollmentRepository enrollmentRepository;
    private EnrollmentDetailRepository enrollmentDetailRepository;
    private CourseGatewayService courseGatewayService;
    private KafkaTemplate<String, String> kafkaTemplate;
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        enrollmentRepository = mock(EnrollmentRepository.class);
        enrollmentDetailRepository = mock(EnrollmentDetailRepository.class);
        courseGatewayService = mock(CourseGatewayService.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        enrollmentService = new EnrollmentServiceImpl(enrollmentRepository, enrollmentDetailRepository, courseGatewayService, kafkaTemplate);
    }

    @Test
    void createEnrollmentSuccessfullyCalculatesTotalAndEmitsKafka() {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                "Nguyen Van A",
                "nguyenvana@example.com",
                List.of(
                        new CreateEnrollmentDetailRequest(1L),
                        new CreateEnrollmentDetailRequest(2L)
                )
        );

        when(courseGatewayService.getCourseById(1L)).thenReturn(new CourseResponse(1L, "Course 1", 1200000.0));
        when(courseGatewayService.getCourseById(2L)).thenReturn(new CourseResponse(2L, "Course 2", 900000.0));

        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment e = invocation.getArgument(0);
            e.setId(100L);
            return e;
        });

        when(enrollmentDetailRepository.save(any(EnrollmentDetail.class))).thenAnswer(invocation -> {
            EnrollmentDetail d = invocation.getArgument(0);
            d.setId(200L);
            return d;
        });

        EnrollmentResponse response = enrollmentService.createEnrollment(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Nguyen Van A", response.studentName());
        assertEquals("nguyenvana@example.com", response.studentEmail());
        assertEquals(2100000.0, response.totalFee());
        assertEquals(EnrollmentStatus.PENDING, response.status());
        assertEquals(2, response.items().size());

        assertEquals(1200000.0, response.items().get(0).lineTotal());
        assertEquals(1200000.0, response.items().get(0).courseFee());
        assertEquals("Course 1", response.items().get(0).courseName());

        assertEquals(900000.0, response.items().get(1).lineTotal());
        assertEquals(900000.0, response.items().get(1).courseFee());
        assertEquals("Course 2", response.items().get(1).courseName());

        verify(kafkaTemplate).send("enrollment-created", "nguyenvana@example.com");
    }

    @Test
    void createEnrollmentFailsWhenCourseNotFoundAndDoesNotSaveOrPublish() {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                "Nguyen Van A",
                "nguyenvana@example.com",
                List.of(new CreateEnrollmentDetailRequest(999L))
        );

        when(courseGatewayService.getCourseById(999L)).thenThrow(new CourseNotFoundException(999L));

        assertThrows(CourseNotFoundException.class, () -> enrollmentService.createEnrollment(request));

        verify(enrollmentRepository, never()).save(any());
        verify(enrollmentDetailRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void createEnrollmentFailsWhenCourseServiceFailsAndDoesNotSaveOrPublish() {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                "Nguyen Van A",
                "nguyenvana@example.com",
                List.of(new CreateEnrollmentDetailRequest(1L))
        );

        when(courseGatewayService.getCourseById(1L)).thenThrow(new CourseServiceException("Service down"));

        assertThrows(CourseServiceException.class, () -> enrollmentService.createEnrollment(request));

        verify(enrollmentRepository, never()).save(any());
        verify(enrollmentDetailRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void createEnrollmentFailsWhenDuplicateCourses() {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(
                "Nguyen Van A",
                "nguyenvana@example.com",
                List.of(
                        new CreateEnrollmentDetailRequest(1L),
                        new CreateEnrollmentDetailRequest(1L)
                )
        );

        assertThrows(DuplicateCourseException.class, () -> enrollmentService.createEnrollment(request));

        verify(courseGatewayService, never()).getCourseById(anyLong());
        verify(enrollmentRepository, never()).save(any());
        verify(enrollmentDetailRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
