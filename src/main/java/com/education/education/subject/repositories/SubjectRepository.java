package com.education.education.subject.repositories;

import com.education.education.subject.entities.Subject;
import com.education.education.user.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    List<Subject> findByCreatedBy(User createdBy);
}
