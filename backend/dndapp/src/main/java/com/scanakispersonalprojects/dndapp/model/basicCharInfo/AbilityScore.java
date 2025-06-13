package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * Enumeration of the Ability Scores (AS) in DND 5e currently. 
 * 
 * @param jsonValue     The jsonValue String values associated with each AS is the name of 
 *                      it's column in the @code characters_info table.
 * 
 */



public enum AbilityScore {
    STRENGTH ("strength"),
    DEXTERITY ("dexterity"),
    CONSTITUTION ("constitution"),
    INTELLIGENCE ("intelligence"),
    WISDOM("wisdom"), 
    CHARISMA ("charisma");

    public final String jsonValue;

    private AbilityScore(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue(){
        return jsonValue;
    }
}
