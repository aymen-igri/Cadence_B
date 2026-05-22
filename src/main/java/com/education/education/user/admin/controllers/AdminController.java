package com.education.education.user.admin.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @GetMapping("/cards")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Cards> cards() {
    return ResponseEntity.ok(adminService.cards());
  }

  @GetMapping("/charts/stackedArea")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<StackedAreaChart> stackedAreaChartData() {
    return ResponseEntity.ok(subSessionService.getStackedAreaChartData());
  }

  @GetMapping("/charts/doughnut")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<DoughnutChart> doughnutChartData() {
    return ResponseEntity.ok(subjectService.getDoughnutChartData());
  }

  @GetMapping("/charts/heatMap")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<HeatMapChart> heatMapChartData(
      @RequestBody HeatMapChartReq req) {
    return ResponseEntity.ok(subSessionService.getHeatMapChartData(req.weekNumber(), req.year()));
  }

  @GetMapping("/tables/topGroups")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<List<TopActiveGroupsResponse>> topGroupsTable() {
    return ResponseEntity.ok(groupService.getTopGroups());
  }

  @GetMapping("/tables/mfaActivities")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<List<MfaActivityRes>> mfaActivitiesTable() {
    return ResponseEntity.ok(mfaSessionService.getMfaActivities());
  }

  @PostMapping("/tables/searchUsers")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Page<UserSearchResponse>> searchedUserTable(
      @RequestBody UserTableReq request) {
    return ResponseEntity.ok(userService.getSearchedGeneralUsers(
        request.request(),
        request.page(),
        request.size()));
  }
}
