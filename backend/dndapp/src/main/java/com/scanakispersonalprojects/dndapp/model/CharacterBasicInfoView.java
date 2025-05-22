package com.scanakispersonalprojects.dndapp.model;

import java.util.Map;
import java.util.UUID;

public class CharacterBasicInfoView {
    
    private String charInfoUUID;
    private String name;
    private boolean inspiration;
    private String background; 
    private UUID backgroundUUID;
    private String race;
    private UUID raceUUID;
   

    private Map<AbilityScore, Integer> abilityScores;
    
    public CharacterBasicInfoView(String charInfoUUID, String name, boolean inspiration, String background,
            UUID backgroundUUID, String race, UUID raceUUID,
            Map<AbilityScore, Integer> abilityScores) {
        this.charInfoUUID = charInfoUUID;
        this.name = name;
        this.inspiration = inspiration;
        this.background = background;
        this.backgroundUUID = backgroundUUID;
        this.race = race;
        this.raceUUID = raceUUID;
        this.abilityScores = abilityScores;
    }

    public String getCharInfoUUID() {
        return charInfoUUID;
    }

    public String getBackground() {
        return background;
    }

    public UUID getBackgroundUUID() {
        return backgroundUUID;
    }

    public String getRace() {
        return race;
    }

    public UUID getRaceUUID() {
        return raceUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInspiration() {
        return inspiration;
    }

    public void setInspiration(boolean inspiration) {
        this.inspiration = inspiration;
    }

    public Map<AbilityScore, Integer> getAbilityScores() {
        return abilityScores;
    }

    public void setAbilityScores(Map<AbilityScore, Integer> abilityScores) {
        this.abilityScores = abilityScores;
    }
    
    
}
