package fr.eletutour.chaosmonkeyapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChaosMonkeyApplication {

    public static void main(String[] args) {
        System.out.println("🎬 Starting Netflix-like Streaming Service with Chaos Monkey...");
        System.out.println("🐵 Chaos Engineering Demo - Inspired by Netflix's Simian Army");
        SpringApplication.run(ChaosMonkeyApplication.class, args);
    }

}
