package com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem;

import java.util.UUID;

public class CharacterHasItemUpdate {
    

    private UUID itemUuid;
    private Integer quantity = null;
    private Boolean equipped = null;
    private Boolean attuned = null;
    private UUID containerUuid = null;


    public CharacterHasItemUpdate() {}

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    public Boolean isAttuned() {
        return attuned;
    }

    public void setAttuned(boolean attuned) {
        this.attuned = attuned;
    }

    public UUID getContainerUuid() {
        return containerUuid;
    }

    public void setContainer(UUID containerUuid) {
        this.containerUuid = containerUuid;
    };
    
}
