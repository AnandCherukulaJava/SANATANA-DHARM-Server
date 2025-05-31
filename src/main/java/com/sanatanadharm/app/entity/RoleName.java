package com.sanatanadharm.app.entity;

public enum RoleName {
    USER("User"),
    SUPER_USER("Super User"),
    ADMIN("Admin"),
    SUPER_ADMIN("Super Admin");
    
    private final String displayName;
    
    RoleName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}