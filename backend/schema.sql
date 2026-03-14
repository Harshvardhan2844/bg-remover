-- =============================================
-- BG Remover App - PostgreSQL Database Schema
-- =============================================
-- Run this file to create the database manually.
-- (Spring Boot also auto-creates tables via JPA,
--  but this file is for reference / manual setup.)

-- Create the database (run this as postgres superuser)
-- CREATE DATABASE bgremover;

-- Connect to the database before running below commands:
-- \c bgremover

-- =============================================
-- USERS TABLE
-- Stores all registered users
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,          -- Auto-increment ID
    name        VARCHAR(100) NOT NULL,          -- User's full name
    email       VARCHAR(150) NOT NULL UNIQUE,   -- Email (must be unique)
    password    VARCHAR(255),                   -- Hashed password (null for OAuth users)
    provider    VARCHAR(20) DEFAULT 'local',    -- 'local', 'google', or 'github'
    created_at  TIMESTAMP DEFAULT NOW()         -- When account was created
);

-- =============================================
-- IMAGES TABLE
-- Stores info about each processed image
-- =============================================
CREATE TABLE IF NOT EXISTS images (
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    original_image_path   VARCHAR(500),          -- Path to original uploaded image
    processed_image_path  VARCHAR(500),          -- Path to background-removed image
    created_at            TIMESTAMP DEFAULT NOW()
);

-- Index for faster lookup of images by user
CREATE INDEX IF NOT EXISTS idx_images_user_id ON images(user_id);

-- =============================================
-- Sample data for testing (optional)
-- =============================================
-- INSERT INTO users (name, email, password, provider)
-- VALUES ('Test User', 'test@example.com', 'hashed_password_here', 'local');
