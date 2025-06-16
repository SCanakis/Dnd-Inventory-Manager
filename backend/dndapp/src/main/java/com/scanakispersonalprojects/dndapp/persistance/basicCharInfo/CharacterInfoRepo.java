package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClass;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfo;

@Repository
public interface CharacterInfoRepo extends JpaRepository<CharacterInfo, UUID>{


    @Query(value = "SELECT * FROM character_class " + 
                    "WHERE char_info_uuid = :charUuid", nativeQuery = true)
    public List<CharacterClass> getCharacterClasses(@Param("charUuid") UUID charUuid);


}
