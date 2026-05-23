package com.education.education.user.admin.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.education.education.auth.deo.responses.MfaActivityRes;
import com.education.education.auth.services.MfaSessionService;
import com.education.education.groups.DTO.response.TopActiveGroupsResponse;
import com.education.education.groups.services.GroupService;
import com.education.education.session.subSession.dto.request.HeatMapChartReq;
import com.education.education.session.subSession.dto.response.HeatMapChart;
import com.education.education.session.subSession.dto.response.StackedAreaChart;
import com.education.education.session.subSession.services.SubSessionService;
import com.education.education.subject.dto.response.DoughnutChart;
import com.education.education.subject.services.SubjectService;
import com.education.education.user.admin.dto.res.Cards;
import com.education.education.user.admin.services.AdminService;
import com.education.education.user.user.dto.request.UserTableReq;
import com.education.education.user.user.dto.response.UserDetailsRes;
import com.education.education.user.user.dto.response.UserSearchResponse;
import com.education.education.user.user.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@SecurityRequirement(name = "bererAuth")
public class AdminController {

  private final AdminService adminService;
  private final SubSessionService subSessionService;
  private final SubjectService subjectService;
  private final GroupService groupService;
  private final MfaSessionService mfaSessionService;
  private final UserService userService;

  @GetMapping("/dashboard/cards")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Cards> cards() {
    return ResponseEntity.ok(adminService.cards());
  }

  @GetMapping("/dashboard/charts/stackedArea")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<StackedAreaChart> stackedAreaChartData() {
    return ResponseEntity.ok(subSessionService.getStackedAreaChartData());
  }

  @GetMapping("/dashboard/charts/doughnut")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<DoughnutChart> doughnutChartData() {
    return ResponseEntity.ok(subjectService.getDoughnutChartData());
  }

  @PostMapping("/dashboard/charts/heatMap")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<HeatMapChart> heatMapChartData(
      @RequestBody HeatMapChartReq req) {
    return ResponseEntity.ok(subSessionService.getHeatMapChartData(req.weekNumber(), req.year()));
  }

  @GetMapping("/dashboard/tables/topGroups")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<List<TopActiveGroupsResponse>> topGroupsTable() {
    return ResponseEntity.ok(groupService.getTopGroups());
  }

  @GetMapping("/dashboard/tables/mfaActivities")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<List<MfaActivityRes>> mfaActivitiesTable() {
    return ResponseEntity.ok(mfaSessionService.getMfaActivities());
  }

  @PostMapping("/users/tables/searchUsers")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Page<UserSearchResponse>> searchedUserTable(
      @RequestBody UserTableReq request) {
    return ResponseEntity.ok(userService.getSearchedGeneralUsers(
        request.request(),
        request.page(),
        request.size()));
  }

  @GetMapping("/users/tables/userDetails")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserDetailsRes> userDetailsTable(
      @RequestParam UUID userId) {
    return ResponseEntity.ok(userService.getUserDetails(userId));
  }

  @PatchMapping("/users/ban")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Void> banUser(
      @RequestParam UUID userId) {
    userService.banUser(userId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/users/unban")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Void> unbanUser(
      @RequestParam UUID userId) {
    userService.unbanUser(userId);
    return ResponseEntity.ok().build();
  }
}
