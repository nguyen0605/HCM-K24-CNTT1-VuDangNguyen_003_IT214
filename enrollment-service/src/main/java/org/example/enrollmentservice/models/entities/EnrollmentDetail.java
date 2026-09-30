package org.example.enrollmentservice.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enrollment_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EnrollmentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_fee")
    private Double courseFee;
}
