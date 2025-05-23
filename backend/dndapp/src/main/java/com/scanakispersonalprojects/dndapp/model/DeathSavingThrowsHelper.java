package com.scanakispersonalprojects.dndapp.model;


/**
 * 
 * Helper Read only record for the CharcterBasicInfoView. This record
 * should NEVER be passed back into a Write API.
 * 
 * @param success   The number of succesful death saving throw the character has rolled (0 < success  <3)
 * @param failure   The number of failed death saving throw the character has rolled (0 < failure  <3)
 */

public record DeathSavingThrowsHelper(
    short success,
    short failure
) {}
