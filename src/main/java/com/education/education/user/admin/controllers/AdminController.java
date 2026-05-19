package com.education.education.user.admin.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.education.education.user.admin.dto.res.Cards;
import com.education.education.user.admin.services.AdminService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@SecurityRequirement(name = "bererAuth")
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/cards")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Cards> cards() {
    return ResponseEntity.ok(adminService.cards());
  }

}
