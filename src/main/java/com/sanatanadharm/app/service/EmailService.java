package com.sanatanadharm.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender emailSender;
    
    @Value("${app.frontend-url}")
    private String frontendUrl;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("SANATANA-DHARM - Email Verification");
            
            String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
            String emailBody = "Dear User,\n\n" +
                    "Thank you for registering with SANATANA-DHARM!\n\n" +
                    "Please click the following link to verify your email address:\n" +
                    verificationUrl + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you did not create an account, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "SANATANA-DHARM Team";
            
            message.setText(emailBody);
            emailSender.send(message);
            
            logger.info("Verification email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("SANATANA-DHARM - Password Reset");
            
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            String emailBody = "Dear User,\n\n" +
                    "You have requested to reset your password for your SANATANA-DHARM account.\n\n" +
                    "Please click the following link to reset your password:\n" +
                    resetUrl + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "SANATANA-DHARM Team";
            
            message.setText(emailBody);
            emailSender.send(message);
            
            logger.info("Password reset email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to SANATANA-DHARM!");
            
            String emailBody = "Dear " + username + ",\n\n" +
                    "Welcome to SANATANA-DHARM!\n\n" +
                    "Your email has been successfully verified and your account is now active.\n\n" +
                    "You can now log in and start exploring our platform.\n\n" +
                    "Best regards,\n" +
                    "SANATANA-DHARM Team";
            
            message.setText(emailBody);
            emailSender.send(message);
            
            logger.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }
}