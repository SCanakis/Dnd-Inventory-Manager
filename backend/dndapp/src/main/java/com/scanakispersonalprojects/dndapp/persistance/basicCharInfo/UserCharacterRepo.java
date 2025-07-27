package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.UserCharacter;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.UserCharacterId;

public interface UserCharacterRepo extends JpaRepository<UserCharacter, UserCharacterId>{


    
}
