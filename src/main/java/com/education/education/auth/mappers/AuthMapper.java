package com.education.education.auth.mappers;

import com.education.education.auth.deo.requests.SignUpDTORequest;
import com.education.education.auth.deo.responses.SignUpDTOResponse;
import com.education.education.user.generalUser.entities.GeneralUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthMapper {

    public GeneralUser toGeneralUser(SignUpDTORequest request){
        GeneralUser user = new GeneralUser();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setGender(request.gender());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setUsername(request.username());
        user.setPassword(request.password());
        return user;
    }

    public SignUpDTOResponse toSignUpResponse(GeneralUser user){
        return new SignUpDTOResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getEmail(),
                user.getPhone(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }
}
