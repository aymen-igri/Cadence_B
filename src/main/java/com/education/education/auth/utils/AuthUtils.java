package com.education.education.auth.utils;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
@Getter
public class AuthUtils {
    private String mySecret = Dotenv.load().get("MY_SECRET_KEY");

    public DecodedJWT verifyDecodedJwtToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(mySecret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}

