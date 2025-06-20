package com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Rarity;

/**
 * Projection interface for retrieving character inventory item data.
 * Provides a read-only view of items in a character's inventory,
 * combining item catalog information with character-specific details
 * like quantity, equipped status, and container location.
 * 
 */
public interface CharacterHasItemProjection {

    UUID getItemUuid();

    String getItemName();

    Integer getItemWeight();

    Integer getItemValue();

    Rarity getItemRarity();

    Integer getQuantity();
    
    Boolean getEquipped();
    
    Boolean getAttuned();
    
    UUID getContainerUuid();

}
