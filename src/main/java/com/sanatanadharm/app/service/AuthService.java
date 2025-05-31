package com.sanatanadharm.app.service;

import com.sanatanadharm.app.dto.request.ForgotPasswordRequest;
import com.sanatanadharm.app.dto.request.LoginRequest;
import com.sanatanadharm.app.dto.request.RegisterRequest;
import com.sanatanadharm.app.dto.request.ResetPasswordRequest;
import com.sanatanadharm.app.dto.response.JwtResponse;
import com.sanatanadharm.app.dto.response.MessageResponse;
import com.sanatanadharm.app.entity.Role;
import com.sanatanadharm.app.entity.RoleName;
import com.sanatanadharm.app.entity.User;
import com.sanatanadharm.app.repository.RoleRepository;
import com.sanatanadharm.app.repository.UserRepository;
import com.sanatanadharm.app.security.UserPrincipal;
import com.sanatanadharm.app.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    EmailService emailService;
    
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);
            
            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            
            // Update last login
            User user = userRepository.findById(userDetails.getId()).orElse(null);
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
            }
            
            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    user != null ? user.getFirstName() : null,
                    user != null ? user.getLastName() : null,
                    roles));
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid username/email or password!", false));
        }
    }
    
    public ResponseEntity<?> registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!", false));
        }
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!", false));
        }
        
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        
        Set<Role> roles = new HashSet<>();
        
        // Assign default USER role
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        
        user.setRoles(roles);
        
        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        user.setEmailVerified(false);
        
        userRepository.save(user);
        
        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        } catch (Exception e) {
            logger.error("Failed to send verification email: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(new MessageResponse("User registered successfully! Please check your email for verification."));
    }
    
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElse(null);
        
        if (user == null) {
            // Don't reveal if email exists or not for security
            return ResponseEntity.ok(new MessageResponse("If the email exists, a password reset link has been sent."));
        }
        
        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 1 hour expiry
        
        userRepository.save(user);
        
        // Send reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        } catch (Exception e) {
            logger.error("Failed to send password reset email: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to send password reset email. Please try again.", false));
        }
        
        return ResponseEntity.ok(new MessageResponse("If the email exists, a password reset link has been sent."));
    }
    
    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByResetToken(resetPasswordRequest.getToken())
                .orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid reset token!", false));
        }
        
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Reset token has expired!", false));
        }
        
        // Update password
        user.setPassword(encoder.encode(resetPasswordRequest.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully!"));
    }
    
    public ResponseEntity<?> verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid verification token!", false));
        }
        
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Verification token has expired!", false));
        }
        
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("Email verified successfully!"));
    }
    
    public ResponseEntity<?> refreshToken(String refreshToken) {
        try {
            if (jwtUtils.validateJwtToken(refreshToken)) {
                String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
                String newAccessToken = jwtUtils.generateTokenFromUsername(username);
                String newRefreshToken = jwtUtils.generateRefreshTokenFromUsername(username);
                
                User user = userRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    List<String> roles = user.getRoles().stream()
                            .map(role -> "ROLE_" + role.getName().name())
                            .collect(Collectors.toList());
                    
                    return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken,
                            user.getId(), user.getUsername(), user.getEmail(),
                            user.getFirstName(), user.getLastName(), roles));
                }
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid refresh token!", false));
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Token refresh failed!", false));
        }
    }
}