package com.sanatanadharm.app.repository;

import com.sanatanadharm.app.entity.Role;
import com.sanatanadharm.app.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(RoleName name);
    
    Boolean existsByName(RoleName name);
}