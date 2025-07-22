package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for creating basic character information. This
 * DTO encapsulates the data needed to create a new character.
 * 
 * Used primarly for API request when creating new characters.
 * 
 */
public class BasicCharInfoCreationDTO {
    
    private String name;
    private UUID backgroundUuid;
    private UUID raceUuid;

    private Map<AbilityScore, Integer> abilityScores;    
    private List<CharacterClassDetail> characterClassDetails;


    public BasicCharInfoCreationDTO() {}


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
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


    public void setAbilityScores(Map<AbilityScore, Integer> abilityScores) {
        this.abilityScores = abilityScores;
    }


    public List<CharacterClassDetail> getCharacterClassDetails() {
        return characterClassDetails;
    }


    public void setCharacterClassDetails(List<CharacterClassDetail> characterClassDetails) {
        this.characterClassDetails = characterClassDetails;
    }

    

}
