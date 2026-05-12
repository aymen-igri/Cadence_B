package com.education.education.user.passwordResetToken.services;

import com.education.education.user.passwordResetToken.entities.PasswordResetToken;
import com.education.education.user.passwordResetToken.repositories.PasswordResetTokenRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
@AllArgsConstructor
@Transactional
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void resetWithToken(String token, String newPassword) {
        String hashedToken = this.hash(token);

        PasswordResetToken t = tokenRepository.findByToken(hashedToken);
        if (t.isExpired()) {
            throw new RuntimeException("Invalide or expired token");
        }

        User user = t.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(t);
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
