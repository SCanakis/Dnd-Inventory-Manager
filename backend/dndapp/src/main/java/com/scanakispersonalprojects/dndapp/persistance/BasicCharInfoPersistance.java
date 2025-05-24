package com.scanakispersonalprojects.dndapp.persistance;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

@Service
public interface BasicCharInfoPersistance {
    
    public CharacterBasicInfoView getCharInfo(UUID uuid);

    public CharacterBasicInfoView updateCharInfo(UUID uuid, CharViewPatch updatedView);


}
