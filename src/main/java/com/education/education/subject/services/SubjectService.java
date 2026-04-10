package com.education.education.subject.services;

import com.education.education.subject.dto.request.CreateSubjectReq;
import com.education.education.subject.dto.response.CreateSubjectRes;
import com.education.education.subject.entities.Subject;
import com.education.education.subject.mappers.SubjectMapper;
import com.education.education.subject.repositories.SubjectRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final SubjectMapper subjectMapper;

    public CreateSubjectRes createSubject(
            CreateSubjectReq subjectReq,
            UserDetails userDetails
    ){
        Subject createSubject = subjectMapper.subjectReqToSubject(subjectReq);

        User createdBy = userRepository.findByUsername(userDetails.getUsername());
        createSubject.setCreatedBy(createdBy);

        subjectRepository.save(createSubject);

        return subjectMapper.subjectToSubjectRes(createSubject);
    }
}
