package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.User;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.UserRoleProjection;

/**
 * Spring Data JPA repository for the {@link User} entity. 
 * 
 * Most CRUD operations are inheritaed from {@link JpaRepository}
 * The extra method blow cover the few queries that are unique to the app.
 */
@Repository
public interface UserRepo extends JpaRepository<User, UUID>{

    
    Optional<User> findByUsername(String username);

    Optional<User> findByUuid(UUID uuid);

    boolean existsByUsername(String username);
    
    boolean existsByUuid(UUID uuid);


    /**
     * Get all character IDs that belong to a user.
     * @param userUuid      the user's UUID
     * @return              list of character UUIDs
     */
    @Query(value = "SELECT character_uuid FROM users_characters WHERE user_uuid= :userUuid", nativeQuery = true)
    List<UUID> findCharacterUuidByUserUuid(@Param("userUuid") UUID userUuid);

    @Query(value = "INSERT INTO users_characters (user_uuid, character_uuid) " + 
                    "VALUES( :userUuid, :characterUuid)", nativeQuery = true)
    void addCharacterUser(@Param("userUuid") UUID userUuid, @Param ("characterUuid") UUID characterUuid);


    /**
     * Remove a link betwen a user and one of their characteres.
     * 
     * @param userUuid      the user's UUID
     * @param characterUuid the character's UUId
     * @return {@code true} if at least one row was deleted.
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users_characters WHERE user_uuid= :userUuid AND character_uuid = :characterUuid", nativeQuery = true)
    int deleteCharacter(@Param("userUuid") UUID userUuid,  @Param("characterUuid") UUID characterUuid);


    @Query(value = "SELECT authority FROM authorities " +
                    "WHERE username = :username;", nativeQuery= true)
    UserRoleProjection getAuthoritiesFromUsername(@Param("username") String username);
}
