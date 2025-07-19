package com.scanakispersonalprojects.dndapp.service.basicCharInfo;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class CharacterLinkService {
    
    private UserRepo userRepo;

    public CharacterLinkService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public boolean linkCharacter(UUID userUuid, UUID charUuid) {
        try {
            userRepo.addCharacterUser(userUuid, charUuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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
