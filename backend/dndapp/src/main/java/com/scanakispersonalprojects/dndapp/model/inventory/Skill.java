package com.scanakispersonalprojects.dndapp.model.inventory;

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

    private final String dataBaseString; 

    private Skill(String dataBaseString) {
        this.dataBaseString = dataBaseString;
    }

    public String getDataBaseString() {
        return dataBaseString;
    }



}
