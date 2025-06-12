package com.scanakispersonalprojects.dndapp.model.inventory.components;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.RollType;
import com.scanakispersonalprojects.dndapp.model.inventory.Skill;

@Repository
public class SkillAlteredComponent {
    private Map<Skill, RollType > skillAlteredRollType; 
    private Map<Skill, Integer> skillAlteredBonus;
    
    public SkillAlteredComponent(Map<Skill, RollType> skillAlteredRollType, Map<Skill, Integer> skillAlteredBonus) {
        this.skillAlteredRollType = skillAlteredRollType;
        this.skillAlteredBonus = skillAlteredBonus;
    }

    public SkillAlteredComponent() {}

    public Map<Skill, RollType> getSkillAlteredRollType() {
        return skillAlteredRollType;
    }

    public void setSkillAlteredRollType(Map<Skill, RollType> skillAlteredRollType) {
        this.skillAlteredRollType = skillAlteredRollType;
    }

    public Map<Skill, Integer> getSkillAlteredBonus() {
        return skillAlteredBonus;
    }

    public void setSkillAlteredBonus(Map<Skill, Integer> skillAlteredBonus) {
        this.skillAlteredBonus = skillAlteredBonus;
    }
    

    
}
