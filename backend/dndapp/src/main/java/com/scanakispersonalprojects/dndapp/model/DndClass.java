package com.scanakispersonalprojects.dndapp.model;

import java.util.UUID;


/**
 * 
 * An immutable, read-only class of the basic class information needed for the 
 * {@link CharacterBasicInfoView}. 
 * It hsould NEVER be passed back into a write API. 
 * 
 * @param classUuid         Unique identifer of the class in {@code class}
 * @param className         display name of class
 * @param subClassUuid      Unique identifer of the subclass in {@code subclass}
 * @param subClassName      display name of subclass
 * @param level             Amount of levels the character has dipped into this class
 * @param currentHitDice    The Amount of this classes HitDice the character has remaing bf long rest/
 * @param hitDiceValue      The dice value of each hitDie. 
 * 
 */


public record DndClass(
    UUID classUuid,
    String className,
    UUID subClassUuid,
    String subClassName,
    short level,
    short currentHitDice,
    short hitDiceValue
) {
 
}
