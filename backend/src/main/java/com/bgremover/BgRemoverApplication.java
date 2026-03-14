package com.bgremover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Background Remover application.
 * This class starts the Spring Boot application.
 */
@SpringBootApplication
public class BgRemoverApplication {

    public static void main(String[] args) {
        SpringApplication.run(BgRemoverApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  BG Remover App is running!");
        System.out.println("  Backend: http://localhost:8080");
        System.out.println("===========================================");
    }
}
