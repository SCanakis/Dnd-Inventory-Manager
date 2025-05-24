package com.scanakispersonalprojects.dndapp.persistance;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.HealthUpdate;

@Service
public interface BasicCharInfoPersistance {
    
    public CharacterBasicInfoView getCharInfo(UUID uuid);

    public CharacterBasicInfoView updateHealth(UUID uuid, HealthUpdate healthUpdate);

}
