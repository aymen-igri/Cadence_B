package com.education.education.auth.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
@Getter
public class AuthUtils {

    @Value("${jwt.secret}")
    private String mySecret;

    public DecodedJWT verifyDecodedJwtToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(mySecret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}

