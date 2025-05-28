package com.scanakispersonalprojects.dndapp.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.scanakispersonalprojects.dndapp.model.User;

@Repository
public interface UserDao extends JpaRepository<User, UUID>{

    Optional<User> findByUsername(String username);

    Optional<User> findByUuid(UUID uuid);

    boolean existsByUsername(String username);
    
    boolean existsByUuid(UUID uuid);


    @Query(value = "SELECT character_uuid FROM users_characters WHERE user_uuid= :userUuid", nativeQuery = true)
    List<UUID> findCharacterUuidByUserUuid(@Param("userUuid") UUID userUuid);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users_characters WHERE user_uuid= :userUuid AND character_uuid = :characterUuid", nativeQuery = true)
    boolean deleteCharacter(@Param("userUuid") UUID userUuid,  @Param("characterUuid") UUID characterUuid);
}
