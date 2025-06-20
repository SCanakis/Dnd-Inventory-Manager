package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClass;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterHasClassId;

import jakarta.transaction.Transactional;

/**
 * Basic JPA Repository for accessing the character_class postgres table.
 *
 * This is the relationship between character's and their classes
 * allows for multiclassing
 * 
 */
@Repository
public interface CharacterClassRepo extends JpaRepository<CharacterClass, CharacterHasClassId>{

    /**
     * Returns all the character-class relationship associated 
     * with a characterUuid
     * 
     * @param charInfoUuid
     * @return {@link List<CharacterClass>}
     */
    List<CharacterClass> findByIdCharInfoUuid(UUID charInfoUuid);


    @Query("SELECT cc FROM CharacterClass cc WHERE cc.id.charInfoUuid = :charInfoUuid AND cc.id.classUuid = :classUuid")
    Optional<CharacterClass> findByCharInfoUuidAndClassUuid(@Param("charInfoUuid") UUID charInfoUuid, @Param("classUuid") UUID classUuid);

    /**
     * Deletes all character-class relationships assocaited
     * with a charactersUuid
     * 
     * Used for character deletion
     * 
     * @param charInfoUuid
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM character_class WHERE char_info_uuid = :charInfoUuid", nativeQuery = true)
    void deleteCharacterClasses(@Param("charInfoUuid") UUID charInfoUuid);

}
