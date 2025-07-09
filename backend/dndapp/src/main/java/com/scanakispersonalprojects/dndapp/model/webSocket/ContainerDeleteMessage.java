package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public class ContainerDeleteMessage extends WebSocketMessage{
    
    private UUID containerUuid;

    public ContainerDeleteMessage() {
        super("CONTAINER_DELETE" , null);
    }

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
