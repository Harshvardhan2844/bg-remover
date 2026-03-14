package com.bgremover.security;

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

/**
 * This filter runs on every HTTP request.
 * It checks if the request has a valid JWT token.
 * If yes, it sets the user as "authenticated" in Spring Security.
 *
 * The token is passed in the "Authorization" header like:
 * Authorization: Bearer <token>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Get the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        String email = null;
        String token = null;

        // Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                email = jwtUtil.getEmailFromToken(token);
            } catch (Exception e) {
                // Invalid token, just continue without authentication
                System.out.println("Invalid JWT token: " + e.getMessage());
            }
        }

        // If we got an email and user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Check if token is valid
            if (jwtUtil.validateToken(token)) {
                // Create authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                // Set authentication in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue with the next filter
        filterChain.doFilter(request, response);
    }
}
