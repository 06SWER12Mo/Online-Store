package com.example.demo.config;

import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class RoleInitializer {

    @Bean
    public CommandLineRunner initializeRolesAndAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            System.out.println("🔄 Initializing default users...");
            
            // ✅ Create admin user
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@store.com");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setPhoneNumber("+1234567890");
                admin.setEnabled(true);
                admin.setLocked(false);
                admin.setEmailVerified(true);
                admin.setRole(Role.ADMIN);  // ✅ Simple enum
                userRepository.save(admin);
                System.out.println("✅ Admin created: admin@store.com / Admin@123");
            }
            
            // ✅ Create manager user
            if (!userRepository.existsByUsername("manager")) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setEmail("manager@store.com");
                manager.setPassword(passwordEncoder.encode("Manager@123"));
                manager.setFirstName("Manager");
                manager.setLastName("User");
                manager.setPhoneNumber("+1234567891");
                manager.setEnabled(true);
                manager.setLocked(false);
                manager.setEmailVerified(true);
                manager.setRole(Role.MANAGER);  // ✅ Simple enum
                userRepository.save(manager);
                System.out.println("✅ Manager created: manager@store.com / Manager@123");
            }
            
            // ✅ Create test user
            if (!userRepository.existsByUsername("testuser")) {
                User testUser = new User();
                testUser.setUsername("testuser");
                testUser.setEmail("test@example.com");
                testUser.setPassword(passwordEncoder.encode("Test@123"));
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setPhoneNumber("+1234567892");
                testUser.setEnabled(true);
                testUser.setLocked(false);
                testUser.setEmailVerified(true);
                testUser.setRole(Role.USER);  // ✅ Simple enum
                userRepository.save(testUser);
                System.out.println("✅ Test user created: test@example.com / Test@123");
            }
            
            System.out.println("✅ Initialization complete!");
        };
    }
}