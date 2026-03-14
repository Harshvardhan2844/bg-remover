package com.bgremover.repository;

import com.bgremover.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface handles database operations for Users.
 * Spring Data JPA automatically creates the implementation for us.
 * We just need to define the method signatures.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email address
    Optional<User> findByEmail(String email);

    // Check if a user with this email already exists
    boolean existsByEmail(String email);
}
