package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemUpdate;

/**
 * WebSocket message for updating inventory
 */
public class InventoryUpdateMessage extends WebSocketMessage {
    
    // uuid of item to be updated
    private UUID itemUuid;

    // uuid of container where item is located
    private UUID containerUuid;

    // DTO for updating character inventory item properites 
    private CharacterHasItemUpdate update;



    /**
     * Default constructor for JSON deserialization.
     */
    public InventoryUpdateMessage() {
        super("INVENTORY_UPDATE", null);
    }

    /**
     * Creats a inventory updates message
     * 
     * @param charUuid - uuid of character's inventory to be updated
     * @param itemUuid - item which update applies to 
     * @param containerUuid - uuid of container where the item it located
     * @param update - DTO for updating charcter inventory item properites
     */
    public InventoryUpdateMessage(UUID charUuid, UUID itemUuid, UUID containerUuid, CharacterHasItemUpdate update) {
        super("INVENTORY_UPDATE", charUuid);
        this.itemUuid = itemUuid;
        this.containerUuid = containerUuid;
        this.update = update;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public UUID getContainerUuid() {
        return containerUuid;
    }

    public void setContainerUuid(UUID containerUuid) {
        this.containerUuid = containerUuid;
    }

    public CharacterHasItemUpdate getUpdate() {
        return update;
    }

    public void setUpdate(CharacterHasItemUpdate update) {
        this.update = update;
    }


}
