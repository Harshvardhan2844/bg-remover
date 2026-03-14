package com.bgremover.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * This class represents a User in our database.
 * Each user has an id, name, email, password, and login provider.
 */
@Data                  // Lombok: auto-generates getters, setters, toString
@NoArgsConstructor     // Lombok: auto-generates empty constructor
@AllArgsConstructor    // Lombok: auto-generates constructor with all fields
@Entity                // Tells JPA this is a database table
@Table(name = "users") // Table name in PostgreSQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true) // Email must be unique
    private String email;

    @Column // Password can be null for OAuth users (Google/GitHub)
    private String password;

    /**
     * How the user logged in:
     * - "local"  = email/password
     * - "google" = Google OAuth
     * - "github" = GitHub OAuth
     */
    @Column(nullable = false)
    private String provider = "local";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // This method runs automatically before saving to database
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
