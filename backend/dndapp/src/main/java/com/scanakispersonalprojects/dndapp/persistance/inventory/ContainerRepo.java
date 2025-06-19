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

@Repository
public interface ContainerRepo extends JpaRepository<Container, ContainerId>{

    
    @Query(value = "SELECT * FROM container " + 
                    "WHERE char_uuid = :charUuid ;"
                    , nativeQuery = true)

    List<Container> getCharactersContainers(@Param("charUuid") UUID charUuid);


    @Modifying
    @Query(value =  "UPDATE container SET " +
                    "current_consumed = :quantity " +
                    "WHERE char_uuid = :charUuid AND " +
                    "container_uuid = :containerUuid" ,
                    nativeQuery= true)
    void updateCurrentCapacity(@Param("charUuid") UUID charUuid, @Param("containerUuid") UUID containerUuid, @Param("quantity") int quantity);
}
