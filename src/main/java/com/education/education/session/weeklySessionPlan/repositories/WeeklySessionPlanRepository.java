package com.education.education.session.weeklySessionPlan.repositories;

import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.user.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WeeklySessionPlanRepository extends JpaRepository<WeeklySessionPlan, UUID> {
  WeeklySessionPlan findByUser_IdAndWeekYearAndWeekNumber(UUID userId, Integer weekYear, Integer weekNumber);

  boolean existsByUser_IdAndWeekYearAndWeekNumber(UUID userId, Integer weekYear, Integer weekNumber);

  List<WeeklySessionPlan> findAllByUser_IdOrderByWeekYearDescWeekNumberDesc(UUID userId);

  List<WeeklySessionPlan> findAllBySessionStatus(ESessionStatus sessionStatus);

  @Query("select w from WeeklySessionPlan w where w.user = :user order by w.weekYear desc, w.weekNumber desc")
  List<WeeklySessionPlan> findByUserOrderByStartTimeDesc(@Param("user") User user);

  @Query("select w from WeeklySessionPlan w where w.user = :user and w.planStatus = :planStatus order by w.weekYear desc, w.weekNumber desc")
  List<WeeklySessionPlan> findByUserAndPlanStatusOrderByStartTimeDesc(
      @Param("user") User user,
      @Param("planStatus") EPlanStatus planStatus);

  @Query("select w from WeeklySessionPlan w where w.sessionStatus <> :status")
  List<WeeklySessionPlan> findAllBySessionStatusNot(@Param("status") ESessionStatus status);

  default List<WeeklySessionPlan> findAllByStartTimeBeforeAndSessionStatusNot(
      LocalDateTime endTime,
      ESessionStatus status) {
    if (endTime == null) {
      return List.of();
    }

    java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.ISO;
    int targetWeekYear = endTime.get(weekFields.weekBasedYear());
    int targetWeekNumber = endTime.get(weekFields.weekOfWeekBasedYear());

    return findAllBySessionStatusNot(status).stream()
        .filter(plan -> plan.getWeekYear() != null && plan.getWeekNumber() != null)
        .filter(plan -> plan.getWeekYear() < targetWeekYear
            || (plan.getWeekYear().equals(targetWeekYear) && plan.getWeekNumber() < targetWeekNumber))
        .toList();
  }

  @Query("SELECT COUNT(w) FROM WeeklySessionPlan w WHERE w.sessionStatus = :status AND w.user.id = :userId")
  Integer countByStatus(ESessionStatus status, UUID userId);

  @Query("SELECT COUNT(w) FROM WeeklySessionPlan w WHERE w.user.id = :userId")
  Integer countTotalByUserId(UUID userId);
}
