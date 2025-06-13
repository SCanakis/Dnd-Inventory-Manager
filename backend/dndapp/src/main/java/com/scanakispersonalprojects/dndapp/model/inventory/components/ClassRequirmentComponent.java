package com.scanakispersonalprojects.dndapp.model.inventory.components;

import java.util.Map;
import java.util.UUID;

public class ClassRequirmentComponent {
    
    private Map<UUID, String> classRestrictions;

    public ClassRequirmentComponent(Map<UUID, String> classRestrictions) {
        this.classRestrictions = classRestrictions;
    }

    public Map<UUID, String> getClassRestrictions() {
        return classRestrictions;
    }

    public void setClassRestrictions(Map<UUID, String> classRestrictions) {
        this.classRestrictions = classRestrictions;
    } 

    

}
