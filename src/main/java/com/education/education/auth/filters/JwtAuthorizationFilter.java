package com.education.education.auth.filters;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.education.education.auth.utils.AuthUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthUtils authUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (req.getServletPath().equals("/auth/refreshToken")) {
            filterChain.doFilter(req, res);
            return;
        }

        String jwtAuthorizationToken = req.getHeader("Authorization");

        if (jwtAuthorizationToken != null && jwtAuthorizationToken.startsWith("Bearer ")) {
            try {
                String jwt = jwtAuthorizationToken.substring(7);

                Algorithm algorithm = Algorithm.HMAC256(authUtils.getMySecret());
                JWTVerifier jwtVerifier = com.auth0.jwt.JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);

                String username = decodedJWT.getSubject();

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                List<String> rolesFromToken = decodedJWT.getClaim("roles").asList(String.class);

                List<SimpleGrantedAuthority> authorities = rolesFromToken != null
                        ? rolesFromToken.stream()
                          .map(SimpleGrantedAuthority::new)
                          .collect(Collectors.toList())
                        : List.of();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                
                String errorMessage = e.getMessage();
                String responseBody = String.format(
                        "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"%s\"}",
                        errorMessage != null ? errorMessage.replace("\"", "\\\"") : "Invalid or expired token"
                );
                res.getWriter().write(responseBody);
                return;
            }
        }

        filterChain.doFilter(req, res);
    }
}