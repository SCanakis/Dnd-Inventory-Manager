package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * WebSocket mesesage for deleting item from inventory
 */
public class InventoryDeleteMessage extends WebSocketMessage{
    
    // uuid of item to be deleted
    private UUID itemUuid;

    // uuid of container where item is located
    private UUID containerUuid;


    /**
     * Default constructor for JSON deserialization.
     */
    public InventoryDeleteMessage() {
        super("INVENTORY_DELETE", null);
    }

    /**
     * Creates a inventory delete message for a specific item in a specific container
     * 
     * @param charUuid - uuid of character inventory
     * @param itemUuid - uuid of item to be deleted
     * @param containerUuid - uuid of container where item is located 
     */
    public InventoryDeleteMessage( UUID charUuid, UUID itemUuid, UUID containerUuid) {
        super("INVENTORY_DELETE", charUuid);
        this.itemUuid = itemUuid;
        this.containerUuid = containerUuid;
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

}
