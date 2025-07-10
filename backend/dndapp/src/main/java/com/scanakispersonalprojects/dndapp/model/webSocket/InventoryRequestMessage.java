package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * WebSocket message for subscribing to inventory
 */
public class InventoryRequestMessage extends WebSocketMessage{
    
    // uuid of container for filtering 
    private UUID containerUuid = null;

    // searchterm for filtering
    private String searchTerm = null;


    /**
     * Default constructor for JSON deserialization.
     */
    public InventoryRequestMessage() {
        super("INVENTORY_SEARCH_REQUEST", null);
    }

    /**
     * Creates a inventory request message with filters
     * 
     * @param charUuid - uuid character inventory 
     * @param containerUuid - container uuid for filtering
     * @param searchTerm - searchtemr for filtering
     */
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
