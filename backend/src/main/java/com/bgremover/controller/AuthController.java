package com.bgremover.controller;

import com.bgremover.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication Controller.
 * Handles HTTP requests for:
 * - POST /auth/register  → Create new account
 * - POST /auth/login     → Login with email/password
 *
 * These endpoints are PUBLIC (no JWT token needed).
 */
@RestController
@RequestMapping("/auth") // All routes start with /auth
@CrossOrigin(origins = "${app.frontend.url}") // Allow requests from React frontend
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user.
     *
     * Request body example:
     * {
     *   "name": "John Doe",
     *   "email": "john@example.com",
     *   "password": "mypassword123"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");

            // Basic validation
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
            }
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (password == null || password.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
            }

            Map<String, Object> response = authService.register(name, email, password);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Return error message to frontend
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Login with email and password.
     *
     * Request body example:
     * {
     *   "email": "john@example.com",
     *   "password": "mypassword123"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }

            Map<String, Object> response = authService.login(email, password);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password"));
        }
    }
}
