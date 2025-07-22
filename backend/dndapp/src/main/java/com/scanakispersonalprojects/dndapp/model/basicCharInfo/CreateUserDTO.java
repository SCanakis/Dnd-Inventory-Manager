package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

/**
 * Data Transfer Object (DTO) for creating a user. This DTO encapsualtes
 * the esssential data needed to create a new user. 
 * 
 * Used primarly for API request when creating new user. 
 */
public class CreateUserDTO {
    private String username;
    private String password;
    
    public CreateUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
