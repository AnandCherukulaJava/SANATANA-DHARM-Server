package com.sanatanadharm.app.repository;

import com.sanatanadharm.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    Optional<User> findByResetToken(String resetToken);
    
    Optional<User> findByVerificationToken(String verificationToken);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.resetTokenExpiry < :currentTime")
    List<User> findUsersWithExpiredResetTokens(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT u FROM User u WHERE u.verificationTokenExpiry < :currentTime")
    List<User> findUsersWithExpiredVerificationTokens(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u WHERE u.isEnabled = true")
    List<User> findAllActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.emailVerified = false")
    List<User> findAllUnverifiedUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    Long countUsersCreatedAfter(@Param("startDate") LocalDateTime startDate);
}