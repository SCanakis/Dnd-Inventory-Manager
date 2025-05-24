package com.scanakispersonalprojects.dndapp.persistance;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scanakispersonalprojects.dndapp.model.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;

@Service
public class CharacterService {
    
    
    private final CharacterDao dao;

    public CharacterService(CharacterDao dao) {
        this.dao = dao;
    }

    @Transactional
    public CharacterBasicInfoView updateCharInfo(UUID uuid, CharViewPatch patch) {
        if(patch.currentHP() != -1){
            dao.updateCurrentHealth(uuid, patch.currentHP());
        }
        if(patch.tempHP() != -1){
            dao.updateTempHealth(uuid, patch.tempHP());
        }
        for(var e : patch.hitDice().entrySet()) {
            dao.updateHitDice(uuid, e.getKey(), e.getValue());
        }
        if(patch.name() != null) {
            dao.updateName(uuid, patch.name());
        }
        if(patch.success() != -1) {
            dao.updateSuccessST(uuid, patch.success());
        }
        if(patch.failure() != -1) {
            dao.updateFailureST(uuid, patch.failure());
        }
        for(var e : patch.abilityScore().entrySet()) {
            dao.updateAbilityScore(uuid, e.getValue(), e.getKey());
        }

        return dao.getCharInfo(uuid);
    }
    


    @Transactional
    public CharacterBasicInfoView getCharInfo(UUID uuid) {
        return dao.getCharInfo(uuid);
    }
    
}
