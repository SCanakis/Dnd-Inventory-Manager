package com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog;

import com.fasterxml.jackson.annotation.JsonValue;

/** 
 * This enum is used to determine where an item can be equipped and to enforce
 * equipment slot restrictions (e.g., only one item per slot in most cases).
 */

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

