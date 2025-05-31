package com.sanatanadharm.app.config;

import com.sanatanadharm.app.entity.Role;
import com.sanatanadharm.app.entity.RoleName;
import com.sanatanadharm.app.entity.User;
import com.sanatanadharm.app.repository.RoleRepository;
import com.sanatanadharm.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeSuperAdmin();
    }
    
    private void initializeRoles() {
        logger.info("Initializing roles...");
        
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                logger.info("Created role: {}", roleName.getDisplayName());
            }
        }
        
        logger.info("Roles initialization completed.");
    }
    
    private void initializeSuperAdmin() {
        logger.info("Checking for Super Admin user...");
        
        if (!userRepository.existsByUsername("superadmin")) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("superadmin@sanatanadharm.com");
            superAdmin.setPassword(passwordEncoder.encode("SuperAdmin@123"));
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setEmailVerified(true);
            superAdmin.setIsEnabled(true);
            superAdmin.setIsAccountNonExpired(true);
            superAdmin.setIsAccountNonLocked(true);
            superAdmin.setIsCredentialsNonExpired(true);
            
            Set<Role> roles = new HashSet<>();
            Role superAdminRole = roleRepository.findByName(RoleName.SUPER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Super Admin role not found"));
            roles.add(superAdminRole);
            superAdmin.setRoles(roles);
            
            userRepository.save(superAdmin);
            logger.info("Super Admin user created successfully");
            logger.info("Username: superadmin");
            logger.info("Password: SuperAdmin@123");
            logger.info("Email: superadmin@sanatanadharm.com");
        } else {
            logger.info("Super Admin user already exists");
        }
    }
}