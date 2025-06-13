package com.scanakispersonalprojects.dndapp.model.inventory.components;

import java.util.Map;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;

public class SkillRequirmentComponent {
    
    private Map<AbilityScore, Integer> abilityRequirment;
    
    public SkillRequirmentComponent(Map<AbilityScore, Integer> abilityRequirment){
        this.abilityRequirment = abilityRequirment;
    } 

    public SkillRequirmentComponent() {}

    public Map<AbilityScore, Integer> getAbilityRequirment() {
        return abilityRequirment;
    }

    public void setAbilityRequirment(Map<AbilityScore, Integer> abilityRequirment) {
        this.abilityRequirment = abilityRequirment;
    }
}
