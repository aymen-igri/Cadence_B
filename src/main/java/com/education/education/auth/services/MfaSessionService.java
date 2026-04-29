package com.education.education.auth.services;

import com.education.education.auth.utils.AuthUtils;
import com.education.education.email.services.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.education.education.auth.entities.MfaSession;
import com.education.education.auth.enums.EMfaType;
import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;

import com.education.education.auth.repositories.MfaSessionRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class MfaSessionService {
    
    private static final Logger logger = LoggerFactory.getLogger(MfaSessionService.class);
    
    private final MfaSessionRepository mfaSessionRepository;
    private final UserRepository userRepository;
    private final AuthUtils authUtils;
    private final JavaMailSender mailSender;
    private final EmailService emailService;

    private void sendEmail(String to, String code){
        try {
            logger.info("Attempting to send MFA email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(("aymenigri@gmail.com"));
            message.setTo(to);
            message.setSubject("Your StudyPlatform Verification Code");
            message.setText("Your 6-digit code is: " + code + " It will expire in 5 minutes.");
            mailSender.send(message);
            logger.info("Email successfully sent to: {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email", e);
        } catch (Exception e) {
            logger.error("Unexpected error sending email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Unexpected error sending verification email", e);
        }
    }
    
    private void sendSMS(String to, String code){
        System.out.println("Sending SMS to " + to + " with code " + code);
    }
    
    public boolean verifySessionCode(UserDetails userdetails, String code, EMfaType type){

        User user = userRepository.findByUsername(userdetails.getUsername());

        return mfaSessionRepository.findFirstByUserAndCodeAndTypeAndIsUsedFalseOrderByCreatedAtDesc(user, code, type)
            .filter(session -> !session.isExpired())
            .map(session -> {
                session.setUsed(true);
                mfaSessionRepository.save(session);
                return true;
            }).orElse(false);
    }
    
    public boolean verifyAppCode(String secret, String code){
        try{
            Totp totp = new Totp(secret);
            return totp.verify(code);
        } catch (Exception e) {
            return false;
        }
    }
    
    public void sendMfaCode(UserDetails userDetails, EMfaType type) {
        
        User user = userRepository.findByUsername(userDetails.getUsername());
        
        String code = String.format("%06d", new Random().nextInt(999999));
        
        MfaSession session = MfaSession.builder()
            .user(user)
            .code(code)
            .expiry(LocalDateTime.now().plusMinutes(5))
            .type(type)
            .isUsed(false)
            .build();
        mfaSessionRepository.save(session);
        
        if (type == EMfaType.EMAIL) {
            emailService.sendMfaVerificationEmail(user.getEmail(),code);
        } else if (type == EMfaType.SMS) {
            sendSMS(user.getPhone(), code);
        }
    }

    public Map<String, String> generateFinalToken(UserDetails userDetails){
        return authUtils.generateTokenResponse(userDetails);
    }

    public boolean verifyMfa(UserDetails userDetails, String code, EMfaType type){
        if (type == EMfaType.APP){
            return verifyAppCode(
                    userRepository.findByUsername(userDetails.getUsername()).getTotpSecret(),
                    code
            );
        }else {
            return verifySessionCode(userDetails, code, type);
        }
    }
    
}
