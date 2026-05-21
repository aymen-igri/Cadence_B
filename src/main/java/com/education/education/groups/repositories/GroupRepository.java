package com.education.education.groups.repositories;

import com.education.education.groups.entities.Group;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
  @Query("SELECT g FROM Group g " +
      "ORDER BY (" +
      " (SELECT COUNT(m) FROM GroupMessage m WHERE m.group = g) + " +
      " (SELECT COUNT(s) FROM SharedSession s WHERE s.group = g)" +
      ") DESC")
  List<Group> findTopGroups(Pageable pageable);
}
