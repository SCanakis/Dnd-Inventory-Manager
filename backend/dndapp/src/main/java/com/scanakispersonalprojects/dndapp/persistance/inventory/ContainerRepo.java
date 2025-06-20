package com.scanakispersonalprojects.dndapp.persistance.inventory;


import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.inventory.containers.Container;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerId;

/**
 * Repository interface for Container entity operations.
 * Provides database access methods for managing character inventory containers,
 * including retrieval and capacity management operations.
 */
@Repository
public interface ContainerRepo extends JpaRepository<Container, ContainerId>{

    /**
     * Retrieves all containers belonging to a specific character.
     * 
     * @param charUuid the unique identifier of the character
     * @return list of containers owned by the character, empty list if none found
     */
    @Query(value = "SELECT * FROM container " + 
                    "WHERE char_uuid = :charUuid ;"
                    , nativeQuery = true)

    List<Container> getCharactersContainers(@Param("charUuid") UUID charUuid);

    /**
     * Updates the current capacity (number of items stored) for a specific container.
     * This method modifies the database directly and should be used within
     * a transactional context.
     * 
     * @param charUuid the unique identifier of the character who owns the container
     * @param containerUuid the unique identifier of the container to update
     * @param quantity the new current capacity value to set
     */
    @Modifying
    @Query(value =  "UPDATE container SET " +
                    "current_consumed = :quantity " +
                    "WHERE char_uuid = :charUuid AND " +
                    "container_uuid = :containerUuid" ,
                    nativeQuery= true)
    void updateCurrentCapacity(@Param("charUuid") UUID charUuid, @Param("containerUuid") UUID containerUuid, @Param("quantity") int quantity);
}
