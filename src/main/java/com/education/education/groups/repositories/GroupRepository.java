package com.education.education.groups.repositories;

import com.education.education.groups.DTO.response.ChartGroupsForUserRes;
import com.education.education.groups.DTO.response.GroupSearchResponse;
import com.education.education.groups.entities.Group;
import com.education.education.groups.enums.GroupPrivacy;

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

  @Query("SELECT new com.education.education.groups.DTO.response.ChartGroupsForUserRes(g.name, " +
      "(SELECT COUNT(m) FROM GroupMessage m WHERE m.group = g AND m.sender.id = :userId) + " +
      "(SELECT COUNT(s) FROM SharedSession s WHERE s.group = g AND s.sharedByUser.id = :userId)) " +
      "FROM Group g " +
      "JOIN g.members gm " +
      "WHERE gm.user.id = :userId")
  List<ChartGroupsForUserRes> countActivityForUser(UUID userId);

  @Query("SELECT new com.education.education.groups.DTO.response.GroupSearchResponse(g.id, g.name, g.privacyLevel) FROM Group g "
      +
      "WHERE (:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
      "AND (:privacyLevel IS NULL OR g.privacyLevel = :privacyLevel)")
  List<GroupSearchResponse> searchGroups(String name, GroupPrivacy privacyLevel, Pageable pageable);
}
