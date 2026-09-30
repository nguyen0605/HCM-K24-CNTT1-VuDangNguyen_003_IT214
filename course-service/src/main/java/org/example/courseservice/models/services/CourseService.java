package org.example.courseservice.models.services;

import org.example.courseservice.models.entities.Course;

public interface CourseService {
    Course getCourseById(Long id);
}
