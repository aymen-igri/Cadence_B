package com.education.education.user.user.services;

import com.education.education.user.user.dto.request.AddUserRequest;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.mappers.UserMapper;
import com.education.education.user.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

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
}
