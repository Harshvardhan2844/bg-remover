package com.bgremover.service;

import com.bgremover.model.User;
import com.bgremover.repository.UserRepository;
import com.bgremover.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * This service handles user registration and login logic.
 * Controllers call this service to perform auth operations.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Register a new user with email and password.
     *
     * @param name     User's full name
     * @param email    User's email
     * @param password Plain text password (we will hash it before saving)
     * @return Map containing JWT token and user info
     */
    public Map<String, Object> register(String name, String email, String password) {
        // Check if email is already registered
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already registered. Please login instead.");
        }

        // Create new user object
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hash the password!
        user.setProvider("local");

        // Save to database
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(email);

        // Return token and user info
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("message", "Registration successful!");
        return response;
    }

    /**
     * Login with email and password.
     *
     * @param email    User's email
     * @param password Plain text password
     * @return Map containing JWT token and user info
     */
    public Map<String, Object> login(String email, String password) {
        // This will throw an exception if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // If we reach here, credentials are correct
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        String token = jwtUtil.generateToken(email);

        // Return token and user info
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("message", "Login successful!");
        return response;
    }
}
