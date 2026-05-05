package com.education.education.user.user.services;

import com.education.education.user.role.dto.request.AddRoleToUserRequest;
import com.education.education.user.role.services.RoleService;
import com.education.education.user.user.dto.request.AddUserRequest;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.dto.response.UserProfileRes;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.mappers.UserMapper;
import com.education.education.user.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleService roleService;

    public AddUserResponse addUser(AddUserRequest request){
        User user = userMapper.toUser(request);

        if(userRepository.findByUsername(user.getUsername()) != null){
            throw new IllegalArgumentException("user already exists, change the username");
        }

        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new IllegalArgumentException("user already exists, change the email");
        }

        userRepository.save(user);
        return userMapper.toAddUserResponseDTO(user);
    }

    // for general user creation with different roles
    public AddUserResponse createUser(AddUserRequest request, String roleName){
        AddUserResponse addUserResponse = this.addUser(request);
        AddRoleToUserRequest addRoleToUserRequest = new AddRoleToUserRequest(
                addUserResponse.username(),
                roleName
        );
        roleService.addRoleToUser(addRoleToUserRequest);

        return addUserResponse;
    }

    public UserProfileRes profile(UserDetails userDetails){
        User user = userRepository.findByUsername(userDetails.getUsername());
        return userMapper.toUserProfile(user);
    }
}
