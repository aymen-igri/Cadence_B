package com.education.education.auth.services;

import com.education.education.auth.deo.requests.SignUpDTORequest;
import com.education.education.auth.deo.responses.SignUpDTOResponse;
import com.education.education.auth.mappers.AuthMapper;
import com.education.education.user.generalUser.entities.GeneralUser;
import com.education.education.user.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Transactional
    public SignUpDTOResponse signUp(SignUpDTORequest request){
        GeneralUser user = authMapper.toGeneralUser(request);
        userRepository.save(user);
        return authMapper.toSignUpResponse(user);
    }

}
