package com.education.education.auth.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.education.education.auth.deo.responses.SignUpDTOResponse;
import com.education.education.auth.utils.AuthUtils;
import com.education.education.user.role.entities.Role;
import com.education.education.user.role.services.RoleService;
import com.education.education.user.user.dto.request.AddUserRequest;
import com.education.education.user.user.dto.response.AddUserResponse;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import com.education.education.user.user.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final AuthUtils authUtils;
    private final UserService userService;
    private final RoleService roleService;

    public SignUpDTOResponse signUp(AddUserRequest request){
        AddUserResponse addUserResponse = userService.createUser(request, "ROLE_GENERAL_USER");

        Algorithm algorithm = Algorithm.HMAC256(authUtils.getMySecret());
        String jwtAccessToken = JWT.create()
                .withSubject(addUserResponse.username())
                .withExpiresAt(new Date(System.currentTimeMillis()*10*60*1000))
                .withClaim("roles", List.of("ROLE_GENERAL_USER"))
                .sign(algorithm);

        String jwtRefreshToken = JWT.create()
                .withSubject(addUserResponse.username())
                .sign(algorithm);

        SignUpDTOResponse.Tokens tokens = new SignUpDTOResponse.Tokens(jwtAccessToken, jwtRefreshToken);
        SignUpDTOResponse.AuthUser user = new SignUpDTOResponse.AuthUser(
                addUserResponse.id(),
                addUserResponse.firstName(),
                addUserResponse.LastName(),
                addUserResponse.username(),
                addUserResponse.email(),
                addUserResponse.phone(),
                addUserResponse.gender() != null ? addUserResponse.gender().name() : null,
                "ROLE_GENERAL_USER"
        );

        return new SignUpDTOResponse(tokens, user);
    }

    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, String> requestBody = mapper.readValue(req.getInputStream(), Map.class);
        String jwt = requestBody.get("refreshToken");

        if(jwt != null && !jwt.isEmpty()){
            try{
                Algorithm algorithm = Algorithm.HMAC256(authUtils.getMySecret());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                String username = decodedJWT.getSubject();
                User user = userRepository.findByUsername(username);
                // create access token
                String jwtAccessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000)) // access token got 10 minutes before it expires
                        .withIssuer(req.getRequestURL().toString())
                        .withClaim("roles", user.getRole().stream().map(Role::getRole).toList())
                        .sign(algorithm);

                Map<String, String> idToken = new HashMap<>();
                idToken.put("accessToken", jwtAccessToken);
                idToken.put("refreshToken", jwt);
                res.setContentType("application/json");
                new ObjectMapper().writeValue(res.getOutputStream(), idToken);

            }catch(Exception e){
                System.out.println("Refresh token error: " + e.getMessage());
                throw new AccessDeniedException(e.getMessage());
            }
        }else{
            throw new IllegalArgumentException("Refresh token is missing from the request body");
        }
    }
}
