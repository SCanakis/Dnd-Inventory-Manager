package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * WebSocket message for deleitn container from a character's inventory
 */
public class ContainerDeleteMessage extends WebSocketMessage{
    
    // The UUID of the container to be deleted
    private UUID containerUuid;

    /**
     * Defautl contructor for JSON deserilzation.
     */
    public ContainerDeleteMessage() {
        super("CONTAINER_DELETE" , null);
    }

    /**
     * Creates a dlete message for a specific container
     * 
     * @param charUuid
     * @param containerUuid
     */
    public ContainerDeleteMessage(UUID charUuid, UUID containerUuid) {
        super("CONTAINER_DELETE" , charUuid);
        this.containerUuid = containerUuid;
    }

    public UUID getContainerUuid() {
        return containerUuid;
    }

    public void setContainerUuid(UUID containerUuid) {
        this.containerUuid = containerUuid;
    }

    

}
