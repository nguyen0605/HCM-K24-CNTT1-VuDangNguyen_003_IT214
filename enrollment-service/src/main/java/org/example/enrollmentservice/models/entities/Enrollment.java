package org.example.enrollmentservice.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.enrollmentservice.models.constants.EnrollmentStatus;

@Entity
@Table(name = "enrollments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String studentName;

    @Column(name = "student_email")
    private String studentEmail;

    @Column(name = "total_fee")
    private Double totalFee;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

}
