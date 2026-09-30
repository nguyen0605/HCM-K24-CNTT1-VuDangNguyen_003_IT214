package org.example.enrollmentservice.models.repositories;

import org.example.enrollmentservice.models.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
