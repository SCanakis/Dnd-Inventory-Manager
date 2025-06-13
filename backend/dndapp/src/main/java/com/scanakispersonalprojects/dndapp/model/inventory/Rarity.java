package com.scanakispersonalprojects.dndapp.model.inventory;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Rarity {
    COMMON ("common"),
    UNCOMMON ("uncommon"),
    RARE ("rare"),
    VERY_RARE ("very_rare"),
    LEGENDARY ("legendary");

    private final String jsonValue; 

    private Rarity(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getjsonValue() {
        return jsonValue;
    }
}
