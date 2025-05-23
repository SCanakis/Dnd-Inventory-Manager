package com.scanakispersonalprojects.dndapp.persistance;


import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

public interface BasicCharInfoPersistance {
    
    public CharacterBasicInfoView getCharInfo(UUID uuid);

}
