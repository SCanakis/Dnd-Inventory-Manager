package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CharacterInfoUpdateDTO {
    
    private String name;
    private Boolean inspiration;
    private UUID backgroundUuid;
    private UUID raceUuid;
    private Map<AbilityScore, Integer> abilityScores; 
    private HPHandler hpHandler; 
    private DeathSavingThrowsHelper deathSavingThrowsHelper;
    private List<CharacterClassDetail> characterClassDetail; 
    
    // Default constructor
    public CharacterInfoUpdateDTO() {}
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Boolean getInspiration() {
        return inspiration;
    }
    
    public void setInspiration(Boolean inspiration) {
        this.inspiration = inspiration;
    }
    
    public UUID getBackgroundUuid() {
        return backgroundUuid;
    }
    
    public void setBackgroundUuid(UUID backgroundUuid) {
        this.backgroundUuid = backgroundUuid;
    }
    
    public UUID getRaceUuid() {
        return raceUuid;
    }
    
    public void setRaceUuid(UUID raceUuid) {
        this.raceUuid = raceUuid;
    }
    
    public Map<AbilityScore, Integer> getAbilityScores() {
        return abilityScores;
    }
    
    public void setAbilityScores(Map<AbilityScore, Integer>  abilityScores) {
        this.abilityScores = abilityScores;
    }
    
    public HPHandler getHpHandler() {
        return hpHandler;
    }
    
    public void setHpHandler(HPHandler hpHandler) {
        this.hpHandler = hpHandler;
    }
    
    public DeathSavingThrowsHelper getDeathSavingThrowsHelper() {
        return deathSavingThrowsHelper;
    }
    
    public void setDeathSavingThrowsHelper(DeathSavingThrowsHelper deathSavingThrowsHelper) {
        this.deathSavingThrowsHelper = deathSavingThrowsHelper;
    }
    
    public List<CharacterClassDetail> getCharacterClassDetail() {
        return characterClassDetail;
    }
    
    public void setCharacterClassDetail(List<CharacterClassDetail> characterClassDetail) {
        this.characterClassDetail = characterClassDetail;
    }
    

}