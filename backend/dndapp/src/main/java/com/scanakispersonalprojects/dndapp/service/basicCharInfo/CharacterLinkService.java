package com.scanakispersonalprojects.dndapp.service.basicCharInfo;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.UserRepo;

import jakarta.transaction.Transactional;

/**
 * This service manages user-character relationships. 
 * 
 * Used in character creation and deletion. 
 * 
 */
@Service
public class CharacterLinkService {
    
    /** Repository for user-character operations */
    private UserRepo userRepo;

    /**
     * Constrcuts a new CharacterLinkService with the reuqies repo dependencies
     * 
     * @param userRepo
     */
    public CharacterLinkService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Links a user with their character. 
     * 
     * Used during character creation 
     * 
     * @param userUuid - the unique user identifier
     * @param charUuid - the unique character identifier
     * @return
     */
    @Transactional
    public boolean linkCharacter(UUID userUuid, UUID charUuid) {
        try {
            userRepo.addCharacterUser(userUuid, charUuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Unlinks a user with their character. 
     * 
     * Used during character deletion 
     * 
     * @param userUuid - the unique user identifier
     * @param charUuid - the unique character identifier
     * @return
     */
    @Transactional
    public boolean unlinkCharacter(UUID userUuid, UUID charUuid) {
        try {
            userRepo.deleteCharacter(userUuid, charUuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    

}
