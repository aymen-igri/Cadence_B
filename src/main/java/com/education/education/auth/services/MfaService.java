package com.education.education.auth.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.education.education.auth.entities.MfaSession;
import com.education.education.auth.enums.EMfaType;
import org.jboss.aerogear.security.otp.Totp;


import com.education.education.auth.repositories.MfaSessionRepository;

import java.time.LocalDateTime;
import java.util.Random;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class MfaService {
    
    private final MfaSessionRepository mfaSessionRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    private void sendEmail(String to, String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your StudyPlatform Verification Code");
        message.setText("Your 6-digit code is: " + code + " It will expire in 5 minutes.");
        mailSender.send(message);
    }
    
    private void sendSMS(String to, String code){
        System.out.println("Sending SMS to " + to + " with code " + code);
    }
    
    public boolean verifySessionCode(User user, String code, EMfaType type){
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
            sendEmail(userDetails.getUsername(), code);
        } else if (type == EMfaType.SMS) {
            sendSMS(user.getPhone(), code);
        }
    }
    
}
