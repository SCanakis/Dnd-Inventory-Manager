package com.scanakispersonalprojects.dndapp.model.inventory.components;

import java.util.List;

import com.scanakispersonalprojects.dndapp.model.inventory.EquipableType;

public class EquippableComponent {
    
    private boolean equippable = false;
    private boolean attunable = false;
    private List<EquipableType> equipableType;
    
    public EquippableComponent(boolean equippable, boolean attunable, List<EquipableType> equipableType) {
        this.equippable = equippable;
        this.attunable = attunable;
        this.equipableType = equipableType;
    }

    public EquippableComponent() {}

    public boolean isEquippable() {
        return equippable;
    }

    public void setEquippable(boolean equippable) {
        this.equippable = equippable;
    }

    public boolean isAttunable() {
        return attunable;
    }

    public void setAttunable(boolean attunable) {
        this.attunable = attunable;
    }

    public List<EquipableType> getEquipableType() {
        return equipableType;
    }

    public void setEquipableType(List<EquipableType> equipableType) {
        this.equipableType = equipableType;
    }

    
    
}
