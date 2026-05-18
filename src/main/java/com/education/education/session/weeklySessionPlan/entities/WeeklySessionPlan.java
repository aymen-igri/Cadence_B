package com.education.education.session.weeklySessionPlan.entities;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.base.auditableEntity.AuditableEntity;
import com.education.education.session.task.entities.Task;
import com.education.education.session.sharedSession.entities.SharedSession;
import com.education.education.session.weeklySessionPlan.enums.EGenerationAlgoType;
import com.education.education.session.weeklySessionPlan.enums.EGenerationType;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.session.subSession.entities.SubSession;
import com.education.education.user.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "weekly_session", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "week_year",
        "week_number" }))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeeklySessionPlan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "session_id")
    private UUID id;

    @Column(nullable = true)
    private String title;

    @Column(name = "week_year", nullable = false)
    private Integer weekYear;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ESessionStatus sessionStatus = ESessionStatus.UPCOMING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EGenerationType generationType = EGenerationType.MANUAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EPlanStatus planStatus = EPlanStatus.DRAFT;

    @Column(nullable = false)
    private Long penaltyPoints = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EGenerationAlgoType generationAlgoType = EGenerationAlgoType.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_plan_id")
    private AvailabilityPlan availabilityPlan;

    @OneToMany(mappedBy = "weeklySessionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubSession> subSessions;

    @OneToMany(mappedBy = "weeklySessionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> createdTasks;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharedSession> sharedSessions;

    @Transient
    public LocalDateTime getStartTime() {
        if (weekYear == null || weekNumber == null) {
            return null;
        }

        WeekFields weekFields = WeekFields.ISO;
        return java.time.LocalDate.of(weekYear, 1, 4)
                .with(weekFields.weekOfWeekBasedYear(), weekNumber)
                .with(weekFields.dayOfWeek(), 1)
                .atStartOfDay();
    }

    @Transient
    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            this.weekYear = null;
            this.weekNumber = null;
            return;
        }

        WeekFields weekFields = WeekFields.ISO;
        this.weekYear = startTime.get(weekFields.weekBasedYear());
        this.weekNumber = startTime.get(weekFields.weekOfWeekBasedYear());
    }
}