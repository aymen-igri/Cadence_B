package com.education.education.user.user.services;

import com.education.education.groups.DTO.response.ChartGroupsForUserRes;
import com.education.education.groups.repositories.GroupRepository;
import com.education.education.session.weeklySessionPlan.dto.response.ChartWeeklySessionPlanForUserRes;
import com.education.education.session.weeklySessionPlan.enums.ESessionStatus;
import com.education.education.session.weeklySessionPlan.mappers.WeeklySessionPlanMapper;
import com.education.education.session.weeklySessionPlan.repositories.WeeklySessionPlanRepository;
import com.education.education.user.role.dto.request.AddRoleToUserRequest;
import com.education.education.user.role.services.RoleService;
import com.education.education.user.user.dto.request.AddUserRequest;
import com.education.education.user.user.dto.request.UpdateUserDataReq;
import com.education.education.user.user.dto.request.UserSearchRequest;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.dto.response.UpdateUserDataRes;
import com.education.education.user.user.dto.response.UserDetailsRes;
import com.education.education.user.user.dto.response.UserProfileRes;
import com.education.education.user.user.dto.response.UserSearchResponse;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.mappers.UserMapper;
import com.education.education.user.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final RoleService roleService;
  private final WeeklySessionPlanRepository weeklySessionPlanRepository;
  private final GroupRepository groupRepository;
  private final WeeklySessionPlanMapper weeklySessionPlanMapper;

  public AddUserResponse addUser(AddUserRequest request) {
    User user = userMapper.toUser(request);

    if (userRepository.findByUsername(user.getUsername()) != null) {
      throw new IllegalArgumentException("user already exists, change the username");
    }

    if (userRepository.findByEmail(user.getEmail()) != null) {
      throw new IllegalArgumentException("user already exists, change the email");
    }

    userRepository.save(user);
    return userMapper.toAddUserResponseDTO(user);
  }

  // for general user creation with different roles
  public AddUserResponse createUser(AddUserRequest request, String roleName) {
    AddUserResponse addUserResponse = this.addUser(request);
    AddRoleToUserRequest addRoleToUserRequest = new AddRoleToUserRequest(
        addUserResponse.username(),
        roleName);
    roleService.addRoleToUser(addRoleToUserRequest);

    return addUserResponse;
  }

  public UpdateUserDataRes updateUserData(UserDetails userDetails, UpdateUserDataReq req) {
    User user = userMapper.toUser(userDetails, req);
    userRepository.save(user);
    return userMapper.toUpdateUserDataRes(user);
  }

  public UserProfileRes profile(UserDetails userDetails) {
    User user = userRepository.findByUsername(userDetails.getUsername());
    return userMapper.toUserProfile(user);
  }

  public void updatePFP(UserDetails userDetails, String imageURL) {
    User user = userRepository.findByUsername(userDetails.getUsername());
    if (user == null)
      throw new IllegalArgumentException("User not found");

    user.setProfilePic(imageURL);
    userRepository.save(user);
  }

  private String clean(String s) {
    if (s == null || s.trim().isEmpty()) {
      return null;
    }
    return "%" + s.trim().toLowerCase() + "%";
  }

  public Page<UserSearchResponse> getSearchedGeneralUsers(UserSearchRequest request, int page, int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    String fName = this.clean(request.firstName());
    String lName = this.clean(request.lastName());
    String email = this.clean(request.email());
    String phone = this.clean(request.phone());

    Page<User> userPage = userRepository.searchGeneralUser(
        fName,
        lName,
        email,
        phone,
        request.gender(),
        request.status(),
        pageable);

    return userPage.map(u -> userMapper.toUserSearchResponse(
        u.getId(),
        u.getFirstName(),
        u.getLastName(),
        u.getGender(),
        u.getEmail(),
        u.getPhone(),
        u.getStatus()));
  }

  public UserDetailsRes getUserDetails(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    ChartWeeklySessionPlanForUserRes sessionsStatis = weeklySessionPlanMapper.toChartWeeklySessionPlanForUserRes(
        weeklySessionPlanRepository.countTotalByUserId(userId),
        weeklySessionPlanRepository.countByStatus(ESessionStatus.COMPLETED, userId),
        weeklySessionPlanRepository.countByStatus(ESessionStatus.ACTIVE, userId),
        weeklySessionPlanRepository.countByStatus(ESessionStatus.UPCOMING, userId),
        weeklySessionPlanRepository.countByStatus(ESessionStatus.INCOMPLETED, userId));

    List<ChartGroupsForUserRes> groupStatis = groupRepository.countActivityForUser(userId);

    return userMapper.toUserDetailsRes(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getGender(),
        user.getEmail(),
        user.getPhone(),
        user.getStatus(),
        groupStatis,
        sessionsStatis);
  }

  public void banUser(UUID userId) {
    userRepository.banUser(userId);
  }

  public void unbanUser(UUID userId) {
    userRepository.unbanUser(userId);
  }
}
