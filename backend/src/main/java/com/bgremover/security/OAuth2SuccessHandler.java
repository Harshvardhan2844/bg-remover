package com.bgremover.security;

import com.bgremover.model.User;
import com.bgremover.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class handles what happens AFTER a user successfully logs in with Google or GitHub.
 *
 * Steps:
 * 1. Get user info from Google/GitHub
 * 2. Create user in our database if first time login
 * 3. Generate a JWT token
 * 4. Redirect to frontend with the token
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Get the OAuth2 user info (from Google or GitHub)
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Extract email from OAuth2 user attributes
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Determine provider (google or github)
        String registrationId = "google"; // Default
        // We can check the attributes to determine provider

        // Check if user already exists in our database
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // First time login - create new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name != null ? name : email);
            newUser.setProvider("oauth2"); // Mark as OAuth user
            return userRepository.save(newUser);
        });

        // Generate JWT token for this user
        String token = jwtUtil.generateToken(user.getEmail());

        // Redirect to frontend dashboard with token as URL parameter
        // Frontend will extract the token and store it
        String redirectUrl = frontendUrl + "/oauth2/callback?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
