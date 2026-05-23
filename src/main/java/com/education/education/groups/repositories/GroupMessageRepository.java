package com.education.education.groups.repositories;

import com.education.education.groups.DTO.response.GroupMessageActivityDataRes;
import com.education.education.groups.entities.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, UUID> {
  List<GroupMessage> findByGroupIdOrderBySentAtAsc(UUID groupId);

  Page<GroupMessage> findByGroupId(UUID groupId, Pageable pageable);

  @Query("SELECT new com.education.education.groups.DTO.response.GroupMessageActivityDataRes(CAST(m.sentAt AS LocalDate), COUNT(m)) "
      +
      "FROM GroupMessage m " +
      "WHERE m.group.id = :groupId " +
      "GROUP BY CAST(m.sentAt AS LocalDate) " +
      "ORDER BY CAST(m.sentAt AS LocalDate) DESC")
  List<GroupMessageActivityDataRes> findMessageActivityByGroupId(UUID groupId);
}
