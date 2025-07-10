package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * WebSocket message for requesting all containers from a character
 */
public class ContainerRequestMessage extends WebSocketMessage {

    /**
     * Default constuctor for JSON deserialization.
     */
    public ContainerRequestMessage() {
        super("CONTAINER_SEARCH_REQUEST", null);
    }

    /**
     * Creates a container request message for a specific character 
     * 
     * @param charUuid
     */
    public ContainerRequestMessage(UUID charUuid) {
        super("CONTAINER_SEARCH_REQUEST", charUuid);
    }
}
