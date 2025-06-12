package com.scanakispersonalprojects.dndapp.model.inventory;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.inventory.components.AbilityRequirmentComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.ArmorComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.EquippableComponent;
import com.scanakispersonalprojects.dndapp.model.inventory.components.SkillAlteredComponent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="TBD")
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID itemUuid;
    
    private String name;
    private String description;
    private short value;
    
    
    private EquippableComponent equippableComponent;
    private ArmorComponent armorComponent;
    private AbilityRequirmentComponent abilityRequirmentComponent;
    private SkillAlteredComponent skillAlteredComponent;


    public Item(UUID itemUuid, String name, String description, short value) {
        this.itemUuid = itemUuid;
        this.name = name;
        this.description = description;
        this.value = value;
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


    public short getValue() {
        return value;
    }


    public void setValue(short value) {
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


    public AbilityRequirmentComponent getAbilityRequirmentComponent() {
        return abilityRequirmentComponent;
    }


    public void setAbilityRequirmentComponent(AbilityRequirmentComponent abilityRequirmentComponent) {
        this.abilityRequirmentComponent = abilityRequirmentComponent;
    }


    public SkillAlteredComponent getSkillAlteredComponent() {
        return skillAlteredComponent;
    }


    public void setSkillAlteredComponent(SkillAlteredComponent skillAlteredComponent) {
        this.skillAlteredComponent = skillAlteredComponent;
    }

    

    
    


    
}
