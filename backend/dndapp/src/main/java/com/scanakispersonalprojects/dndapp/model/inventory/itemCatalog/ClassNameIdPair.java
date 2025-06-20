package com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog;

import java.util.Objects;
import java.util.UUID;


/**
 * Data transfer object representing a D&D class requirement for an item.
 * Used to store class restrictions that determine which character classes
 * can use, equip, or attune to specific items in the catalog.
 * 
 * For example, a "Holy Avenger" sword might require the "Paladin" class,
 * or a spellcaster focus might require "Wizard" or "Sorcerer" classes.
 */
public class ClassNameIdPair {
    private UUID classUuid;
    private String className;
    
    public ClassNameIdPair(UUID classUuid, String className) {
        this.classUuid = classUuid;
        this.className = className;
    }

    public UUID getClassUuid() {
        return classUuid;
    }

    public void setClassUuid(UUID classUuid) {
        this.classUuid = classUuid;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ClassNameIdPair that = (ClassNameIdPair) obj;
        return Objects.equals(classUuid, that.classUuid) && 
               Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classUuid, className);
    }

    
}
