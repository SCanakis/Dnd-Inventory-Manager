package com.scanakispersonalprojects.dndapp.testutils.inventory;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.User;

public class UserBuilder {
    private String username;
    private String password;
    
    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }
    
    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    public User build() {
        return new User(username, password);
    }

    public static UserBuilder defaultUser() {
        return new UserBuilder()
                .username("testuser")
                .password("{noop}test");
    }
}
    