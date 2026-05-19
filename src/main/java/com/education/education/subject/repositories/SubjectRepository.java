package com.education.education.subject.repositories;

import com.education.education.subject.entities.Subject;
import com.education.education.subject.enums.EPriority;
import com.education.education.user.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
  List<Subject> findByCreatedBy(User createdBy);

  @Query("SELECT COUNT(s) FROM Subject s WHERE s.priority = :priority")
  Number countByPriority(EPriority priority);
}
