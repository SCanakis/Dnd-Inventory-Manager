package com.scanakispersonalprojects.dndapp.service.basicCharInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Background;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClass;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClassDetail;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfo;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Race;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Subclass;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.BackgroundRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.CharacterClassRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.CharacterInfoRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.DndClassRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.RaceRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.SubClassRepo;

@Service
public class CharacterInfoService {
    
    private CharacterInfoRepo characterInfoRepo;

    private CharacterClassRepo characterClassRepo;

    private DndClassRepo dndClassRepo;

    private SubClassRepo subClassRepo;

    private RaceRepo raceRepo;

    private BackgroundRepo backgroundRepo;

    public CharacterInfoService(CharacterInfoRepo characterInfoRepo, CharacterClassRepo characterClassRepo,
            DndClassRepo dndClassRepo, SubClassRepo subClassRepo, RaceRepo raceRepo, BackgroundRepo backgroundRepo) {
        this.characterInfoRepo = characterInfoRepo;
        this.characterClassRepo = characterClassRepo;
        this.dndClassRepo = dndClassRepo;
        this.subClassRepo = subClassRepo;
        this.raceRepo = raceRepo;
        this.backgroundRepo = backgroundRepo;
    }

    public CharacterBasicInfoView getCharacterBasicInfoView(UUID charInfoUuid) {
        Optional<CharacterInfo> charInfoOptional = characterInfoRepo.findById(charInfoUuid);
        if(charInfoOptional.isPresent()) {
            CharacterInfo charInfo = charInfoOptional.get();
            List<CharacterClassDetail> classes = getCharacterClassDetails(charInfoUuid);
            String raceName = getRaceName(charInfo.getRaceUuid());
            String bgName = getBackgroundName(charInfo.getRaceUuid());
        
            return new CharacterBasicInfoView(
                charInfo.getCharInfoUuid(),
                charInfo.getName(),
                charInfo.getInspiration() != null ? charInfo.getInspiration() : false,
                bgName,
                charInfo.getBackgroundUuid(),
                raceName,
                charInfo.getRaceUuid(),
                charInfo.getAbilityScores(),
                classes,
                charInfo.getHpHandler(),
                charInfo.getDeathSavingThrowsHelper()
            );
        }
        return null;

    }

    public List<CharacterClassDetail> getCharacterClassDetails(UUID charInfoUuid) {
        List<CharacterClass> characterClasses = characterClassRepo.findByIdCharInfoUuid(charInfoUuid);
        
        List<CharacterClassDetail> result = new ArrayList<>();
        for(CharacterClass aClass : characterClasses) {
            result.add(mapToCharacterClassDetail(aClass));
        }
        return result;
    }
    
    public CharacterClassDetail mapToCharacterClassDetail(CharacterClass characterClass) {
        Optional<DndClass> dndClass = dndClassRepo.findById(characterClass.getClassUuid());
        String subClassName = "";

        if(dndClass.isPresent()) {
            if(characterClass.getSubclassUuid() != null) {
                Optional<Subclass> subclass = subClassRepo.findById(characterClass.getSubclassUuid());
                if(subclass.isPresent()) {
                    subClassName = subclass.get().getName();
                }
            }
            return new CharacterClassDetail(
                characterClass.getClassUuid(),
                dndClass.get().getName(),
                dndClass.get().getHitDiceValue(),
                characterClass.getSubclassUuid(),
                subClassName,
                characterClass.getLevel(),
                characterClass.getHitDiceRemaining()
            );
            
        } 
        return null;
        
    }

    public String getRaceName(UUID raceUuid) {
        Optional<Race> raceOptional = raceRepo.findById(raceUuid);
        Race race = raceOptional.get();
        return race.getName();
    }
 
    public String getBackgroundName(UUID backgroundUuid) {
        Optional<Background> backgroundOptional = backgroundRepo.findById(backgroundUuid);
        Background bg= backgroundOptional.get();
        return bg.getName();
    }

    public boolean deleteCharacter(UUID charInfoUuid) {
        Optional<CharacterInfo> characterInfoOptional = characterInfoRepo.findById(charInfoUuid);
        if(characterInfoOptional.isPresent()) {
            characterInfoRepo.deleteById(charInfoUuid);
            characterInfoOptional = characterInfoRepo.findById(charInfoUuid);
            if(characterInfoOptional.isEmpty()) {
                return true;
            }
        }
        return false;
        
    }
 

}
