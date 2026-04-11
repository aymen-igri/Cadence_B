package com.education.education.session.subSession.mappers;

import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.entities.SubSession;
import com.education.education.session.subSession.enums.ESubSessionStatus;
import com.education.education.session.subSession.repositories.SubSessionRepository;
import com.education.education.subject.repositories.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SubSessionMapper {

    private final SubjectRepository subjectRepository;

    public SubSession toSubSession(CreateSubSessionReq request){
        SubSession subSession = new SubSession();
        subSession.setDayOfWeek(request.dayOfWeek());
        subSession.setStartTime(request.startTime());
        subSession.setEndTime(request.endTime());
        subSession.setSubSessionStatus(request.status());
        subSession.setSubject(subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found")));

        return subSession;
    }

    public CreateSubSessionRes toCreateSubSessionRes(SubSession subSection){
        return new CreateSubSessionRes(
                subSection.getId(),
                subSection.getDayOfWeek(),
                subSection.getStartTime(),
                subSection.getEndTime(),
                subSection.getSubSessionStatus(),
                subSection.getSubject().getId(),
                subSection.getSubject().getName()
        );
    }
}
