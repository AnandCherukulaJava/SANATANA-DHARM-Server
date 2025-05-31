package com.sanatanadharm.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('SUPER_USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String userAccess() {
        return "User Content.";
    }
    
    @GetMapping("/superuser")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String superUserAccess() {
        return "Super User Content.";
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
    
    @GetMapping("/superadmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String superAdminAccess() {
        return "Super Admin Board.";
    }
}