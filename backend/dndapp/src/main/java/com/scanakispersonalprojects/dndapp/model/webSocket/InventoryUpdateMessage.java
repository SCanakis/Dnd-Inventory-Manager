package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemUpdate;

public class InventoryUpdateMessage extends WebSocketMessage {
    
    private UUID itemUuid;
    private UUID containerUuid;
    private CharacterHasItemUpdate update;

    public InventoryUpdateMessage() {
        super("INVENTORY_UPDATE", null);
    }

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
