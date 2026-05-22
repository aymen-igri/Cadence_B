package com.education.education.user.user.mappers;

import com.education.education.groups.DTO.response.ChartGroupsForUserRes;
import com.education.education.session.weeklySessionPlan.dto.response.ChartWeeklySessionPlanForUserRes;
import com.education.education.user.user.dto.request.AddUserRequest;
import com.education.education.user.user.dto.request.SignInDTORequest;
import com.education.education.user.user.dto.request.UpdateUserDataReq;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.dto.response.UpdateUserDataRes;
import com.education.education.user.user.dto.response.UserDetailsRes;
import com.education.education.user.user.dto.response.UserProfileRes;
import com.education.education.user.user.dto.response.UserSearchResponse;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.enums.EGender;
import com.education.education.user.user.enums.EStatus;
import com.education.education.user.user.repositories.UserRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public SignInDTORequest toSignInDto(String username, String password) {
    return new SignInDTORequest(username, password);
  }

  public User toUser(AddUserRequest request) {
    User user = new User();
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setGender(request.gender());
    user.setEmail(request.email());
    user.setPhone(request.phone());
    user.setUsername(request.username());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setStatus(EStatus.ACTIVE);
    return user;
  }

  public AddUserResponse toAddUserResponseDTO(User user) {
    return new AddUserResponse(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getGender(),
        user.getEmail(),
        user.getPhone(),
        user.getUsername(),
        user.getStatus(),
        user.getCreatedAt());
  }

  public User toUser(UserDetails userDetails, UpdateUserDataReq req) {
    User user = userRepository.findByUsername(userDetails.getUsername());

    user.setFirstName(req.firstName());
    user.setLastName(req.lastName());
    user.setGender(req.gender());
    user.setPhone(req.phone());

    return user;
  }

  public UpdateUserDataRes toUpdateUserDataRes(User user) {
    return new UpdateUserDataRes(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getGender(),
        user.getPhone());
  }

  public UserProfileRes toUserProfile(User user) {
    return new UserProfileRes(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getGender(),
        user.getEmail(),
        user.getPhone(),
        user.isTotpEnabled(),
        user.getStatus(),
        user.getProfilePic(),
        user.getRole().get(0).getRole());
  }

  public UserSearchResponse toUserSearchResponse(
      UUID id,
      String firstName,
      String lastName,
      EGender gender,
      String email,
      String phone,
      EStatus status,
      Pageable pageable) {
    return new UserSearchResponse(
        id,
        firstName,
        lastName,
        gender,
        email,
        phone,
        status);
  }

  public UserDetailsRes toUserDetailsRes(
      UUID id,
      String firstName,
      String lastName,
      EGender gender,
      String email,
      String phone,
      EStatus status,
      List<ChartGroupsForUserRes> groupsForUser,
      ChartWeeklySessionPlanForUserRes chartWeeklySessionPlanForUserRes) {
    return new UserDetailsRes(
        id,
        firstName,
        lastName,
        gender,
        email,
        phone,
        status,
        groupsForUser,
        chartWeeklySessionPlanForUserRes);
  }
}
