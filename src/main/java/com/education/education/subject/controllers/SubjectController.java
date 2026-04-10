package com.education.education.subject.controllers;

import com.education.education.subject.dto.request.CreateSubjectReq;
import com.education.education.subject.dto.response.CreateSubjectRes;
import com.education.education.subject.services.SubjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subject")
@PreAuthorize("hasAuthority('GENERAL_USER')")
@AllArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    
    @PostMapping("/create")
    public ResponseEntity<CreateSubjectRes> createSubject(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateSubjectReq subjectReq
    ){
        return ResponseEntity.ok(subjectService.createSubject(subjectReq, userDetails));
    }
}
