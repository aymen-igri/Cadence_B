package com.education.education.auth.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.education.education.auth.deo.responses.SignUpDTOResponse;
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
import java.util.*;

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
            
            String identifier = credentials.get("identifier");
            String password = credentials.get("password");

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(identifier, password);
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

        boolean mfaRequired = user.isTotpEnabled(); // i will delete true because is just for devlopement phase
        Algorithm algorithm = Algorithm.HMAC256(authUtils.getMySecret());

        if (mfaRequired) {
            String mfaToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 *1000))
                    .withClaim("roles", List.of("ROLE_PRE_AUTH"))
                    .sign(algorithm);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("mfaRequired", true);
            responseBody.put("mfaToken", mfaToken);

            List<String> methods = new ArrayList<>();
            methods.add("Email");
            methods.add("SMS");
            if (user.isTotpEnabled()) methods.add("Authenticator App");
            responseBody.put("availableMethods", methods);

            res.setContentType("application/json");
            new ObjectMapper().writeValue(res.getOutputStream(), responseBody);
        }else{
            String jwtAccessToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                    .withIssuer(req.getRequestURL().toString())
                    .withClaim("roles", userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()).toList())
                    .sign(algorithm);

            String jwtRefreshToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)) // refresh token got 30 days before it expire
                    .withIssuer(req.getRequestURL().toString())
                    .sign(algorithm);

            SignUpDTOResponse.Tokens tokens = new SignUpDTOResponse.Tokens(jwtAccessToken, jwtRefreshToken);

            String roleStr = user.getRole() != null && !user.getRole().isEmpty() ? user.getRole().get(0).getRole() : null;

            SignUpDTOResponse.AuthUser authUser = new SignUpDTOResponse.AuthUser(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getGender() != null ? user.getGender().name() : null,
                    roleStr
            );

            SignUpDTOResponse responseBody = new SignUpDTOResponse(tokens, authUser);

            res.setContentType("application/json");
            new ObjectMapper().writeValue(res.getOutputStream(), responseBody);
        }
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
