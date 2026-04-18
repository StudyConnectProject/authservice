package com.studyconnetct.authservice.config;

import com.studyconnetct.authservice.entity.Role;
import com.studyconnetct.authservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {
    
    private final RoleRepository roleRepository;
    
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeDatabase() {
        try {
            // Check and insert default roles
            if (!roleRepository.existsByName("STUDENT")) {
                Role studentRole = Role.builder()
                        .name("STUDENT")
                        .isActive(true)
                        .build();
                roleRepository.save(studentRole);
                log.info("✓ Role STUDENT created");
            } else {
                log.info("✓ Role STUDENT already exists");
            }
            
            if (!roleRepository.existsByName("TUTOR")) {
                Role tutorRole = Role.builder()
                        .name("TUTOR")
                        .isActive(true)
                        .build();
                roleRepository.save(tutorRole);
                log.info("✓ Role TUTOR created");
            } else {
                log.info("✓ Role TUTOR already exists");
            }
            
            log.info("✓ Database initialized successfully - All roles ready");
        } catch (Exception e) {
            log.error("✗ Error initializing database: ", e);
        }
    }
}

