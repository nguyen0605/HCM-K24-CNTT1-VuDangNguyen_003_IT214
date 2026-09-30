package org.example.enrollmentservice.clients;

import org.example.enrollmentservice.models.dto.responses.CourseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service", path = "/api/v1/courses")
public interface CourseClient {

    @GetMapping("/{id}")
    CourseResponse getCourseById(@PathVariable Long id);
}
