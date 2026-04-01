package com.education.education.user.user.mappers;

import com.education.education.user.user.dto.SignInDTORequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
    public SignInDTORequest toSignInDto(String username, String password){
        return new SignInDTORequest(username, password);
    }
}
