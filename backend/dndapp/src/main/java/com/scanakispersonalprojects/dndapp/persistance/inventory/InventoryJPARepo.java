package com.scanakispersonalprojects.dndapp.persistance.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemSlotID;

@Repository
public interface InventoryJPARepo extends JpaRepository<CharacterHasItemSlot, CharacterHasItemSlotID>{



    @Query("SELECT ic.itemUuid as itemUuid, " +
           "ic.itemName as itemName, " +
           "ic.itemWeight as itemWeight, " +
           "ic.itemValue as itemValue, " +
           "ic.itemRarity as itemRarity, " +
           "chis.quantity as quantity, " +
           "chis.equipped as equipped, " +
           "chis.attuned as attuned, " +
           "chis.inAttackTab as inAttackTab, " +
           "chis.id.containerUuid as containerUuid " +
           "FROM CharacterHasItemSlot chis " +
           "JOIN ItemCatalog ic ON chis.id.itemUuid = ic.itemUuid " +
           "WHERE chis.id.charUuid = :characterUuid")
    List<CharacterHasItemProjection> getInventoryUsingUUID(@Param("characterUuid") UUID charUuid);


    
}
