package org.example.enrollmentservice.exceptions;

public class CourseServiceException extends RuntimeException {

    public CourseServiceException(String message) {
        super(message);
    }

    public CourseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
