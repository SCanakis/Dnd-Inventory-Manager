package com.scanakispersonalprojects.dndapp.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CharacterBasicInfoView(
    UUID charInfoUUID,
    String name,
    boolean inspiration,
    String background,
    UUID backgroundUUID,
    String race,
    UUID raceUUID,
    Map<AbilityScore, Integer> abilityScores,
    List<DndClass> classes
) {}
