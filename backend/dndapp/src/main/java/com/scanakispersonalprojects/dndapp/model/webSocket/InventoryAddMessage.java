package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * WebSocket message for adding items to inventory 
 */
public class InventoryAddMessage extends WebSocketMessage{
    
    // itemUuid - UUID of item to be added
    private UUID itemUuid;

    // quantity - quantity to be added
    private int quantity;

    /**
     * Default constructor for JSON deserialization.
     */
    public InventoryAddMessage () {
        super("INVENTORY_ADD", null);
    }

    /**
     * Creates a add inventory message
     * 
     * @param charUuid - uuid of the character reciving the item
     * @param itemUuid - uuid of the item to be added
     * @param quantity - quantity of itesm to be added
     */
    public InventoryAddMessage(UUID charUuid, UUID itemUuid, int quantity) {
        super("INVENTORY_ADD", charUuid);
        this.itemUuid = itemUuid;
        this.quantity = quantity;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
