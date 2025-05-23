package com.scanakispersonalprojects.dndapp.model;

/**
 * 
 * A helper READ ONLY record for the CharacerBasicInfoView. This record 
 * should NEVER be passed back into an Write API endpoint. 
 * 
 * @param currentHP     The number hp of the character currently.
 * @param maxp          The max hp of the character.
 * @param temp          The number of temp hit points of the character currently.
 * 
 */


public record HPHandler(
    int currentHp,
    int maxHp,
    int tempHp
) {
}
