package com.scanakispersonalprojects.dndapp.service.basicCharInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CreateUserDTO;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CustomUserPrincipal;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.User;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.UserRoleProjection;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.UserRepo;

import jakarta.transaction.Transactional;

/**
 * Tells Spring Security how to look up a user in the database.
 * 
 * Given a username it:
 *  Finds the User row.
 *  Pulls the user's character UUIDs
 *  Turns those UUIDs into sipmle character views
 *  Retursn everyting as a {@link CustomUserPrincipal}
 * 
 * Use {@link #getUsersUuid(String) if you only need the user's UUID}
 * 
 */

@Service
public class CustomUserDetailsService implements UserDetailsService{

    /** Repository for user operations */
    private UserRepo userRepo;

    /** Service for character business logic operations */
    private CharacterInfoService characterService;

    /** Springboot Security password Ecnoder */
    private PasswordEncoder passwordEncoder;

    /**
     * Constructs a new CustomUserDetailsService with the requires dependencies
     * 
     * @param userRepo 
     * @param characterService
     * @param passwordEncoder
     */

    public CustomUserDetailsService(UserRepo userRepo, CharacterInfoService characterService, PasswordEncoder passwordEncoder) {
        this.characterService = characterService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        List<UUID> charUuids = userRepo.findCharacterUuidByUserUuid(user.getUuid());
        
        List<CharacterBasicInfoView> characters = new ArrayList<>();
        for(UUID uuid : charUuids) {
            characters.add(characterService.getCharacterBasicInfoView(uuid));
        }

        return new CustomUserPrincipal(user, characters);
    }
    /**
     * Fetch's user's UUID
     * 
     * @param authentication        authentication object given by Spring Security      
     * @return      
     * @throws UsernameNotFoundException        If user is not found
     */
    public UUID getUsersUuid(Authentication authentication) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));;
        return user.getUuid();
    }


    /**
     * Fetches the UUIDs of the users Character 
     * 
     * 
     * @param authentication        authentication object given by Spring Security
     * @return a list of UUID of the characters that belong to that user. 
     */
    public List<UUID> getUsersCharacters(Authentication authentication) {
        return userRepo.findCharacterUuidByUserUuid(getUsersUuid(authentication));
    }

    public boolean isAdmin(Authentication authentication) {
        UserRoleProjection projection = userRepo.getAuthoritiesFromUsername(authentication.getName());
        return projection.getAuthority().equals("ROLE_ADMIN");

    }

    /**
     * Create user and makes sure credentials are valid. 
     * 
     * @param createUserDTO - infromation needed to create user {@link CreateUserDTO}
     * @return
     */
    @Transactional
    public boolean createUser(CreateUserDTO createUserDTO) {
        if(createUserDTO == null ||  
            createUserDTO.getUsername() == null || createUserDTO.getUsername().trim().isEmpty() ||
            createUserDTO.getPassword() == null ||  createUserDTO.getPassword().trim().isEmpty()
            ) {
                return false;
            }
        try {
            String hashedPassword = passwordEncoder.encode(createUserDTO.getPassword());
            User user = new User(createUserDTO.getUsername(), hashedPassword);
            userRepo.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete user and associated characters.
     * 
     * @param authentication - SpringBoot Authentication of account being deleted
     * @return
     */
    @Transactional
    public boolean deleteUser(Authentication authentication) {
        if(authentication == null || authentication.getName() == null) {
            return false;
        }

        try {
            Optional<User> userOptional = userRepo.findByUsername(authentication.getName());
            if(userOptional.isPresent()) {
            
                
                List<UUID> userCharacters = getUsersCharacters(authentication);
                User user = userOptional.get();
                UUID userUuid = user.getUuid();
                
                for(UUID character : userCharacters) {
                    if(!characterService.deleteCharacter(character, userUuid)) {
                        throw new IllegalStateException("Failed to dlete character: " + character);
                    }
                }

                userRepo.delete(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }



}
