package com.bgremover.service;

import com.bgremover.model.Image;
import com.bgremover.model.User;
import com.bgremover.repository.ImageRepository;
import com.bgremover.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * This service handles image background removal.
 *
 * Process:
 * 1. Save uploaded image to local "uploads" folder
 * 2. Send image to remove.bg API
 * 3. Receive processed image (PNG with transparent background)
 * 4. Save processed image to local folder
 * 5. Store image paths in database
 * 6. Return processed image data to frontend
 */
@Service
public class ImageService {

    @Value("${removebg.api.key}")
    private String removeBgApiKey;

    @Value("${removebg.api.url}")
    private String removeBgApiUrl;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Main method: removes background from an uploaded image.
     *
     * @param file      The uploaded image file
     * @param userEmail Email of the logged-in user
     * @return The processed image as a byte array (PNG format)
     */
    public byte[] removeBackground(MultipartFile file, String userEmail) throws IOException {
        // Make sure the uploads folder exists
        createUploadsFolder();

        // Generate unique filenames to avoid conflicts
        String uniqueId = UUID.randomUUID().toString();
        String originalFileName = uniqueId + "_original_" + file.getOriginalFilename();
        String processedFileName = uniqueId + "_processed.png";

        // Save the original uploaded image to disk
        Path originalPath = Paths.get(uploadDir, originalFileName);
        Files.write(originalPath, file.getBytes());
        System.out.println("Original image saved: " + originalPath);

        // Send image to remove.bg API and get processed image
        byte[] processedImageBytes = callRemoveBgApi(file.getBytes(), file.getOriginalFilename());

        // Save the processed image to disk
        Path processedPath = Paths.get(uploadDir, processedFileName);
        Files.write(processedPath, processedImageBytes);
        System.out.println("Processed image saved: " + processedPath);

        // Save record in database
        saveImageRecord(userEmail, originalFileName, processedFileName);

        // Return the processed image bytes to be sent to frontend
        return processedImageBytes;
    }

    /**
     * Calls the remove.bg API to remove the background from an image.
     *
     * @param imageBytes The image data as bytes
     * @param fileName   Original file name
     * @return Processed image bytes (PNG with transparent background)
     */
    private byte[] callRemoveBgApi(byte[] imageBytes, String fileName) {
        RestTemplate restTemplate = new RestTemplate();

        // Set headers - we need to send our API key
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", removeBgApiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Create multipart request body with the image
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image_file", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return fileName; // Must return filename
            }
        });
        body.add("size", "auto"); // Let remove.bg choose the best size

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Call the remove.bg API
        ResponseEntity<byte[]> response = restTemplate.exchange(
                removeBgApiUrl,
                HttpMethod.POST,
                requestEntity,
                byte[].class // We expect image bytes back
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to remove background. Status: " + response.getStatusCode());
        }
    }

    /**
     * Save image record in the database.
     */
    private void saveImageRecord(String userEmail, String originalFileName, String processedFileName) {
        userRepository.findByEmail(userEmail).ifPresent(user -> {
            Image image = new Image();
            image.setUser(user);
            image.setOriginalImagePath(originalFileName);
            image.setProcessedImagePath(processedFileName);
            imageRepository.save(image);
        });
    }

    /**
     * Create the uploads folder if it doesn't exist.
     */
    private void createUploadsFolder() {
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Created uploads folder: " + folder.getAbsolutePath());
        }
    }
}
