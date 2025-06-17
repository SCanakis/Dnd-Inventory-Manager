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
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfoUpdateDTO;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.BackgroundRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.CharacterClassRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.CharacterInfoRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.DndClassRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.RaceRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.SubClassRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.UserDaoPSQL;

import jakarta.transaction.Transactional;

@Service
public class CharacterInfoService {
    
    private CharacterInfoRepo characterInfoRepo;

    private CharacterClassRepo characterClassRepo;

    private DndClassRepo dndClassRepo;

    private SubClassRepo subClassRepo;

    private RaceRepo raceRepo;

    private BackgroundRepo backgroundRepo;

    private UserDaoPSQL userDao;

    public CharacterInfoService(CharacterInfoRepo characterInfoRepo, CharacterClassRepo characterClassRepo,
            DndClassRepo dndClassRepo, SubClassRepo subClassRepo, RaceRepo raceRepo, BackgroundRepo backgroundRepo, UserDaoPSQL userDao) {
        this.characterInfoRepo = characterInfoRepo;
        this.characterClassRepo = characterClassRepo;
        this.dndClassRepo = dndClassRepo;
        this.subClassRepo = subClassRepo;
        this.raceRepo = raceRepo;
        this.backgroundRepo = backgroundRepo;
        this.userDao = userDao;
    }

    public CharacterBasicInfoView getCharacterBasicInfoView(UUID charInfoUuid) {
        Optional<CharacterInfo> charInfoOptional = characterInfoRepo.findById(charInfoUuid);
        if(charInfoOptional.isPresent()) {
            CharacterInfo charInfo = charInfoOptional.get();
            List<CharacterClassDetail> classes = getCharacterClassDetails(charInfoUuid);
            String raceName = getRaceName(charInfo.getRaceUuid());
            String bgName = getBackgroundName(charInfo.getBackgroundUuid());
        
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
        if(raceUuid == null) {
            return "No Race";
        }
        Optional<Race> raceOptional = raceRepo.findById(raceUuid);
        Race race = raceOptional.get();
        return race.getName();
    }
 
    public String getBackgroundName(UUID backgroundUuid) {
    if (backgroundUuid == null) {
        return "No Background";
    }
    
    Optional<Background> background = backgroundRepo.findById(backgroundUuid);
    if (background.isPresent()) {
        return background.get().getName();
    } else {
        System.out.println("Background not found for UUID: " + backgroundUuid);
        return "Unknown Background";
    }
}

    @Transactional
    public boolean deleteCharacter(UUID charInfoUuid, UUID userUuid) {
        try {
            System.out.println("Attempting to delete character: " + charInfoUuid);
            System.out.println("For user: " + userUuid);
            
            Optional<CharacterInfo> characterInfoOptional = characterInfoRepo.findById(charInfoUuid);
            System.out.println("Character found: " + characterInfoOptional.isPresent());
            
            if(characterInfoOptional.isPresent()) {
                System.out.println("Character found, attempting delete...");
                
                // Delete from users_characters first
                userDao.deleteCharacter(userUuid, charInfoUuid);
                System.out.println("Deleted from users_characters");
                
                // Delete character classes
                characterClassRepo.deleteCharacterClasses(charInfoUuid);
                System.out.println("Deleted character classes");
                
                // Delete character
                characterInfoRepo.deleteById(charInfoUuid);
                System.out.println("Deleted character");
                
                return true;
            }
            System.out.println("Character not found");
            return false;
        } catch (Exception e) {
            System.out.println("Delete failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Transactional
    public List<CharacterClassDetail> updateCharacterClassDetail(UUID charInfoUuid, List<CharacterClassDetail> characterClassDetails) {
        List<CharacterClassDetail> result = new ArrayList<>();
        try {
            for(CharacterClassDetail dndClass : characterClassDetails) {
                Optional<CharacterClass> existingClassOptional = characterClassRepo
                .findByCharInfoUuidAndClassUuid(charInfoUuid, dndClass.classUuid());

                if(existingClassOptional.isPresent()) {
                    CharacterClass existingClass = existingClassOptional.get();

                    existingClass.setSubclassUuid(dndClass.subclassUuid());
                    existingClass.setLevel(dndClass.level());
                    existingClass.setHitDiceRemaining(dndClass.hitDiceRemaining());

                    CharacterClass savedClass = characterClassRepo.save(existingClass);

                    result.add(mapToCharacterClassDetail(savedClass));
                }
            }
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
 
    @Transactional
    public CharacterBasicInfoView updateCharInfo(UUID uuid, CharacterInfoUpdateDTO updateDTO) {
        Optional<CharacterInfo> existsOptional = characterInfoRepo.findById(uuid);
            if(existsOptional.isPresent()) {
                CharacterInfo existing = existsOptional.get();
                
                // Update only non-null fields from DTO
                if(updateDTO.getName() != null) {
                    existing.setName(updateDTO.getName());
                }
                if(updateDTO.getInspiration() != null) {
                    existing.setInspiration(updateDTO.getInspiration());
                }
                if(updateDTO.getBackgroundUuid() != null) {
                    existing.setBackgroundUuid(updateDTO.getBackgroundUuid());
                }
                if(updateDTO.getRaceUuid() != null) {
                    existing.setRaceUuid(updateDTO.getRaceUuid());
                }
                if(updateDTO.getAbilityScores() != null) {
                    existing.setAbilityScores((updateDTO.getAbilityScores()));
                }
                if(updateDTO.getHpHandler() != null) {
                    existing.setHpHandler(updateDTO.getHpHandler());
                }
                if(updateDTO.getDeathSavingThrowsHelper() != null) {
                    existing.setDeathSavingThrowsHelper(updateDTO.getDeathSavingThrowsHelper());
                }
                
                characterInfoRepo.save(existing);
                return getCharacterBasicInfoView(uuid);
            }
    return null;

    }

    @Transactional
    public CharacterBasicInfoView updateUsingPatch(UUID uuid, CharacterInfoUpdateDTO updatePatch) {
        try {
            // Update character basic info
            updateCharInfo(uuid, updatePatch);
            
            // Update character classes if provided
            if(updatePatch.getCharacterClassDetail() != null && !updatePatch.getCharacterClassDetail().isEmpty()) {
                updateCharacterClassDetail(uuid, updatePatch.getCharacterClassDetail());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getCharacterBasicInfoView(uuid);
    }



}
