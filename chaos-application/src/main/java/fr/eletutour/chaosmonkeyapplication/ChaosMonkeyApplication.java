package fr.eletutour.chaosmonkeyapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChaosMonkeyApplication {

    public static void main(String[] args) {
        String profile = System.getProperty("spring.profiles.active", "default");

        System.out.println("🎬 Starting Netflix-like Streaming Service with Chaos Monkey...");
        System.out.println("🐵 Chaos Engineering Demo - Inspired by Netflix's Simian Army");
        System.out.println("🔧 Profil actif : " + profile);
        SpringApplication.run(ChaosMonkeyApplication.class, args);
    }

}
