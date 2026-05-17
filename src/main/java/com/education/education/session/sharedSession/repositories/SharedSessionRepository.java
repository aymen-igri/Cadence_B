package com.education.education.session.sharedSession.repositories;

import com.education.education.session.sharedSession.entities.SharedSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SharedSessionRepository extends JpaRepository<SharedSession, UUID> {
    List<SharedSession> findByGroup_Id(UUID groupId);

    Optional<SharedSession> findBySession_IdAndGroup_Id(UUID sessionId, UUID groupId);

    boolean existsBySessionIdAndGroupId(UUID sessionId, UUID groupId);

    @Query("SELECT s FROM SharedSession s WHERE s.session.weekYear < :currentYear OR (s.session.weekYear = :currentYear AND s.session.weekNumber < :currentWeek)")
    List<SharedSession> findExpiredSharedSessions(@Param("currentYear") int currentYear,
            @Param("currentWeek") int currentWeek);
}
