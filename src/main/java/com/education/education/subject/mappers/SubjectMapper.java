package com.education.education.subject.mappers;

import com.education.education.subject.dto.request.CreateSubjectReq;
import com.education.education.subject.dto.response.CreateSubjectRes;
import com.education.education.subject.entities.Subject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SubjectMapper {

    public CreateSubjectRes subjectToSubjectRes(Subject subject){
        return new CreateSubjectRes(
                subject.getId(),
                subject.getName(),
                subject.getPriority(),
                subject.getDescription(),
                subject.getCreatedAt()
        );
    }

    public Subject subjectReqToSubject(CreateSubjectReq subjectReq){
        Subject subject = new Subject();
        subject.setName(subjectReq.name());
        subject.setPriority(subjectReq.priority());
        subject.setDescription(subjectReq.description());

        return subject;
    }
}
