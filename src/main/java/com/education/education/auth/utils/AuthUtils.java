package com.education.education.auth.utils;

import com.education.education.auth.deo.responses.SignUpDTOResponse;
import com.education.education.auth.deo.responses.SignUpDTOResponse.Tokens;
import com.education.education.auth.deo.responses.SignUpDTOResponse.AuthUser;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

@Component
@Getter
@RequiredArgsConstructor // spring is the one that inject the dependencies not lombok, works best for non final fields like my secret in our case
public class AuthUtils {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String mySecret;

    public DecodedJWT verifyDecodedJwtToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(this.getMySecret());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public SignUpDTOResponse generateTokenResponse(UserDetails userDetails){

        User user = userRepository.findByUsername(userDetails.getUsername());
        Algorithm algorithm = Algorithm.HMAC256(this.getMySecret());

        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                .withClaim("roles", userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()).toList())
                .sign(algorithm);

        String jwtRefreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                .sign(algorithm);

        String gender = user.getGender() != null ? user.getGender().name() : null;

        String role = user.getRole() != null && !user.getRole().isEmpty() ? user.getRole().get(0).getRole() : null;

        return new SignUpDTOResponse(
            new Tokens(
                jwtAccessToken,
                jwtRefreshToken
            ),
            new AuthUser(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                gender,
                role
            )
        );
    }
}
