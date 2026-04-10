package com.education.education.session.subSession.entities;

import com.education.education.base.auditableEntity.AuditableEntity;
import com.education.education.session.WeeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.subject.entities.Subject;
import com.education.education.user.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subSession")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubSession extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SubSession_id")
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ESubSessionStatus subSessionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private WeeklySessionPlan weeklySessionPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
