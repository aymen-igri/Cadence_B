package com.education.education.session.services;

import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.session.subSession.repositories.SubSessionRepository;
import com.education.education.session.weeklySessionPlan.entities.WeeklySessionPlan;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class SessionExpirationSchedulerService {

    private final WeeklySessionPlanRepository weeklySessionPlanRepository;
    private final SubSessionRepository subSessionRepository;
    private static final Logger logger = Logger.getLogger(SessionExpirationSchedulerService.class.getName());

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeExpiredWeeklySessions() {
        logger.info("Starting scheduled task: closeExpiredWeeklySessions");

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysAgo = now.minusDays(7);

            // Find all sessions that have expired and are not already CLOSED
            List<WeeklySessionPlan> expiredSessions = weeklySessionPlanRepository
                    .findAllByStartTimeBeforeAndSessionStatusNot(sevenDaysAgo, ESessionStatus.CLOSED);

            logger.info("Found " + expiredSessions.size() + " expired sessions to process");

            for (WeeklySessionPlan session : expiredSessions) {
                processExpiredSession(session);
            }

            logger.info("Successfully completed closeExpiredWeeklySessions task");
        } catch (Exception e) {
            logger.severe("Error during closeExpiredWeeklySessions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    private void processExpiredSession(WeeklySessionPlan session) {
        logger.info("Processing expired session: " + session.getId());

        // Get all subsessions for this session
        List<SubSession> subSessions = subSessionRepository
                .findByWeeklySessionPlanOrderByStartTimeAsc(session);

        // Flip PENDING subsessions to INCOMPLETED
        for (SubSession subSession : subSessions) {
            if (subSession.getSubSessionStatus() == ESubSessionStatus.PENDING) {
                subSession.setSubSessionStatus(ESubSessionStatus.INCOMPLETED);
                subSessionRepository.save(subSession);
                logger.info("Flipped subsession " + subSession.getId() + " to INCOMPLETED");
            }
        }

        // Determine the final status of the weekly session
        boolean allCompleted = subSessions.stream()
                .allMatch(ss -> ss.getSubSessionStatus() == ESubSessionStatus.COMPLETED);

        if (allCompleted) {
            session.setSessionStatus(ESessionStatus.COMPLETED);
            logger.info("Set session " + session.getId() + " to COMPLETED");
        } else {
            session.setSessionStatus(ESessionStatus.CLOSED);
            logger.info("Set session " + session.getId() + " to CLOSED");
        }

        weeklySessionPlanRepository.save(session);
    }
}
