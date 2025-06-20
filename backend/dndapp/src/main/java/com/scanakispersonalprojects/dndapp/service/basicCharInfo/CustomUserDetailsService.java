package com.scanakispersonalprojects.dndapp.service.basicCharInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CustomUserPrincipal;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.User;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.UserRoleProjection;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.UserRepo;


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

    private UserRepo userDao;

    private CharacterInfoService characterService;

    /**
     * Builds the {@link UserDetails} boject Srping Security needs.
     */

    public CustomUserDetailsService(UserRepo userDaoPSQL, CharacterInfoService characterService) {
        this.characterService = characterService;
        this.userDao = userDaoPSQL;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        List<UUID> charUuids = userDao.findCharacterUuidByUserUuid(user.getUuid());
        
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
        User user = userDao.findByUsername(authentication.getName())
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
        return userDao.findCharacterUuidByUserUuid(getUsersUuid(authentication));
    }

    public boolean isAdmin(Authentication authentication) {
        UserRoleProjection projection = userDao.getAuthoritiesFromUsername(authentication.getName());
        return projection.getAuthority().equals("ROLE_ADMIN");

    }

}
