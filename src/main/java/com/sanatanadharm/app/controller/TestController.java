package com.sanatanadharm.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/test")
@Tag(name = "Test", description = "Test endpoints for different access levels")
public class TestController {
    
    @GetMapping("/all")
    @Operation(summary = "Public Access", description = "Endpoint accessible to everyone")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Public content returned")
    })
    public String allAccess() {
        return "Public Content.";
    }
    
    @GetMapping("/user")
    @Operation(summary = "User Access", description = "Endpoint accessible to authenticated users")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User content returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('USER') or hasRole('SUPER_USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String userAccess() {
        return "User Content.";
    }
    
    @GetMapping("/superuser")
    @Operation(summary = "Super User Access", description = "Endpoint accessible to super users and above")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Super user content returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String superUserAccess() {
        return "Super User Content.";
    }
    
    @GetMapping("/admin")
    @Operation(summary = "Admin Access", description = "Endpoint accessible to admins only")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin content returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
    
    @GetMapping("/superadmin")
    @Operation(summary = "Super Admin Access", description = "Endpoint accessible to super admins only")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Super admin content returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String superAdminAccess() {
        return "Super Admin Board.";
    }
}