package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RollType {
    ADVANTAGE("advantage"),
    STRAIGHT("straight"),
    DISADVANTAGE("disadvantage");

    private final String jsonValue;

    RollType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    
}
