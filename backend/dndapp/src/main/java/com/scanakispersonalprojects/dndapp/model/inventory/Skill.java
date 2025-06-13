package com.scanakispersonalprojects.dndapp.model.inventory;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Skill {
    
    ACROBATICS("acrobatics"),
    ANIMAL_HANDLING("animal_handling"),
    ARCANA("arcana"),
    ATHLETICS("athletics"),
    DECEPTION("deception"),
    HISTORY("history"),
    INSIGHT("insight"),
    INTIMIDATION("intimidation"),
    INVESTIGATION("investigation"),
    MEDICINE("medicine"),
    NATURE("nature"),
    PERCEPTION("perception"),
    PERFORMANCE("performance"),
    PERSUASION("persuasion"),
    RELIGION("religion"),
    SLEIGHT_OF_HAND("sleight_of_hand"),
    STEALTH("stealth"),
    SURVIVAL("survival"); 

    private final String jsonValue; 

    private Skill(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }



}
