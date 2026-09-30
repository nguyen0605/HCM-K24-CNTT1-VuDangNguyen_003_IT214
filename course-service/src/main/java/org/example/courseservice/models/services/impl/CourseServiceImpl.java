package org.example.courseservice.models.services.impl;

import org.example.courseservice.exceptions.CourseNotFoundException;
import org.example.courseservice.models.entities.Course;
import org.example.courseservice.models.repositories.CourseRepository;
import org.example.courseservice.models.services.CourseService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "courses", key = "#id", sync = true)
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }
}
