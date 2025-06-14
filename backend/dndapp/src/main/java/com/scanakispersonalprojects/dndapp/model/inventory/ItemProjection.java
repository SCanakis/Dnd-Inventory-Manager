package com.scanakispersonalprojects.dndapp.model.inventory;

import java.util.UUID;

public interface ItemProjection {

    UUID getItemUuid();

    String getItemName();

    Integer getItemWeight();

    Integer getItemValue();

    Rarity getItemRarity();
    
}
