package org.example.enrollmentservice.exceptions;

public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(Long courseId) {
        super("Course not found with id: " + courseId);
    }
}
