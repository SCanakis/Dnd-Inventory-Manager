package com.scanakispersonalprojects.dndapp.model.inventory.components;

import java.util.Map;
import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.RollType;

public class SkillAlteredComponent {
    private Map<UUID, RollType > skillAlteredRollType; 
    private Map<UUID, Integer> skillAlteredBonus;
    
    public SkillAlteredComponent(Map<UUID, RollType> skillAlteredRollType, Map<UUID, Integer> skillAlteredBonus) {
        this.skillAlteredRollType = skillAlteredRollType;
        this.skillAlteredBonus = skillAlteredBonus;
    }

    public SkillAlteredComponent() {}

    public Map<UUID, RollType> getSkillAlteredRollType() {
        return skillAlteredRollType;
    }

    public void setSkillAlteredRollType(Map<UUID, RollType> skillAlteredRollType) {
        this.skillAlteredRollType = skillAlteredRollType;
    }

    public Map<UUID, Integer> getSkillAlteredBonus() {
        return skillAlteredBonus;
    }

    public void setSkillAlteredBonus(Map<UUID, Integer> skillAlteredBonus) {
        this.skillAlteredBonus = skillAlteredBonus;
    }
    

    
}
