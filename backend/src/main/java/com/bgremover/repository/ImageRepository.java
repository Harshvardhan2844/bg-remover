package com.bgremover.repository;

import com.bgremover.model.Image;
import com.bgremover.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface handles database operations for Images.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // Get all images uploaded by a specific user
    List<Image> findByUserOrderByCreatedAtDesc(User user);
}
