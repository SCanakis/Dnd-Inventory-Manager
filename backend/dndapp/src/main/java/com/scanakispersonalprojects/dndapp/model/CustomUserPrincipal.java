package com.scanakispersonalprojects.dndapp.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Brides teh domain-level {@link User} entity with Spring Security by
 * implemeting the {@link UserDetails} contract
 * 
 * This adapter is used by Srping Security to authenticate users and
 * inject user information into the security context during request processing. 
 * 
 * Core credential data (username, password, authroties)
 * 
 * Exposes:
 * {@link #getUser()} - direct access to the underlying {@code User}
 * 
 * {@link #getCharacters()} - a pre-fetched, read-only list of the
 * user's D&D characters.
 * 
 * 
 */
public class CustomUserPrincipal implements UserDetails{
    
    /** Bakcing domain entity with credential and profile data */
    private User user;

    /** Lightweight essential details of the user's characters */
    private List<CharacterBasicInfoView> characters;

    /**
     * Creats a new principal that adapts the given {@code User}
     * 
     * @param user          the domain entity to wrap
     * @param characters    list of character that bleong to the user.
     */

    public CustomUserPrincipal(User user, List<CharacterBasicInfoView> characters) {
        this.user = user;
        this.characters = characters;
    }

    /**
     * Returns the username used to authentcate the user
     * 
     * @return the username sotred in the warpped {@link User}
     */

    @Override
    public String getUsername() {
        return user.getUsername(); 
    }
    
    /**
     * Returns the username used to authentcate the user
     * 
     * @return the username sotred in the warpped {@link User}
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    public User getUser() {
        return user;
    }
    public List<CharacterBasicInfoView> getCharacters() {
        return characters;
    }
    
    public UUID getUserUuid() {
        return user.getUuid();
    }
    

}
