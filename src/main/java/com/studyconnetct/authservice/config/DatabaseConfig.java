package com.studyconnetct.authservice.config;

import com.studyconnetct.authservice.entity.Role;
import com.studyconnetct.authservice.entity.User;
import com.studyconnetct.authservice.entity.UserRole;
import com.studyconnetct.authservice.repository.RoleRepository;
import com.studyconnetct.authservice.repository.UserRepository;
import com.studyconnetct.authservice.util.HashUtil;
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
    private final UserRepository userRepository;
    private final HashUtil hashUtil;

    // Credenciales de la cuenta administradora sembrada por defecto.
    private static final String ADMIN_EMAIL = "admin@studyconnect.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeDatabase() {
        try {
            // Roles por defecto
            ensureRole("STUDENT");
            ensureRole("TUTOR");
            ensureRole("ADMIN");

            // Cuenta administradora por defecto
            seedAdminUser();

            log.info("✓ Database initialized successfully - All roles ready");
        } catch (Exception e) {
            log.error("✗ Error initializing database: ", e);
        }
    }

    private void ensureRole(String name) {
        if (!roleRepository.existsByName(name)) {
            roleRepository.save(Role.builder().name(name).isActive(true).build());
            log.info("✓ Role {} created", name);
        } else {
            log.info("✓ Role {} already exists", name);
        }
    }

    private void seedAdminUser() {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.info("✓ Admin user already exists");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        User admin = User.builder()
                .email(ADMIN_EMAIL)
                .passwordHash(hashUtil.hashPassword(ADMIN_PASSWORD))
                .isActive(true)
                .build();
        admin = userRepository.save(admin);

        UserRole userRole = UserRole.builder()
                .user(admin)
                .role(adminRole)
                .build();
        admin.getUserRoles().add(userRole);
        userRepository.save(admin);

        log.info("✓ Admin user created -> {} / {}", ADMIN_EMAIL, ADMIN_PASSWORD);
    }
}
