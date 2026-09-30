package org.example.courseservice.exceptions;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(Long id) {
        super("Không tìm thấy khóa học với id: " + id);
    }
}
