package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;

public interface CharacterInfoPersistance {

    
    /**
     * Updates the current hit points for a character
     * 
     * @param charUuid      the UUID of the character to update
     * @param currentHP     the new current hit points value
     * @return the number of rows affected
     */

    public int updateCurrentHealth(UUID charUuid, int currentHP);


    /**
     * Updates the temporary hit points for a character.
     * 
     * @param charUuid      the UUID of the character to update
     * @param tempHP        the new temp hit points value
     * @return the number of rows affected
     */
    public int updateTempHealth(UUID charUuid, int tempHP);


    /**
     * Updates the number of remaining hit dice for a class a 
     * character is associated with.
     * 
     * @param charUuid      the UUID of the character to update
     * @param classUuid     the UUId of the class whose hit dice is being update.
     * @param hitDice       the new remaining hit dice
     * @return the number of rows affected
     */

    public int updateHitDice(UUID charUuid, UUID classUuid, int hitDice);

     /**
     * Updates the name of a character.
     * 
     * @param charUuid      the UUID of the character to update
     * @param name          the new name of the character
     * @return the number of rows affected
     */

    public int updateName(UUID charUuid, String name);
    /**
     * Update the number of succesful death saving throws of a character.
     * 
     * @param charUuid      the UUID of the character to update
     * @param success       the new count of successful death saves
     * @return the number of rows affected
     */

    public int updateSuccessST(UUID charUuid, int success);

    /**
     * Update the number of failed death saving throws of a character.
     * 
     * @param charUuid      the UUID of the character to update
     * @param failure       the new count of failed death saves
     * @return the number of rows affected
     */

    public int updateFailureST(UUID charUuid, int failure);

    /**
    * Updates an ability score attribute for a character.
     * 
     * Determiens the database column from the provided {@link AbilityScore}
     * and perfomes a direct SQL update.
     * 
     * @param charUuid      the UUID of the character to update
     * @param value         the new ability score value
     * @param as            the {@link AbilityScore} enum indicating which score to update
     * @return the number of rows affected
     */

    public int updateAbilityScore(UUID charUuid, int value, AbilityScore as);

    /**
     * Updates the inspiration indicator for a character.
     * 
     * 
     * @param charUuid      the UUID of the character to update
     * @param inspiration   the new inspiration value
     * @return the number of rows affected
     */
    public int updateInspiration(UUID charUuid, boolean inspiration);

    /**
     * Deletes a character in the character_info
     * @param charUuid      the UUID of the charcter to delete
     * @return the number of rows affected
     */

    public int deleteCharacter(UUID charUuid);

    /**
     * Retrives the basic info view for a character, including
     * ability scores, classes, HP handler, and death saving throws.
     * 
     * Returns null if the query fails or no record is found
     * 
     * @param uuid      the UUID of teh charcter to fetch
     * @return a {@link CharcterBasicInfoView} with aggregated data,
     *          or null if retrival fails.
     */        

    public CharacterBasicInfoView getCharInfo(UUID uuid);

}
