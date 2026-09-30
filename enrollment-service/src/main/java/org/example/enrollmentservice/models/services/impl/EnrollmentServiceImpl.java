package org.example.enrollmentservice.models.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.enrollmentservice.models.constants.EnrollmentStatus;
import org.example.enrollmentservice.exceptions.DuplicateCourseException;
import org.example.enrollmentservice.models.dto.requests.CreateEnrollmentDetailRequest;
import org.example.enrollmentservice.models.dto.requests.CreateEnrollmentRequest;
import org.example.enrollmentservice.models.dto.responses.EnrollmentDetailResponse;
import org.example.enrollmentservice.models.dto.responses.EnrollmentResponse;
import org.example.enrollmentservice.models.dto.responses.CourseResponse;
import org.example.enrollmentservice.models.entities.Enrollment;
import org.example.enrollmentservice.models.entities.EnrollmentDetail;
import org.example.enrollmentservice.models.repositories.EnrollmentDetailRepository;
import org.example.enrollmentservice.models.repositories.EnrollmentRepository;
import org.example.enrollmentservice.models.services.EnrollmentService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

        private static final String ENROLLMENT_CREATED_TOPIC = "enrollment-created";

        private final EnrollmentRepository enrollmentRepository;
        private final EnrollmentDetailRepository enrollmentDetailRepository;
        private final CourseGatewayService courseGatewayService;
        private final KafkaTemplate<String, String> kafkaTemplate;

        private record ItemDetailInfo(CreateEnrollmentDetailRequest requestItem, CourseResponse course, double lineTotal) {}

        @Override
        @Transactional
        public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
                if (request == null || request.items() == null || request.items().isEmpty()) {
                        throw new IllegalArgumentException("Enrollment must contain at least one item");
                }

                Set<Long> seenCourseIds = new HashSet<>();
                for (CreateEnrollmentDetailRequest item : request.items()) {
                        if (!seenCourseIds.add(item.courseId())) {
                                throw new DuplicateCourseException();
                        }
                }

                List<ItemDetailInfo> itemInfos = new ArrayList<>();
                double totalFee = 0.0;

                for (CreateEnrollmentDetailRequest item : request.items()) {
                        CourseResponse course = courseGatewayService.getCourseById(item.courseId());
                        double courseFee = course.courseFee();
                        double lineTotal = courseFee;
                        totalFee += lineTotal;
                        itemInfos.add(new ItemDetailInfo(item, course, lineTotal));
                }

                Enrollment enrollment = Enrollment.builder()
                                .studentName(request.studentName())
                                .studentEmail(request.studentEmail())
                                .totalFee(totalFee)
                                .status(EnrollmentStatus.PENDING)
                                .build();
                Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

                List<EnrollmentDetailResponse> detailResponses = new ArrayList<>();
                for (ItemDetailInfo info : itemInfos) {
                        EnrollmentDetail detail = EnrollmentDetail.builder()
                                        .enrollment(savedEnrollment)
                                        .courseId(info.requestItem().courseId())
                                        .courseFee(info.course().courseFee())
                                        .build();
                        EnrollmentDetail savedDetail = enrollmentDetailRepository.save(detail);

                        detailResponses.add(new EnrollmentDetailResponse(
                                        savedDetail.getId(),
                                        savedDetail.getCourseId(),
                                        info.course().courseName(),
                                        savedDetail.getCourseFee(),
                                        info.lineTotal()
                        ));
                }

                kafkaTemplate.send(ENROLLMENT_CREATED_TOPIC, savedEnrollment.getStudentEmail());
                log.info("Sent enrollment-created event to topic {} for email: {}", ENROLLMENT_CREATED_TOPIC, savedEnrollment.getStudentEmail());

                return new EnrollmentResponse(
                                savedEnrollment.getId(),
                                savedEnrollment.getStudentName(),
                                savedEnrollment.getStudentEmail(),
                                savedEnrollment.getTotalFee(),
                                savedEnrollment.getStatus(),
                                detailResponses
                );
        }

}
