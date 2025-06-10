package com.scanakispersonalprojects.dndapp.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scanakispersonalprojects.dndapp.model.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.persistance.CharacterInfoDao;
import com.scanakispersonalprojects.dndapp.persistance.UserDao;


/**
 * Servvice layer for managing D&D character basic information.
 * 
 * Provides method to retrieve and apply partial updates (patches) to a character's data.
 * Delagates persistance operation to the underlying {@link CharacterInfoDao}
 * All operations are executed within a transactional context.
 */


@Service
public class CharacterService {
    
    /**
     * Data-access object for character persistance operations.
     */
    private final CharacterInfoDao charDao;

    private final UserDao userDao;


    /**
     * Constructs a new {@link CharacterSerives} with a given DAO
     * 
     * @param charDao the {@link CharacterInfoDao} used for the charcter data access.
     */
    public CharacterService(CharacterInfoDao charDao, UserDao userDao) {
        this.charDao = charDao;
        this.userDao = userDao;
    }

    /**
     * 
     * Applies the provided patch for the character identified by the givne UUID,
     * updating only the specified fields.
     * - CurrentHP
     * - TempHp
     * - Hit dice remaining
     * - Characer name
     * - Death Saving throw successes and failures
     * - inspiration
     * - Ability Scores
     * 
     * After applying all the updates, the method returns the latest {@link CharacterBasicInfoView}
     * 
     * @param uuid      the UUID of the character to update
     * @param patch     a {@link CharViewPatch} containing only the fileds to be modified.
     *                  fields left at tei default values (-1, null) will be ignored.
     * 
     * @return the update {@link CharacterBasicInfoView} for the character.
     */

    @Transactional
    public CharacterBasicInfoView updateCharInfo(UUID uuid, CharViewPatch patch) {
        if(patch.currentHP() > -1){
            charDao.updateCurrentHealth(uuid, patch.currentHP());
        }
        if(patch.tempHP() > -1){
            charDao.updateTempHealth(uuid, patch.tempHP());
        }
        for(var e : patch.hitDice().entrySet()) {
            charDao.updateHitDice(uuid, e.getKey(), e.getValue());
        }
        if(patch.name() != null) {
            charDao.updateName(uuid, patch.name());
        }
        if(patch.success() > -1) {
            charDao.updateSuccessST(uuid, patch.success());
        }
        if(patch.failure() > -1) {
            charDao.updateFailureST(uuid, patch.failure());
        }
        if(patch.inspiration() != null) {
            charDao.updateInspiration(uuid, patch.inspiration());
        }
        for(var e : patch.abilityScore().entrySet()) {
            charDao.updateAbilityScore(uuid, e.getValue(), e.getKey());
        }

        return charDao.getCharInfo(uuid);
    }
    
    /**
     * 
     * Retrieves the basic information view for the character with the specified UUID.
     * 
     * @param uuid      the UUID of the character to be fetched
     * @return          a {@link CharacterBasicInfoView} representing the charcter's current state.
     */

    @Transactional(readOnly = true)
    public CharacterBasicInfoView getCharInfo(UUID uuid) {
        try {
            return charDao.getCharInfo(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deletes the character from the the Table
     * 
     * @param userUuid  the UUID of the character to be deleted
     * @param charUuid  the UUID of user who that character belongs to 
     * 
     * @return returns result if the character is deleted or not found
     * if one the character is able to be found in one table
     * and not the other then an IllegalStateException occcurs
     * and changes get reverted.
     */

    @Transactional
    public boolean deleteCharacter(UUID userUuid, UUID charUuid) {
        int userRows = userDao.deleteCharacter(userUuid, charUuid);
        int charRows = charDao.deleteCharacter(charUuid);
        if(userRows <= 0 && charRows <= 0) {
            return false;
        }
        if(userRows <= 0 || charRows <= 0 ) {
            throw new IllegalStateException("Inconsistent deletion of " + charUuid + " and it's realtion with " + userUuid );
        }

        return true;
    }
    
}
