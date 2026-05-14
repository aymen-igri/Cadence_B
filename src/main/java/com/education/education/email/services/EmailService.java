package com.education.education.email.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.email}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMfaVerificationEmail(String toEmail, String verificationCode, String firstName) {
        try {
            Context context = new Context();
            context.setVariable("verificationCode", verificationCode);
            context.setVariable("firstName", firstName);

            String htmlContent = templateEngine.process("mfa-verification", context);

            sendHtmlEmail(toEmail, "Your MFA Verification Code - Study Platform", htmlContent);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send MFA verification email to: " + toEmail, e);
        }
    }

    public void sendPasswordResetEmail(String email,String firstName , String resetToken) {;
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);

            String resetLink = "http://localhost:4200/change-password?token=" + resetToken;
            context.setVariable("resetLink", resetLink);

            String htmlContent = templateEngine.process("password-reset", context);

            sendHtmlEmail(email, "Password Reset Request - Study Platform", htmlContent);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email to: " + email, e);
        }
    }

    public void sendPlainTextEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, false); // false = content is plain text

        mailSender.send(message);
    }

    public void sendBulkHtmlEmail(String[] recipients, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setBcc(recipients);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}
