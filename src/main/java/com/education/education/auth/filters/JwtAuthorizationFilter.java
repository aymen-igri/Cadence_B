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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                res.setHeader("error", e.getMessage());
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(req, res);
    }
}