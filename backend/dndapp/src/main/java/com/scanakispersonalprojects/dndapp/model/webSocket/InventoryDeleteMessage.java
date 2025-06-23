package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public class InventoryDeleteMessage extends WebSocketMessage{
    
    private UUID itemUuid;
    private UUID containerUuid;

    public InventoryDeleteMessage() {
        super("INVENTORY_DELETE", null);
    }

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
