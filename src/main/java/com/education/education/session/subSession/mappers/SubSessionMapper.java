package com.education.education.session.subSession.mappers;

import com.education.education.session.subSession.dto.request.CreateSubSessionReq;
import com.education.education.session.subSession.dto.request.UpdateSubSessionReq;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.subSession.entities.SubSession;
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
        subSession.setSubject(subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found")));

        return subSession;
    }

    public SubSession toSubSession(UpdateSubSessionReq request) {
        SubSession subSession = new SubSession();
        subSession.setDayOfWeek(request.dayOfWeek());
        subSession.setStartTime(request.startTime());
        subSession.setEndTime(request.endTime());
        subSession.setSubSessionStatus(request.status());
        subSession.setSubject(subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found")));

        return subSession;
    }

    public void updateSubSessionFromReq(UpdateSubSessionReq request, SubSession subSession) {
        if (request.dayOfWeek() != null) {
            subSession.setDayOfWeek(request.dayOfWeek());
        }
        if (request.startTime() != null) {
            subSession.setStartTime(request.startTime());
        }
        if (request.endTime() != null) {
            subSession.setEndTime(request.endTime());
        }
        if (request.status() != null) {
            subSession.setSubSessionStatus(request.status());
        }
        if (request.subjectId() != null) {
            subSession.setSubject(subjectRepository.findById(request.subjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found")));
        }
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
