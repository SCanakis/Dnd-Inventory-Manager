package com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Rarity;

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
