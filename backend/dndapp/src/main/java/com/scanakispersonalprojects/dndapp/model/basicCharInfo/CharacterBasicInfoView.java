package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * Immutable, read-only record of the information that will always be displayed to the user
 * in the top of the UI.
 * 
 * 
 * The UI should not have to make any additional API calls or queries to display this section.
 * DO NOT send this record through a write API.
 * 
 * @param charInfoUUID              Unique idneitifer of the character in {@code characters_info}
 * @param name                      Player-deinfed character name
 * @param inspiration               Bard/DM mechanic
 * @param background                Display the name of the background
 * @param backgroundUUID            UUID for the background row (foreign key)
 * @param race                      Display name of the race 
 * @param raceUUID                  UUID for the race row (foreign key)
 * @param abilityScores             Mapping from {@link AbilityScore} to a raw score (0-30)
 * @param classes                   Ordered list of class / level combination (supports multiclassing)
 * @param hpHandler                 current and max hit-point status
 * @param deathSavingThrowsHelper   running count of successes/failures
 * 
 */

public record CharacterBasicInfoView(
    UUID charInfoUUID,
    String name,
    boolean inspiration,
    String background,
    UUID backgroundUUID,
    String race,
    UUID raceUUID,
    Map<AbilityScore, Integer> abilityScores,
    List<CharacterClassDetail> classes, 
    HPHandler hpHandler,
    DeathSavingThrowsHelper deathSavingThrowsHelper
) {}
