package com.education.education.session.subSession.repositories;

import com.education.education.session.subSession.entities.SubSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubSessionRepository extends JpaRepository<SubSession, UUID> {
}
