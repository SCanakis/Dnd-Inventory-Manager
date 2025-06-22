package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public class InventoryAddMessage extends WebSocketMessage{
    
    private UUID itemUuid;
    private int quantity;

    public InventoryAddMessage () {
        super("INVENTORY_ADD", null);
    }

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
