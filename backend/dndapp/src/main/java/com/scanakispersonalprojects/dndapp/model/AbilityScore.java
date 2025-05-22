package com.scanakispersonalprojects.dndapp.model;

public enum AbilityScore {
    STRENGTH ("strength"),
    DEXTERITY ("dexterity"),
    CONSTITUION ("constitution"),
    INTELLIGENCE ("intelligence"),
    WISDOM("wisdom"), 
    CHARISMA ("charisma");

    public final String shorthand;

    private AbilityScore(String shorthand) {
        this.shorthand = shorthand;
    }

    public String getString(){
        return shorthand;
    }
}
