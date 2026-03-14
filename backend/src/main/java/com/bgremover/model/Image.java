package com.bgremover.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * This class represents an Image record in the database.
 * It stores the paths to the original and processed images.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Which user uploaded this image.
     * ManyToOne = many images can belong to one user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "original_image_path")
    private String originalImagePath; // Path to the uploaded image

    @Column(name = "processed_image_path")
    private String processedImagePath; // Path to the background-removed image

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
