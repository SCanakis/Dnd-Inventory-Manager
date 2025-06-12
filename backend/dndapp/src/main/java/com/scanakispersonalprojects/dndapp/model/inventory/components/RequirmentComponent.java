package com.scanakispersonalprojects.dndapp.model.inventory.components;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;

public class RequirmentComponent {
    
    private Map<AbilityScore, Integer> abilityRequirment;
    private List<UUID> eligibleClasses;
    
    public RequirmentComponent(Map<AbilityScore, Integer> abilityRequirment, List<UUID> eligibleClasses){
        this.abilityRequirment = abilityRequirment;
        this.eligibleClasses = eligibleClasses;
    } 

    public RequirmentComponent() {}

    public Map<AbilityScore, Integer> getAbilityRequirment() {
        return abilityRequirment;
    }

    public void setAbilityRequirment(Map<AbilityScore, Integer> abilityRequirment) {
        this.abilityRequirment = abilityRequirment;
    }

    public List<UUID> getEligibleClasses() {
        return eligibleClasses;
    }

    public void setEligibleClasses(List<UUID> eligibleClasses) {
        this.eligibleClasses = eligibleClasses;
    }

}
