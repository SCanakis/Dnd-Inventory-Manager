package com.scanakispersonalprojects.dndapp.model.inventory.components;

import com.scanakispersonalprojects.dndapp.model.inventory.EquipableType;

public class EquippableComponent {
    
    private boolean equippable = false;
    private boolean attunable = false;
    private EquipableType equipableType = EquipableType.NONE;
    
    public EquippableComponent(boolean equippable, boolean attunable, EquipableType equipableType) {
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

    public EquipableType getEquipableType() {
        return equipableType;
    }

    public void setEquipableType(EquipableType equipableType) {
        this.equipableType = equipableType;
    }

    
    
}
