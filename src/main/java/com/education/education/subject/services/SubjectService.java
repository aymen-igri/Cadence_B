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

import java.util.List;
import java.util.stream.Collectors;

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
        User createdBy = userRepository.findByUsername(userDetails.getUsername());
        if (createdBy == null) {
            throw new IllegalArgumentException("User not found");
        }

        Subject createSubject = subjectMapper.subjectReqToSubject(subjectReq);
        createSubject.setCreatedBy(createdBy);

        Subject savedSubject = subjectRepository.save(createSubject);

        return new CreateSubjectRes(
                savedSubject.getId(),
                savedSubject.getName(),
                savedSubject.getPriority(),
                savedSubject.getDescription(),
                savedSubject.getCreatedAt()
        );
    }

    public List<CreateSubjectRes> getAllSubjects(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return subjectRepository.findByCreatedBy(user).stream()
                .map(subject -> new CreateSubjectRes(
                        subject.getId(),
                        subject.getName(),
                        subject.getPriority(),
                        subject.getDescription(),
                        subject.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
