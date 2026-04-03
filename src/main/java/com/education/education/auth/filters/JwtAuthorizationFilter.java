package com.education.education.auth.filters;

import com.auth0.jwt.JWT;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthUtils authUtils;
    
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        if (req.getServletPath().equals("/auth/refreshToken")){
            filterChain.doFilter(req,res);
        } else {
            String jwtAuthorizationToken = req.getHeader("Authorization");
            if(jwtAuthorizationToken != null && jwtAuthorizationToken.startsWith("Bearer ")){
                try{
                    String jwt = jwtAuthorizationToken.substring(7);
                    Algorithm algorithm = Algorithm.HMAC256(authUtils.getMySecret());
                    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<GrantedAuthority> authorityCollection = new ArrayList<>();
                    for(String r:roles){
                        authorityCollection.add(new SimpleGrantedAuthority(r));
                    }
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null,authorityCollection);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(req, res);
                }catch(Exception e){
                    res.setHeader("error", e.getMessage());
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
            }else{
                filterChain.doFilter(req, res);
            }
        }
    }
}
