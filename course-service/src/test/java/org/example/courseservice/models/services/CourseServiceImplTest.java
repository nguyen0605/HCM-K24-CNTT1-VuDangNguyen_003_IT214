package org.example.courseservice.models.services;

import org.example.courseservice.exceptions.CourseNotFoundException;
import org.example.courseservice.models.entities.Course;
import org.example.courseservice.models.repositories.CourseRepository;
import org.example.courseservice.models.services.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CourseServiceImplTest {

    private CourseRepository courseRepository;
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseService = new CourseServiceImpl(courseRepository);
    }

    @Test
    void getCourseByIdReturnsCourseWhenItExists() {
        Course course = new Course(1L, "Laptop", "Laptop văn phòng", new BigDecimal("15000000.00"), 5);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(1L);

        assertSame(course, result);
        verify(courseRepository).findById(1L);
    }

    @Test
    void getCourseByIdThrowsExceptionWhenItDoesNotExist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        CourseNotFoundException exception = assertThrows(
                CourseNotFoundException.class,
                () -> courseService.getCourseById(99L)
        );

        assertEquals("Không tìm thấy khóa học với id: 99", exception.getMessage());
    }

    @Test
    void getCourseByIdUsesCoursesCacheWithIdAsKey() throws NoSuchMethodException {
        Cacheable cacheable = CourseServiceImpl.class
                .getMethod("getCourseById", Long.class)
                .getAnnotation(Cacheable.class);

        assertEquals("courses", cacheable.cacheNames()[0]);
        assertEquals("#id", cacheable.key());
        assertTrue(cacheable.sync());
        assertTrue(Serializable.class.isAssignableFrom(Course.class));
    }
}
