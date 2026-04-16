package com.education.education.session.weeklySessionPlan.entities;

import com.education.education.availability.availabilityPlan.entities.AvailabilityPlan;
import com.education.education.base.auditableEntity.AuditableEntity;
import com.education.education.session.weeklySessionPlan.enums.EGenerationType;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.user.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weekly_session")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeeklySessionPlan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "session_id")
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ESessionStatus sessionStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EGenerationType generationType = EGenerationType.MANUAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EPlanStatus planStatus;

    @Column(nullable = false)
    private Long penaltyPoints = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_plan_id")
    private AvailabilityPlan availabilityPlan;
}