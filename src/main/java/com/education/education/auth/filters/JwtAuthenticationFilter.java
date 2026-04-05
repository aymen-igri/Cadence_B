package com.education.education.auth.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.education.education.auth.utils.AuthUtils;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import com.education.education.user.user.wrapper.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AuthUtils authUtils;
    private final UserRepository userRepository;
    

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res){
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, String> credentials = mapper.readValue(req.getInputStream(), Map.class);
            
            String username = credentials.get("username");
            if (username == null) {
                username = credentials.get("email");
            }
            String password = credentials.get("password");

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request body", e);
        }
    }

    @Override
    public void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain,
            Authentication authResult
    )throws IOException{
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());

        Algorithm algorithm = Algorithm.HMAC256(authUtils.getMySecret());
        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000)) // access token got 10 minutes before it expire
                .withIssuer(req.getRequestURL().toString())
                .withClaim("roles", userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()).toList())
                .sign(algorithm);

        String jwtRefreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(req.getRequestURL().toString())
                .sign(algorithm);

        Map<String, String> idToken = new HashMap<>();
        idToken.put("access-token", jwtAccessToken);
        idToken.put("refresh-token", jwtRefreshToken);
        res.setContentType("application/json");
        new ObjectMapper().writeValue(res.getOutputStream(), idToken);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException failed
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 401);
        errorDetails.put("message", "Invalid credentials");
        new ObjectMapper().writeValue(response.getOutputStream(), errorDetails);
    }
}
