package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public class ContainerRequestMessage extends WebSocketMessage {
    public ContainerRequestMessage() {
        super("CONTAINER_SEARCH_REQUEST", null);
    }
    public ContainerRequestMessage(UUID charUuid) {
        super("CONTAINER_SEARCH_REQUEST", charUuid);
    }
}
