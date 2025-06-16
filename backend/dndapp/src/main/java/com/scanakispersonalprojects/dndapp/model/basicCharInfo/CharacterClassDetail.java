package com.scanakispersonalprojects.dndapp.model.basicCharInfo;


import java.util.UUID;



public record CharacterClassDetail(
    UUID classUuid,
    String className,
    HitDiceValue hitDiceValue,
    UUID subclassUuid,
    String subclassName,
    Short level,
    Short hitDiceRemaining
) {
    public CharacterClassDetail {

        if (subclassName == null) {
            subclassName = "";
        }
    }
    

    public String getDisplayName() {
        if (subclassName.isEmpty()) {
            return String.format("%s %d", className, level);
        }
        return String.format("%s (%s) %d", className, subclassName, level);
    }

  
    public String getHitDieNotation() {
        return "d" + hitDiceValue;
    }
}
