package com.scanakispersonalprojects.dndapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.CustomUserPrincipal;
import com.scanakispersonalprojects.dndapp.model.User;
import com.scanakispersonalprojects.dndapp.persistance.UserDao;



@Service
public class CustomUserDetailsService implements UserDetailsService{

    @Autowired 
    private UserDao userDao;

    @Autowired
    private CharacterService characterService;


  
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        List<UUID> charUuids = userDao.findCharacterUuidByUserUuid(user.getUuid());
        
        List<CharacterBasicInfoView> characters = new ArrayList<>();
        for(UUID uuid : charUuids) {
            characters.add(characterService.getCharInfo(uuid));
        }

        return new CustomUserPrincipal(user, characters);
    }

    public UUID getUsersUuid(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));;
        return user.getUuid();
    }

}
