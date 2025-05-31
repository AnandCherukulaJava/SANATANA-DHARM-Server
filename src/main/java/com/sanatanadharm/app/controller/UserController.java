package com.sanatanadharm.app.controller;

import com.sanatanadharm.app.entity.User;
import com.sanatanadharm.app.repository.UserRepository;
import com.sanatanadharm.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User profile management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    @Autowired
    UserRepository userRepository;
    
    @GetMapping("/profile")
    @Operation(summary = "Get User Profile", description = "Get current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('USER') or hasRole('SUPER_USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId()).orElse(null);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("id", user.getId());
        userProfile.put("username", user.getUsername());
        userProfile.put("email", user.getEmail());
        userProfile.put("firstName", user.getFirstName());
        userProfile.put("lastName", user.getLastName());
        userProfile.put("phoneNumber", user.getPhoneNumber());
        userProfile.put("emailVerified", user.getEmailVerified());
        userProfile.put("createdAt", user.getCreatedAt());
        userProfile.put("lastLogin", user.getLastLogin());
        userProfile.put("roles", user.getRoles().stream()
                .map(role -> role.getName().getDisplayName())
                .toArray());
        
        return ResponseEntity.ok(userProfile);
    }
    
    @GetMapping("/dashboard")
    @Operation(summary = "Get User Dashboard", description = "Get user dashboard with personalized information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('USER') or hasRole('SUPER_USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserDashboard(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("message", "Welcome to your dashboard!");
        dashboard.put("username", userPrincipal.getUsername());
        dashboard.put("authorities", userPrincipal.getAuthorities());
        
        return ResponseEntity.ok(dashboard);
    }
}