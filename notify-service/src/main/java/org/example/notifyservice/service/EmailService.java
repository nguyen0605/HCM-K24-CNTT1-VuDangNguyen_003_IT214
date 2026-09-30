package org.example.notifyservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${notification.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendEnrollmentCreatedEmail(String recipient) {
        if (!StringUtils.hasText(recipient)) {
            throw new IllegalArgumentException("Recipient must not be blank");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.from);
        message.setTo(recipient.trim());
        message.setSubject("Đăng ký khóa học thành công");
        message.setText("Đăng ký khóa học thành công. Yêu cầu đăng ký khóa học của bạn đã được hệ thống tiếp nhận.");
        mailSender.send(message);
    }
}
