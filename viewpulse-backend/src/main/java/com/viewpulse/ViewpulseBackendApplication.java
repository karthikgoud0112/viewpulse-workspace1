package com.viewpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ViewpulseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViewpulseBackendApplication.class, args);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎬 ViewPulse Backend Started Successfully!");
        System.out.println("📡 Server: http://localhost:8080");
        System.out.println("🗄️  Database: MySQL (viewpulse_db)");
        System.out.println("🤖 AI Service: http://localhost:5000");
        System.out.println("=".repeat(60) + "\n");
    }
}
