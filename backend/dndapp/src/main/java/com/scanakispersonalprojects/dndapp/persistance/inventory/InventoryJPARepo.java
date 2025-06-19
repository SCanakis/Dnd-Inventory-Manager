package com.scanakispersonalprojects.dndapp.persistance.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.inventory.CharacterHasItemSlotId;

@Repository
public interface InventoryJPARepo extends JpaRepository<CharacterHasItemSlot, CharacterHasItemSlotId>{



    @Query(value = "SELECT ic.item_uuid as itemUuid, " +
           "ic.item_name as itemName, " +
           "ic.item_weight as itemWeight, " +
           "ic.item_value as itemValue, " +
           "ic.item_rarity as itemRarity, " +
           "chis.quantity as quantity, " +
           "chis.equipped as equipped, " +
           "chis.attuned as attuned, " +
           "chis.in_attack_tab as inAttackTab, " +
           "chis.container_uuid as containerUuid " +
           "FROM character_has_item_slot chis " +
           "JOIN item_catalog ic ON chis.item_uuid = ic.item_uuid " +
           "WHERE chis.character_uuid = :characterUuid;", nativeQuery=true)
    List<CharacterHasItemProjection> getInventoryUsingUUID(@Param("characterUuid") UUID charUuid);

    @Query(value = "SELECT ic.item_uuid as itemUuid, " +
           "ic.item_name as itemName, " +
           "ic.item_weight as itemWeight, " +
           "ic.item_value as itemValue, " +
           "ic.item_rarity as itemRarity, " +
           "chis.quantity as quantity, " +
           "chis.equipped as equipped, " +
           "chis.attuned as attuned, " +
           "chis.in_attack_tab as inAttackTab, " +
           "chis.container_uuid as containerUuid " +
           "FROM character_has_item_slot chis " +
           "JOIN item_catalog ic ON chis.item_uuid = ic.item_uuid " +
           "WHERE chis.character_uuid = :characterUuid AND SIMILARITY(ic.item_name, :searchTerm) > 0.06 " + 
           "ORDER BY SIMILARITY(ic.item_name, :searchTerm) DESC", nativeQuery = true)
    List<CharacterHasItemProjection> getInventoyUsingFZF(@Param("characterUuid") UUID charUuid, @Param("searchTerm") String searchTerm);


    @Query(value = "SELECT * FROM character_has_item_slot as chis " +
                     "WHERE chis.character_uuid = :charUuid AND " + 
                     "chis.item_uuid = :itemUuid"
                     , nativeQuery = true)
    List<CharacterHasItemSlot> getListAnItemWithDifferntContainers(@Param("charUuid") UUID charUuid, @Param("itemUuid") UUID itemUuid); 
    
}
