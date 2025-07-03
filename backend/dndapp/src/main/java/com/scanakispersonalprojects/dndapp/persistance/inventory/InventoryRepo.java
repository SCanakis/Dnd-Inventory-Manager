package com.scanakispersonalprojects.dndapp.persistance.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemProjection;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlot;
import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemSlotId;

/**
 * Repository interface for character inventory operations.
 * Provides database access methods for managing items in character inventories,
 * including retrieval, searching, and container-specific operations.
 */
@Repository
public interface InventoryRepo extends JpaRepository<CharacterHasItemSlot, CharacterHasItemSlotId>{


       /**
        * Retrieves the complete inventory for a specific character.
        * Returns detailed information about each item including catalog details
        * and character-specific properties like quantity and equipped status.
        *
        * @param charUuid the unique identifier of the character
        * @return list of character inventory item projections, empty list if no items found
        */

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


       /**
        * Searches a character's inventory using fuzzy string matching.
        * Uses PostgreSQL's SIMILARITY function to find items with names similar
        * to the search term, with a minimum similarity threshold of 0.06.
        * Results are ordered by similarity score (most similar first).
        *
        * @param charUuid the unique identifier of the character
        * @param searchTerm the text to search for in item names
        * @return list of matching inventory items ordered by similarity, empty list if no matches
        */
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

       /**
        * Searches a character's inventory using fuzzy string matching.
        * Uses PostgreSQL's SIMILARITY function to find items with names similar
        * to the search term, with a minimum similarity threshold of 0.06.
        * Results are ordered by similarity score (most similar first).
        *
        * @param charUuid the unique identifier of the character
        * @param containerUuid the unque identifier within a container
        * @param searchTerm the text to search for in item names
        * @return list of matching inventory items ordered by similarity, empty list if no matches
        */
       @Query(value =  "SELECT ic.item_uuid as itemUuid, " +
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
              "AND chis.container_uuid = :containerUuid " + 
              "ORDER BY SIMILARITY(ic.item_name, :searchTerm) DESC", nativeQuery = true)

       List<CharacterHasItemProjection> getItemsInContainerUsingFZF(@Param("characterUuid") UUID charUuid, @Param ("containerUuid") UUID containeUuid, @Param("searchTerm") String searchTerm); 

       /**
        * Retrieves all instances of a specific item owned by a character.
        * This includes the same item stored in different containers, allowing
        * for management of item distribution across multiple storage locations.
        *
        * @param charUuid the unique identifier of the character
        * @param itemUuid the unique identifier of the item to find
        * @return list of character item slots for the specified item, empty list if item not found
        */
       @Query(value = "SELECT * FROM character_has_item_slot as chis " +
                            "WHERE chis.character_uuid = :charUuid AND " + 
                            "chis.item_uuid = :itemUuid"
                            , nativeQuery = true)
       List<CharacterHasItemSlot> getSameItemDifferentContainers(@Param("charUuid") UUID charUuid, @Param("itemUuid") UUID itemUuid); 
       

       /**
        * Retrieves all items stored in a specific container for a character.
       * Useful for displaying container contents and managing container-specific
       * inventory operations.
       *
       * @param charUuid the unique identifier of the character who owns the container
       * @param containUuid the unique identifier of the container
       * @return list of items stored in the specified container, empty list if container is empty
       */
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
              "WHERE chis.character_uuid = :characterUuid " + 
              "AND chis.container_uuid = :containerUuid;" 
              , nativeQuery=true)       

       List<CharacterHasItemProjection> getItemsForAContainer(@Param ("characterUuid") UUID charUuid, @Param("containerUuid") UUID containerUuid);

}
