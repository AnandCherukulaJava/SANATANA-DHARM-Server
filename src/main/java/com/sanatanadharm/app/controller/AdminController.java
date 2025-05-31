package com.sanatanadharm.app.controller;

import com.sanatanadharm.app.entity.User;
import com.sanatanadharm.app.entity.Role;
import com.sanatanadharm.app.repository.UserRepository;
import com.sanatanadharm.app.repository.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    @GetMapping("/dashboard")
    @Operation(summary = "Admin Dashboard", description = "Get admin dashboard with user statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAllActiveUsers().size();
        long unverifiedUsers = userRepository.findAllUnverifiedUsers().size();
        long newUsersThisMonth = userRepository.countUsersCreatedAfter(
                LocalDateTime.now().minusMonths(1));
        
        dashboard.put("totalUsers", totalUsers);
        dashboard.put("activeUsers", activeUsers);
        dashboard.put("unverifiedUsers", unverifiedUsers);
        dashboard.put("newUsersThisMonth", newUsersThisMonth);
        dashboard.put("message", "Admin Dashboard - SANATANA-DHARM");
        
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/users")
    @Operation(summary = "Get All Users", description = "Get paginated list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userRepository.findAll(pageable);
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{id}")
    @Operation(summary = "Get User by ID", description = "Get user details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{id}/enable")
    @Operation(summary = "Enable User", description = "Enable a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User enabled successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        user.setIsEnabled(true);
        userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User enabled successfully");
        response.put("userId", id);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/users/{id}/disable")
    @Operation(summary = "Disable User", description = "Disable a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User disabled successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        user.setIsEnabled(false);
        userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User disabled successfully");
        response.put("userId", id);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/roles")
    @Operation(summary = "Get All Roles", description = "Get list of all available roles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }
}