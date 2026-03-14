package com.bgremover.controller;

import com.bgremover.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Image Controller.
 * Handles HTTP requests for:
 * - POST /api/remove-background → Remove background from image
 *
 * This endpoint is PROTECTED (JWT token required).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${app.frontend.url}")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * Remove background from an uploaded image.
     *
     * Request: multipart/form-data with "image" field
     * Response: PNG image bytes with transparent background
     *
     * @param file           The uploaded image file
     * @param authentication Spring Security injects the logged-in user info
     */
    @PostMapping("/remove-background")
    public ResponseEntity<?> removeBackground(
            @RequestParam("image") MultipartFile file,
            Authentication authentication) {

        try {
            // Check if file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select an image file.");
            }

            // Get the logged-in user's email from JWT token
            String userEmail = authentication.getName();
            System.out.println("Processing image for user: " + userEmail);

            // Remove background using the service
            byte[] processedImage = imageService.removeBackground(file, userEmail);

            // Return the processed PNG image
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(processedImage.length);
            // Tell browser this is a downloadable file
            headers.set("Content-Disposition", "inline; filename=\"result.png\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(processedImage);

        } catch (Exception e) {
            System.err.println("Error removing background: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Failed to remove background: " + e.getMessage());
        }
    }
}
