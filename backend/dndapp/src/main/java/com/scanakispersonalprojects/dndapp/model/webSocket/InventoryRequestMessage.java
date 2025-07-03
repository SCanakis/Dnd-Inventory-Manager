package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public class InventoryRequestMessage extends WebSocketMessage{
    
    private UUID containerUuid = null;
    private String searchTerm = null;

    public InventoryRequestMessage() {
        super("INVENTORY_SEARCH_REQUEST", null);
    }

    public InventoryRequestMessage(UUID charUuid, UUID containerUuid, String searchTerm) {
        super("INVENTORY_SEARCH_REQUEST", charUuid);
        this.containerUuid = containerUuid;
        this.searchTerm = searchTerm;
    }

    public UUID getContainerUuid() {
        return containerUuid;
    }

    public void setContainerUuid(UUID containerUuid) {
        this.containerUuid = containerUuid;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
