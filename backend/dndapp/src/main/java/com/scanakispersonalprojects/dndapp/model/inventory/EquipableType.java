package com.scanakispersonalprojects.dndapp.model.inventory;

public enum EquipableType {
    ARMOR("armor"),
    CLOAK("cloak"),
    BRACERS("bracers"),
    HEAD("head"),
    BELT("belt"),
    HANDS("hands"),
    RING_L("ringl"),     
    RING_R("ringr"),       
    FEET("feet"),
    MAINHAND("mainhand"),
    OFFHAND("offhand"),
    TWOHAND("twohand"),
    BACK("back"),
    SPELLFOCUS("spellfocus"),
    CUSTOM("custom");
    
    private final String lowercase;

    private EquipableType(String lowercase) {
        this.lowercase = lowercase;
    }

    public String getLowercase() {
        return lowercase;
    }
}


