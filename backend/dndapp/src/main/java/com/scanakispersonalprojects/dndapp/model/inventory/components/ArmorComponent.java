package com.scanakispersonalprojects.dndapp.model.inventory.components;

import java.util.Map;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;

public class ArmorComponent {

    private short ACBonus = 0;
    private Map<AbilityScore, Boolean> addAStoAC; 

    public ArmorComponent() {}

    public ArmorComponent(short ACBonus, Map<AbilityScore, Boolean> addAStoAC) {
        this.ACBonus = ACBonus;
        this.addAStoAC = addAStoAC;
    }

    public short getACBonus() {
        return ACBonus;
    }

    public void setACBonus(short aCBonus) {
        ACBonus = aCBonus;
    }

    public Map<AbilityScore, Boolean> getAddAStoAC() {
        return addAStoAC;
    }

    public void setAddAStoAC(Map<AbilityScore, Boolean> addAStoAC) {
        this.addAStoAC = addAStoAC;
    }

    
}
