package com.education.education.auth.repositories;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import com.education.education.auth.deo.responses.MfaActivityRes;
import com.education.education.auth.entities.MfaSession;
import com.education.education.user.user.entities.User;
import com.education.education.auth.enums.EMfaType;

public interface MfaSessionRepository extends JpaRepository<MfaSession, UUID> {
  Optional<MfaSession> findFirstByUserAndTypeAndIsUsedFalseOrderByCreatedAtDesc(User user, EMfaType type);

  @Query("SELECT new com.education.education.auth.deo.responses.MfaActivityRes(" +
      "s.user.username, s.type, s.attempts, s.createdAt, s.isUsed) " +
      "FROM MfaSession s " +
      "ORDER BY s.createdAt DESC")
  List<MfaActivityRes> findLastMfaActivities(Pageable pageable);
}
