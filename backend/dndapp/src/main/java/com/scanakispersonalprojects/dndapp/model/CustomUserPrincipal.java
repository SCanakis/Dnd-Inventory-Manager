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
     * @return the username stored in the warpped {@link User}
     */

    @Override
    public String getUsername() {
        return user.getUsername(); 
    }
    
    /**
     * Returns the password used to authentcate the user
     * 
     * @return the password stored in the warpped {@link User}
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Returns the authorities graanted to the user.
     * by default, every user recieve a single authoroity {@code ROLE_USER}.
     * 
     * @return an immutable singleton containing {@code ROLE_USER}
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    /**
     * Provides direct access to the wrapped {@link User} entity.
     *
     * @return the underlying domain entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns a list of lightweight views of the user’s D&D characters.
     *
     * @return unmodifiable list of {@link CharacterBasicInfoView}
     */
    public List<CharacterBasicInfoView> getCharacters() {
        return characters;
    }
    
    /**
     * Convenience shortcut for retrieving the user’s UUID.
     *
     * @return the unique identifier of the wrapped {@link User}
     */
    public UUID getUserUuid() {
        return user.getUuid();
    }
    

}
