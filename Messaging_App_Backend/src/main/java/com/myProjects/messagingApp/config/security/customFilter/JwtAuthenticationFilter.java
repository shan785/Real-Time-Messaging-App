package com.myProjects.messagingApp.config.security.customFilter;

import com.myProjects.messagingApp.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeaderValue = request.getHeader("Authorization");
        String jwtToken = null;

        if(authHeaderValue != null && authHeaderValue.startsWith("Bearer ")) {
            jwtToken = authHeaderValue.substring(7);
            Claims claims = null;
            try {
                claims = jwtUtil.extractAllClaims(jwtToken);
                String username = claims.getSubject();
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if(!jwtUtil.isTokenExpired(claims.getExpiration())) {
                    UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            catch (ExpiredJwtException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }catch (JwtException ex) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }

        filterChain.doFilter(request, response);

    }
}
