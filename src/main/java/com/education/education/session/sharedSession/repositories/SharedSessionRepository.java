package com.education.education.session.sharedSession.repositories;

import com.education.education.session.sharedSession.entities.SharedSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SharedSessionRepository extends JpaRepository<SharedSession, UUID> {
    List<SharedSession> findByGroup_Id(UUID groupId);

    Optional<SharedSession> findBySession_IdAndGroup_Id(UUID sessionId, UUID groupId);

    boolean existsBySessionIdAndGroupId(UUID sessionId, UUID groupId);
}
