package com.education.education.session.services;

import com.education.education.session.dto.request.GenerationSessionReq;
import com.education.education.session.dto.response.CreateSessionRes;
import com.education.education.session.dto.response.GenerationSessionRes;
import com.education.education.session.subSession.dto.response.CreateSubSessionRes;
import com.education.education.session.weeklySessionPlan.dto.response.CreateWeeklySessionRes;
import com.education.education.session.weeklySessionPlan.enums.EPlanStatus;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class GenerationService {

    public GenerationSessionRes generateSession(
            GenerationSessionReq req,
            UserDetails mainUser
    ){
        // code has to be generated
        return null;
    }
}
