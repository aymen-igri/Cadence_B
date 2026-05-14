package com.education.education.goal.entities;

import com.education.education.base.auditableEntity.AuditableEntity;
import com.education.education.subject.entities.Subject;
import com.education.education.user.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "goals")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Goal extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "goal_id")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, name = "hours")
    private float targetHoursPerWeek;

    private float progress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
