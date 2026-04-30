package com.education.education.auth.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.education.education.auth.entities.MfaSession;
import com.education.education.user.user.entities.User;
import com.education.education.auth.enums.EMfaType;

public interface MfaSessionRepository extends JpaRepository<MfaSession, UUID> {
    Optional<MfaSession> findFirstByUserAndTypeAndIsUsedFalseOrderByCreatedAtDesc(User user, EMfaType type);
}
