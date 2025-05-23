package com.scanakispersonalprojects.dndapp.model;

import java.util.UUID;

public record DndClass(
    UUID classUuid,
    String className,
    UUID subclassUuid,
    String subClassName,
    short level,
    short currentHitDice,
    short hitDiceValue
) {
 
}
