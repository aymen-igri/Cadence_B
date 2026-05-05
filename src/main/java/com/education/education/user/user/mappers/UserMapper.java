package com.education.education.user.user.mappers;

import com.education.education.user.user.dto.request.AddUserRequest;
import com.education.education.user.user.dto.request.SignInDTORequest;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.dto.response.UserProfileRes;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.enums.EStatus;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.education.education.user.role.entities.Role;

@Component
@AllArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public SignInDTORequest toSignInDto(String username, String password){
        return new SignInDTORequest(username, password);
    }

    public User toUser(AddUserRequest request){
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

    public AddUserResponse toAddUserResponseDTO(User user){
        return new AddUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getEmail(),
                user.getPhone(),
                user.getUsername(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }

    public UserProfileRes toUserProfile(User user){
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
                user.getRole().get(0).getRole()
        );
    }
}
