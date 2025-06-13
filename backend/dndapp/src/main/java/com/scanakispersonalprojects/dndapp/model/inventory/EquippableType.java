package com.scanakispersonalprojects.dndapp.model.inventory;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EquippableType {
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
    
    private final String jsonValue;

    private EquippableType(String jsonValue) {
        this.jsonValue = jsonValue;
    }
    
    @JsonValue
    public String getjsonValue() {
        return jsonValue;
    }
}


