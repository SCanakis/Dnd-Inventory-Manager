package com.scanakispersonalprojects.dndapp.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scanakispersonalprojects.dndapp.model.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.persistance.CharacterDao;


/**
 * Servvice layer for managing D&D character basic information.
 * 
 * Provides method to retrieve and apply partial updates (patches) to a character's data.
 * Delagates persistance operation to the underlying {@link CharacterDao}
 * All operations are executed within a transactional context.
 */


@Service
public class CharacterService {
    
    /**
     * Data-access object for character persistance operations.
     */
    private final CharacterDao dao;


    /**
     * Constructs a new {@link CharacterSerives} with a given DAO
     * 
     * @param dao the {@link CharacterDao} used for the charcter data access.
     */
    public CharacterService(CharacterDao dao) {
        this.dao = dao;
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
            dao.updateCurrentHealth(uuid, patch.currentHP());
        }
        if(patch.tempHP() > -1){
            dao.updateTempHealth(uuid, patch.tempHP());
        }
        for(var e : patch.hitDice().entrySet()) {
            dao.updateHitDice(uuid, e.getKey(), e.getValue());
        }
        if(patch.name() != null) {
            dao.updateName(uuid, patch.name());
        }
        if(patch.success() > -1) {
            dao.updateSuccessST(uuid, patch.success());
        }
        if(patch.failure() > -1) {
            dao.updateFailureST(uuid, patch.failure());
        }
        if(patch.inspiration() != null) {
            dao.updateInspiration(uuid, patch.inspiration());
        }
        for(var e : patch.abilityScore().entrySet()) {
            dao.updateAbilityScore(uuid, e.getValue(), e.getKey());
        }

        return dao.getCharInfo(uuid);
    }
    
    /**
     * 
     * Retrieves teh basic information view for the character with the specified UUID.
     * 
     * @param uuid      the UUID of the character to be fetched
     * @return          a {@link CharacterBasicInfoView} representing the charcter's current state.
     */

    @Transactional(readOnly = true)
    public CharacterBasicInfoView getCharInfo(UUID uuid) {
        try {
            return dao.getCharInfo(uuid);
        } catch (Exception e) {
            return null;
        }
    }
    
}
