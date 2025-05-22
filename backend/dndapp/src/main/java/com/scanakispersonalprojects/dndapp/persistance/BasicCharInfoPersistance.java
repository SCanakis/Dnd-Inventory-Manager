package com.scanakispersonalprojects.dndapp.persistance;


import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

public interface BasicCharInfoPersistance {
    
    public CharacterBasicInfoView getCharInfo(String uuid);

}
