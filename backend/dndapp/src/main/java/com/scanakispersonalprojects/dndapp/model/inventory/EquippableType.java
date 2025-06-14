package com.scanakispersonalprojects.dndapp.model.inventory;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EquippableType {
    armor,
    cloak,
    bracers,
    head,
    belt,
    hands,
    ringl,     
    ringr,       
    feet,
    mainhand,
    offhand,
    twohand,
    back,
    spellfocus,
    custom;

    @JsonValue
    public String getJsonValue() {
        return this.name();
    }
}

