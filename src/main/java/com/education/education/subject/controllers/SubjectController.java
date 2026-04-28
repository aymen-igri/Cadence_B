package com.education.education.subject.controllers;

import com.education.education.subject.dto.request.CreateSubjectReq;
import com.education.education.subject.dto.request.UpdateSubjectReq;
import com.education.education.subject.dto.response.CreateSubjectRes;
import com.education.education.subject.services.SubjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subject")
@PreAuthorize("hasRole('GENERAL_USER')")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SubjectController {

    private final SubjectService subjectService;
    
    @PostMapping("/create")
    public ResponseEntity<CreateSubjectRes> createSubject(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateSubjectReq subjectReq
    ){
        return ResponseEntity.ok(subjectService.createSubject(subjectReq, userDetails));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CreateSubjectRes>> getAllSubjects(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subjectService.getAllSubjects(userDetails));
    }

    @PatchMapping("/update/{subjectId}")
    public ResponseEntity<CreateSubjectRes> updateSubject(
            @PathVariable UUID subjectId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateSubjectReq subjectReq
    ){
        return ResponseEntity.ok(subjectService.updateSubject(subjectId, subjectReq, userDetails));
    }

    @DeleteMapping("/delete/{subjectId}")
    public ResponseEntity<Void> deleteSubject(
            @PathVariable UUID subjectId,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        subjectService.deleteSubject(subjectId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
