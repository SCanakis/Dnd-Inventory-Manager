package com.scanakispersonalprojects.dndapp.service.basicCharInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Background;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.BasicCharInfoCreationDTO;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClass;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClassDetail;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfo;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.HPHandler;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Race;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Subclass;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfoUpdateDTO;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DeathSavingThrowsHelper;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.BackgroundRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.CharacterClassRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.CharacterInfoRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.DndClassRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.RaceRepo;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.SubClassRepo;
import com.scanakispersonalprojects.dndapp.service.coinPurse.CoinPurseService;

import jakarta.transaction.Transactional;

/**
 * This service manages all the business logic in CRUD operations
 * regarding basic character information in the Dnd app.
 *  
 * This service handles character creation, updates, deletions, and retrivals
 * operation, including managing relationships between cahracter and their
 * classes, races, and backgrounds.
 */

@Service
public class CharacterInfoService {
    
    /** Repository for character information ops */
    private CharacterInfoRepo characterInfoRepo;

    /** Repository for character class relationship operation */
    private CharacterClassRepo characterClassRepo;

    /** Repository for dnd class definitions */
    private DndClassRepo dndClassRepo;

    /** Repository for dnd subclass definitions */
    private SubClassRepo subClassRepo;

    /** Repository for dnd race definitions */
    private RaceRepo raceRepo;

    /** Repository for dnd background definitions */
    private BackgroundRepo backgroundRepo;


    private CharacterLinkService characterLinkService;

    private CoinPurseService coinPurseService;

    /**
     * Constructs a new CharacterInfoServie with the requires repo dependenceis
     * 
     * @param characterInfoRepo
     * @param characterClassRepo
     * @param dndClassRepo
     * @param subClassRepo
     * @param raceRepo
     * @param backgroundRepo
     * @param characterLinkService
     */

    public CharacterInfoService(CharacterInfoRepo characterInfoRepo, CharacterClassRepo characterClassRepo,
            DndClassRepo dndClassRepo, SubClassRepo subClassRepo, RaceRepo raceRepo, BackgroundRepo backgroundRepo, CharacterLinkService characterLinkService,
            CoinPurseService coinPurseService) {
        this.characterInfoRepo = characterInfoRepo;
        this.characterClassRepo = characterClassRepo;
        this.dndClassRepo = dndClassRepo;
        this.subClassRepo = subClassRepo;
        this.raceRepo = raceRepo;
        this.backgroundRepo = backgroundRepo;
        this.characterLinkService = characterLinkService;
        this.coinPurseService = coinPurseService;
    }


    /**
     * Retrieves a complete view of a character's basic information including
     * classes, race, background, and game statistics.
     *  
     * @param charInfoUuid - the unique character identifier
     * @return CharacterBasicInfoView - containng all character details, or null if not found
     */

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

    /**
     * Retrives detailed information about all classes for a specific character
     * 
     * @param charInfoUuid the character identifer
     * @return list of CharacterClassDetail containing class information
     */
    public List<CharacterClassDetail> getCharacterClassDetails(UUID charInfoUuid) {
        List<CharacterClass> characterClasses = characterClassRepo.findByIdCharInfoUuid(charInfoUuid);
        
        List<CharacterClassDetail> result = new ArrayList<>();
        for(CharacterClass aClass : characterClasses) {
            result.add(mapToCharacterClassDetail(aClass));
        }
        return result;
    }

    /**
     * Maps of CharacterClass entity to a CahracterClassDetail view of object,
     * including class name, subclass information, and level details.
     * 
     * @param characterClass - the character class entity to map
     * @return CharacteClassDetail view objects, or null if class not found
     */
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

    /**
     * Get race name using race idenifier
     * 
     * @param raceUuid
     * @return
     */
    public String getRaceName(UUID raceUuid) {
        if(raceUuid == null) {
            return "No Race";
        }
        Optional<Race> raceOptional = raceRepo.findById(raceUuid);
        Race race = raceOptional.get();
        return race.getName();
    }
 
    /**
     * Get race background using background idenifier
     * 
     * @param backgroundUuid
     * @return
     */
    public String getBackgroundName(UUID backgroundUuid) {
        if (backgroundUuid == null) {
            return "No Background";
        }
        
        Optional<Background> background = backgroundRepo.findById(backgroundUuid);
        if (background.isPresent()) {
            return background.get().getName();
        } else {
            return "Unknown Background";
        }
    }

    /**
     * Deletes a charcter and all associated data including class relationships
     * and user association.
     * 
     * @param charInfoUuid - the unique identifier of teh character to delete
     * @param userUuid - the user identifer of the use who owns the character
     * @return - true if deleted
     */
    @Transactional
    public boolean deleteCharacter(UUID charInfoUuid, UUID userUuid) {
        try {
            
            Optional<CharacterInfo> characterInfoOptional = characterInfoRepo.findById(charInfoUuid);
            
            if(characterInfoOptional.isPresent()) {
                
                characterLinkService.unlinkCharacter(userUuid, charInfoUuid);
                characterClassRepo.deleteCharacterClasses(charInfoUuid);
                characterInfoRepo.deleteById(charInfoUuid);
                
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates character class details including subclass, level, and hit dice information
     * 
     * @param charInfoUuid - the unique identifer of the character
     * @param characterClassDetails - list of updated class details
     * @return - list of updated CharacterClassDetail objects, or null if update failed.
     */
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
            return null;
        }

    }
 
    /**
     * Updates character information with the provided data. Only non-null
     * fileds in teh update DTO will be applied to the character.
     * 
     * @param uuid - the character identifer
     * @param updateDTO - the data transfer onbject containing update information
     * @return updated CharacterBasicInfoView, or null if character not found.
     */

    @Transactional
    public CharacterBasicInfoView updateCharInfo(UUID uuid, CharacterInfoUpdateDTO updateDTO) {
        Optional<CharacterInfo> existsOptional = characterInfoRepo.findById(uuid);
            if(existsOptional.isPresent()) {
                CharacterInfo existing = existsOptional.get();
                
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
                if(updateDTO.getAbilityScores() != null && !updateDTO.getAbilityScores().isEmpty()) {
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

    /**
     * Perfomrs a patch update on character information, updating both 
     * basic character info and class details if provided.
     * 
     * @param uuid - the unique idneitifer of the character to update
     * @param updatePatch - the patch dat containing updated information
     * @return updated CharacterBasicInfoView after appliying all changes.
     */
    @Transactional
    public CharacterBasicInfoView updateUsingPatch(UUID uuid, CharacterInfoUpdateDTO updatePatch) {
        try {
            updateCharInfo(uuid, updatePatch);
            
            if(updatePatch.getCharacterClassDetail() != null && !updatePatch.getCharacterClassDetail().isEmpty()) {
                updateCharacterClassDetail(uuid, updatePatch.getCharacterClassDetail());
            }
            
        } catch (Exception e) {
        }

        return getCharacterBasicInfoView(uuid);
    }


    @Transactional
    public boolean createCharacter(UUID userUuid, BasicCharInfoCreationDTO dto) {
        try {

            if(raceRepo.existsById(dto.getRaceUuid()) && backgroundRepo.existsById(dto.getBackgroundUuid())) {
                
                CharacterInfo characterInfo = new CharacterInfo(
                    dto.getName(),
                    dto.getRaceUuid(),
                    dto.getBackgroundUuid()
                );
                characterInfo.setAbilityScores(dto.getAbilityScores());
                characterInfo.setHpHandler(new HPHandler(0, 0, 0));
                characterInfo.setDeathSavingThrowsHelper(new DeathSavingThrowsHelper(0,0));
                characterInfo = characterInfoRepo.save(characterInfo);

                
                if(characterInfo == null) {
                    return false;
                }

                UUID charInfoUuid = characterInfo.getCharInfoUuid();

                for(CharacterClassDetail classDetail : dto.getCharacterClassDetails()) {
                    if(dndClassRepo.existsById(classDetail.classUuid())) {
                        
                        CharacterClass charClass = new CharacterClass(
                            charInfoUuid,
                            classDetail.classUuid(),
                            classDetail.subclassUuid(),
                            classDetail.level(),
                            classDetail.level()
                        );
                        characterClassRepo.save(charClass);
                    } else {
                        return false;
                    }
                }
                characterLinkService.linkCharacter(userUuid, charInfoUuid);
                coinPurseService.createFreshCoinPurse(charInfoUuid);
                return true;

            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


}
