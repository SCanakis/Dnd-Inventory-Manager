package com.scanakispersonalprojects.dndapp.model.inventory;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.inventory.components.RequirmentComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.ArmorComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.EquippableComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.SkillAlteredComponent;

public class Item {
    private UUID itemUuid;
    
    private String name;
    private String description;
    private int weight;
    private int value;
    private boolean attackable;
    

    private EquippableComponent equippableComponent;
    private ArmorComponent armorComponent;
    private RequirmentComponent requirmentComponent;
    private SkillAlteredComponent skillAlteredComponent;


    public Item(UUID itemUuid, String name, String description,int weight, int value, boolean attackable) {
        this.itemUuid = itemUuid;
        this.name = name;
        this.description = description;
        this.value = value;
        this.weight = weight;
        this.attackable = attackable;
    }


    public UUID getItemUuid() {
        return itemUuid;
    }


    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getValue() {
        return value;
    }


    public void setValue(int value) {
        this.value = value;
    }


    public EquippableComponent getEquippableComponent() {
        return equippableComponent;
    }


    public void setEquippableComponent(EquippableComponent equippableComponent) {
        this.equippableComponent = equippableComponent;
    }


    public ArmorComponent getArmorComponent() {
        return armorComponent;
    }


    public void setArmorComponent(ArmorComponent armorComponent) {
        this.armorComponent = armorComponent;
    }


    public RequirmentComponent getRequirmentComponent() {
        return requirmentComponent;
    }


    public void setRequirmentComponent(RequirmentComponent requirmentComponent) {
        this.requirmentComponent = requirmentComponent;
    }


    public SkillAlteredComponent getSkillAlteredComponent() {
        return skillAlteredComponent;
    }


    public void setSkillAlteredComponent(SkillAlteredComponent skillAlteredComponent) {
        this.skillAlteredComponent = skillAlteredComponent;
    }


    public boolean isAttackable() {
        return attackable;
    }


    public void setAttackable(boolean attackable) {
        this.attackable = attackable;
    }

}
