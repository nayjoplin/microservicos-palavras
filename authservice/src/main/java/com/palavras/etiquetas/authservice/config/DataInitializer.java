package com.palavras.etiquetas.authservice.config;

import com.palavras.etiquetas.authservice.entity.User;
import com.palavras.etiquetas.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin@example.com", "admin123", "admin"));
            userRepository.save(new User("user@example.com", "user123", "usuario"));
            System.out.println("Initial users created successfully");
        }
    }
}
