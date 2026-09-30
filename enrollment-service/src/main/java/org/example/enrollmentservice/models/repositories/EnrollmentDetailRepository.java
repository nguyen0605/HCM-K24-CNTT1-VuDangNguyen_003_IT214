package org.example.enrollmentservice.models.repositories;

import org.example.enrollmentservice.models.entities.EnrollmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentDetailRepository extends JpaRepository<EnrollmentDetail, Long> {
}
