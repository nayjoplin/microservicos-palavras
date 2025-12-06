package com.palavras.etiquetas.authservice.config;

import com.palavras.etiquetas.authservice.entity.User;
import com.palavras.etiquetas.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin@example.com", "admin123", "admin"));
            userRepository.save(new User("user@example.com", "user123", "usuario"));
            logger.info("Initial users created successfully");
        }
    }
}
