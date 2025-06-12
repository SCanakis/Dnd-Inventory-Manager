package com.scanakispersonalprojects.dndapp.model.inventory;

public enum Rarity {
    COMMON ("Common"),
    UNCOMMON ("Uncommon"),
    RARE ("Rare"),
    VERY_RARE ("Very Rare"),
    LEGENDARY ("Legendary");

    private final String theString; 

    private Rarity(String theString) {
        this.theString = theString;
    }

    public String getTheString() {
        return theString;
    }
}
