package com.education.education.user.passwordResetToken.services;

import com.education.education.auth.enums.EMfaType;
import com.education.education.auth.services.MfaSessionService;
import com.education.education.email.services.EmailService;
import com.education.education.user.passwordResetToken.dto.request.PasswordUpdateReq;
import com.education.education.user.passwordResetToken.entities.PasswordResetToken;
import com.education.education.user.passwordResetToken.repositories.PasswordResetTokenRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MfaSessionService mfaSessionService;
    private final EmailService emailService;

    public void initialRecovery(String identifier) {
        User user = userRepository.findByUsername(identifier);
        if (user == null) user = userRepository.findByEmail(identifier);

        if (user != null) {
            tokenRepository.deleteByUser(user);

            String rowToken = UUID.randomUUID().toString();
            String hashedToken = this.hash(rowToken);

            PasswordResetToken token = PasswordResetToken.builder()
                    .token(hashedToken)
                    .user(user)
                    .expirDate(LocalDateTime.now().plusMinutes(15))
                    .build();
            tokenRepository.save(token);

            System.out.println("token that i should send when i want to use it for reset password: "+ token.getToken());

            emailService.sendPasswordResetEmail(user.getEmail(),user.getFirstName() ,rowToken);
        }
    }

    public void resetWithToken(String token, String newPassword) {
        String hashedToken = this.hash(token);

        PasswordResetToken t = tokenRepository.findByToken(hashedToken);
        if (t == null || t.isExpired()) {
            throw new RuntimeException("Invalide or expired token");
        }

        User user = t.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(t);
    }

    public void updatePassword(UserDetails userDetails, PasswordUpdateReq req) {
        User user = userRepository.findByUsername(userDetails.getUsername());

        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        boolean mfaSentInRequest = (req.type() != null && req.code() != null);

        if (user.isTotpEnabled() && !mfaSentInRequest){
            throw new RuntimeException("MFA is enabled on this account. A code is required.");
        }

        if (mfaSentInRequest) {
            boolean valid = mfaSessionService.verifyMfa(userDetails, req.code(), req.type());
            if (!valid) {
                throw new RuntimeException("Invalid verification code");
            }
        }

        changePassword(user, req.newPassword());
    }

    private void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
