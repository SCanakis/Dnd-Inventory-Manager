package com.scanakispersonalprojects.dndapp.model.inventory;

import java.util.UUID;

public class ItemPreview {
    
    
    private UUID itemUuid;
    private String name;
    private int weight;
    private int value;
    private Rarity rarity;
    
    public ItemPreview(UUID itemUuid, String name, int weight, int value, Rarity rarity) {
        this.itemUuid = itemUuid;
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.rarity = rarity;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    

}
