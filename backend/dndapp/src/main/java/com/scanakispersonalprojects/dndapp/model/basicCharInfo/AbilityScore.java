package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AbilityScore {
    strength,
    dexterity,
    constitution,
    intelligence,
    wisdom,
    charisma;

    @JsonValue
    public String getJsonValue() {
        return this.name();
    }
}