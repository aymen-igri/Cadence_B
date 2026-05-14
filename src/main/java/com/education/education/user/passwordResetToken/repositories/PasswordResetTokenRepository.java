package com.education.education.user.passwordResetToken.repositories;

import com.education.education.user.passwordResetToken.entities.PasswordResetToken;
import com.education.education.user.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUser(User user);

    void deleteByUser(User user);
}
