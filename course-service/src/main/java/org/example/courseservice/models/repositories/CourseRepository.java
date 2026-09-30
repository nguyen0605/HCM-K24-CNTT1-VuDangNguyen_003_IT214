package org.example.courseservice.models.repositories;

import org.example.courseservice.models.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
