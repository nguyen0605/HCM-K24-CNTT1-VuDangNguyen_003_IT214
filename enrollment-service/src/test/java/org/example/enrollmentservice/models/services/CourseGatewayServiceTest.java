package org.example.enrollmentservice.models.services;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.enrollmentservice.clients.CourseClient;
import org.example.enrollmentservice.exceptions.CourseNotFoundException;
import org.example.enrollmentservice.exceptions.CourseServiceException;
import org.example.enrollmentservice.models.dto.responses.CourseResponse;
import org.example.enrollmentservice.models.services.impl.CourseGatewayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseGatewayServiceTest {

    private CourseClient courseClient;
    private CourseGatewayService courseGatewayService;

    @BeforeEach
    void setUp() {
        courseClient = mock(CourseClient.class);
        courseGatewayService = new CourseGatewayService(courseClient);
    }

    @Test
    void circuitBreakerAnnotationConfiguredCorrectly() throws NoSuchMethodException {
        Method method = CourseGatewayService.class.getMethod("getCourseById", Long.class);
        CircuitBreaker cb = method.getAnnotation(CircuitBreaker.class);

        assertNotNull(cb);
        assertEquals("courseService", cb.name());
        assertEquals("getCourseByIdFallback", cb.fallbackMethod());
    }

    @Test
    void getCourseByIdReturnsCourseResponse() {
        CourseResponse expected = new CourseResponse(1L, "Java 21", 1500000.0);
        when(courseClient.getCourseById(1L)).thenReturn(expected);

        CourseResponse actual = courseGatewayService.getCourseById(1L);

        assertEquals(expected, actual);
        verify(courseClient).getCourseById(1L);
    }

    @Test
    void getCourseByIdThrowsCourseNotFoundExceptionWhenNotFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/v1/courses/99", Collections.emptyMap(), null, new RequestTemplate());
        FeignException.NotFound notFound = new FeignException.NotFound("Not Found", request, null, null);
        when(courseClient.getCourseById(99L)).thenThrow(notFound);

        assertThrows(CourseNotFoundException.class, () -> courseGatewayService.getCourseById(99L));
    }

    @Test
    void fallbackRethrowsCourseNotFoundException() {
        CourseNotFoundException exception = new CourseNotFoundException(99L);
        assertThrows(CourseNotFoundException.class, () -> courseGatewayService.getCourseByIdFallback(99L, exception));
    }

    @Test
    void fallbackConvertsFeignNotFoundToCourseNotFoundException() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/v1/courses/99", Collections.emptyMap(), null, new RequestTemplate());
        FeignException.NotFound feignNotFound = new FeignException.NotFound("Not Found", request, null, null);

        assertThrows(CourseNotFoundException.class, () -> courseGatewayService.getCourseByIdFallback(99L, feignNotFound));
    }

    @Test
    void fallbackConvertsOtherExceptionsToCourseServiceException() {
        RuntimeException runtimeException = new RuntimeException("Connection timeout");
        assertThrows(CourseServiceException.class, () -> courseGatewayService.getCourseByIdFallback(1L, runtimeException));
    }
}
