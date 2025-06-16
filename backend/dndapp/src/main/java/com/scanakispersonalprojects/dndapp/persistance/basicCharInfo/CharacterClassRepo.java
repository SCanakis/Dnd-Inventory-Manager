package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterClass;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterHasClassId;

@Repository
public interface CharacterClassRepo extends JpaRepository<CharacterClass, CharacterHasClassId>{


    List<CharacterClass> findByIdCharInfoUuid(UUID charInfoUuid);

    @Query("SELECT cc FROM CharacterClass cc WHERE cc.id.charInfoUuid = :charInfoUuid AND cc.id.classUuid = :classUuid")
    Optional<CharacterClass> findByCharInfoUuidAndClassUuid(@Param("charInfoUuid") UUID charInfoUuid, @Param("classUuid") UUID classUuid);
}
