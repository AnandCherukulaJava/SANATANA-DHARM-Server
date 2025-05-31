package com.sanatanadharm.app.controller;

import com.sanatanadharm.app.dto.request.ForgotPasswordRequest;
import com.sanatanadharm.app.dto.request.LoginRequest;
import com.sanatanadharm.app.dto.request.RegisterRequest;
import com.sanatanadharm.app.dto.request.ResetPasswordRequest;
import com.sanatanadharm.app.dto.response.MessageResponse;
import com.sanatanadharm.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    @Autowired
    AuthService authService;
    
    @PostMapping("/signin")
    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }
    
    @PostMapping("/signup")
    @Operation(summary = "User Registration", description = "Register a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Username or email already exists")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        return authService.registerUser(signUpRequest);
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password", description = "Send password reset email to user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return authService.forgotPassword(forgotPasswordRequest);
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Reset user password using reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return authService.resetPassword(resetPasswordRequest);
    }
    
    @GetMapping("/verify-email")
    @Operation(summary = "Verify Email", description = "Verify user email using verification token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        return authService.verifyEmail(token);
    }
    
    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Token", description = "Refresh JWT token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid refresh token")
    })
    public ResponseEntity<?> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return authService.refreshToken(refreshToken);
    }
    
    @PostMapping("/signout")
    @Operation(summary = "User Logout", description = "Logout user (client-side token removal)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged out successfully")
    })
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
    }
}