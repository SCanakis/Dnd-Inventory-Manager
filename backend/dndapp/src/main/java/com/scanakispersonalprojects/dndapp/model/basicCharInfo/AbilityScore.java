package com.scanakispersonalprojects.dndapp.model.basicCharInfo;


/**
 * 
 * Enumeration of the Ability Scores (AS) in DND 5e currently. 
 * 
 * @param shorthand     The shorthand String values associated with each AS is the name of 
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

    public final String shorthand;

    private AbilityScore(String shorthand) {
        this.shorthand = shorthand;
    }

    public String getString(){
        return shorthand;
    }
}
