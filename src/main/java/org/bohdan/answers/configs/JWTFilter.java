package org.bohdan.answers.configs;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.security.JWTUtil;
import org.bohdan.answers.api.services.UserEntityDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
public class JWTFilter extends OncePerRequestFilter {

    JWTUtil jwtUtil;
    UserEntityDetailsService userEntityDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            if (jwt.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid JWT Token In Bearer Header");
            } else {
                try {
                    String username = jwtUtil.validateTokenAndRetrieveClaim(jwt);
                    UserDetails userDetails = userEntityDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    userDetails.getPassword(),
                                    userDetails.getAuthorities()
                            );

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } catch (JWTVerificationException ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Invalid JWT Token");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
