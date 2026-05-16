package com.education.education.session.services;

import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.session.subSession.repositories.SubSessionRepository;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
import com.education.education.session.weeklySessionPlan.services.WeeklySessionPlanService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class SessionExpirationSchedulerService {

    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final SubSessionRepository subSessionRepository;
    private final WeeklySessionPlanService weeklySessionPlanService;
    private static final Logger logger = Logger.getLogger(SessionExpirationSchedulerService.class.getName());

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processActiveWeeklySessions() {
        logger.info("Starting scheduled task: processActiveWeeklySessions");

        try {
            List<WeeklySessionPlan> activeSessions = weeklySessionPlanRepository
                    .findAllBySessionStatus(ESessionStatus.ACTIVE);

            logger.info("Found " + activeSessions.size() + " active sessions to process");

            for (WeeklySessionPlan session : activeSessions) {
                processActiveSession(session);
            }

            logger.info("Successfully completed processActiveWeeklySessions task");
        } catch (Exception e) {
            logger.severe("Error during processActiveWeeklySessions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    private void processActiveSession(WeeklySessionPlan session) {
        logger.info("Processing active session: " + session.getId());

        List<SubSession> subSessions = subSessionRepository
                .findByWeeklySessionPlanOrderByStartTimeAsc(session);

        for (SubSession subSession : subSessions) {
            if (subSession.getSubSessionStatus() == ESubSessionStatus.PENDING && hasSubSessionTimePassed(subSession)) {
                subSession.setSubSessionStatus(ESubSessionStatus.INCOMPLETED);
                subSessionRepository.save(subSession);
                logger.info("Flipped subsession " + subSession.getId() + " to INCOMPLETED");
            }
        }

        weeklySessionPlanService.deriveStatus(session);
    }

    private boolean hasSubSessionTimePassed(SubSession subSession) {
        LocalDate today = LocalDate.now();
        return subSession.getDayOfWeek().getValue() < today.getDayOfWeek().getValue()
                || (subSession.getDayOfWeek() == today.getDayOfWeek()
                        && LocalDateTime.now().toLocalTime().isAfter(subSession.getStartTime()));
    }

}
