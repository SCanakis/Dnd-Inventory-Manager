package com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog;

import java.util.UUID;

/**
 * Projection interface for retrieving basic item catalog information.
 * Provides a lightweight view of items from the catalog without loading
 * the full ItemCatalog entity with all its complex properties and relationships.
 * 
 */
public interface ItemProjection {

    UUID getItemUuid();

    String getItemName();

    Integer getItemWeight();

    Integer getItemValue();

    Rarity getItemRarity();
    
}
